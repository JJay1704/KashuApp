-- Deployment script for an ISOLATED development database. Do not apply to main.
-- Baseline: KashuApp public tables inspected on 2026-10-29.
begin;

alter table public.budget add column if not exists currency varchar not null default 'PEN'
    references public.currency(code);
insert into public.account_type(id, name, classification, icon_name)
values ('deuda', 'Deuda / préstamo', 'pasivo', 'AccountBalance') on conflict (id) do nothing;
create unique index if not exists budget_category_period_currency_uq
on public.budget(user_id, category_id, year, month, currency);

create table if not exists public.goal_contribution (
    id uuid primary key,
    user_id uuid not null references auth.users(id),
    goal_id uuid not null references public.goal(id) on delete cascade,
    account_id uuid not null references public.account(id) on delete restrict,
    amount numeric(16,2) not null check (amount > 0),
    created_at timestamptz not null default now()
);
alter table public.goal_contribution enable row level security;
drop policy if exists contribution_owner_read on public.goal_contribution;
create policy contribution_owner_read on public.goal_contribution for select to authenticated
using ((select auth.uid()) = user_id);
drop policy if exists contribution_owner_insert on public.goal_contribution;
create policy contribution_owner_insert on public.goal_contribution for insert to authenticated
with check ((select auth.uid()) = user_id);
grant select, insert on public.goal_contribution to authenticated;
revoke update, delete on public.goal_contribution from authenticated, anon;
create index if not exists contribution_goal_idx on public.goal_contribution(goal_id);
create index if not exists contribution_account_idx on public.goal_contribution(account_id);

-- The ledger trigger validates and applies each transfer atomically under RLS.
create or replace function public.apply_goal_contribution()
returns trigger language plpgsql security invoker set search_path = '' as $$
declare g public.goal%rowtype; a public.account%rowtype; kind text;
begin
    if new.amount::text in ('NaN','Infinity','-Infinity') or new.amount <= 0 or new.amount > 999999999999.99 then
        raise exception 'El monto no es válido';
    end if;
    select * into g from public.goal where id = new.goal_id and user_id = auth.uid() for update;
    if not found or new.user_id is distinct from auth.uid() then raise exception 'Meta no disponible'; end if;
    select * into a from public.account where id = new.account_id and user_id = auth.uid() for update;
    if not found then raise exception 'Cuenta no disponible'; end if;
    select classification into kind from public.account_type where id = a.type;
    if not a.is_active or kind is distinct from 'activo' or a.currency <> g.currency then
        raise exception 'La cuenta debe ser un activo vigente de la misma moneda';
    end if;
    if a.current_balance < new.amount then raise exception 'Saldo insuficiente'; end if;
    update public.account set current_balance = current_balance - new.amount where id = a.id;
    update public.goal set current_amount = current_amount + new.amount where id = g.id;
    return new;
end;
$$;
drop trigger if exists apply_goal_contribution on public.goal_contribution;
create trigger apply_goal_contribution before insert on public.goal_contribution
for each row execute function public.apply_goal_contribution();

-- The insert trigger owns accounting; RPC locks and handles idempotent retries only.
create or replace function public.contribute_to_goal(
    p_goal_id uuid, p_account_id uuid, p_amount numeric, p_request_id uuid
) returns void language plpgsql security invoker set search_path = '' as $$
declare existing public.goal_contribution%rowtype;
begin
    if auth.uid() is null then raise exception 'Sesión no válida'; end if;
    if p_amount is null or p_amount <= 0 or p_amount::text in ('NaN','Infinity','-Infinity')
       or p_amount <> round(p_amount, 2) or p_amount > 999999999999.99 then
        raise exception 'El aporte debe ser positivo y tener como máximo dos decimales';
    end if;
    perform 1 from public.goal where id = p_goal_id and user_id = auth.uid() for update;
    if not found then raise exception 'Meta no disponible'; end if;
    select * into existing from public.goal_contribution where id = p_request_id;
    if found then
        if existing.user_id <> auth.uid() or existing.goal_id <> p_goal_id
           or existing.account_id <> p_account_id or existing.amount <> p_amount then
            raise exception 'El identificador del aporte ya fue utilizado';
        end if;
        return;
    end if;
    insert into public.goal_contribution(id, user_id, goal_id, account_id, amount)
    values (p_request_id, auth.uid(), p_goal_id, p_account_id, p_amount);
end;
$$;

create or replace function public.refund_goal_contributions()
returns trigger language plpgsql security invoker set search_path = '' as $$
declare contribution record;
begin
    if old.user_id is distinct from auth.uid() then raise exception 'Meta no disponible'; end if;
    -- The goal row is locked by DELETE; account order is stable to avoid deadlocks.
    for contribution in select account_id, sum(amount) amount from public.goal_contribution
        where goal_id = old.id group by account_id order by account_id loop
        update public.account set current_balance = current_balance + contribution.amount
            where id = contribution.account_id and user_id = auth.uid();
        if not found then raise exception 'No se pudo devolver el aporte'; end if;
    end loop;
    return old;
end;
$$;
drop trigger if exists refund_goal_contributions on public.goal;
create trigger refund_goal_contributions before delete on public.goal
for each row execute function public.refund_goal_contributions();
create or replace function public.delete_goal_with_refund(p_goal_id uuid)
returns void language plpgsql security invoker set search_path = '' as $$
begin
    delete from public.goal where id = p_goal_id and user_id = auth.uid();
    if not found then raise exception 'Meta no disponible'; end if;
end;
$$;

create or replace function public.protect_account_history()
returns trigger language plpgsql security invoker set search_path = '' as $$
begin
    if (new.currency <> old.currency or new.type <> old.type) and (
        exists(select 1 from public.transaction where account_id = old.id) or
        exists(select 1 from public.goal_contribution where account_id = old.id)) then
        raise exception 'No puedes cambiar moneda o tipo de una cuenta con movimientos';
    end if;
    if new.initial_balance < 0 then raise exception 'El saldo inicial no puede ser negativo'; end if;
    if new.initial_balance <> old.initial_balance then
        new.current_balance := old.current_balance + new.initial_balance - old.initial_balance;
    end if;
    if new.current_balance < 0 and exists(select 1 from public.account_type where id = new.type and classification = 'pasivo') then
        raise exception 'El movimiento dejaría una deuda negativa';
    end if;
    new.updated_at := now();
    return new;
end;
$$;
drop trigger if exists protect_account_history on public.account;
create trigger protect_account_history before update on public.account
for each row execute function public.protect_account_history();

create or replace function public.validate_transaction_links()
returns trigger language plpgsql security invoker set search_path = '' as $$
declare a public.account%rowtype; category_type text;
begin
    select * into a from public.account where id = new.account_id and user_id = auth.uid() for update;
    if not found or new.user_id is distinct from auth.uid() then raise exception 'Cuenta no disponible'; end if;
    if not a.is_active then raise exception 'La cuenta está desactivada'; end if;
    select type into category_type from public.category where id = new.category_id and (is_default or user_id = auth.uid());
    if not found or category_type <> new.type then raise exception 'La categoría no corresponde al movimiento'; end if;
    return new;
end;
$$;
drop trigger if exists validate_transaction_links on public.transaction;
create trigger validate_transaction_links before insert or update on public.transaction
for each row execute function public.validate_transaction_links();

-- Expense increases outstanding debt; income pays it down. Asset semantics stay unchanged.
create or replace function public.update_account_balance()
returns trigger language plpgsql security invoker set search_path = '' as $$
declare sign_old integer; sign_new integer;
begin
    if tg_op in ('DELETE', 'UPDATE') then
        select case when t.classification = 'pasivo' then -1 else 1 end into sign_old
        from public.account a join public.account_type t on t.id = a.type where a.id = old.account_id;
        update public.account set current_balance = current_balance -
            (case when old.type = 'INCOME' then old.amount else -old.amount end) * sign_old
            where id = old.account_id;
    end if;
    if tg_op in ('INSERT', 'UPDATE') then
        select case when t.classification = 'pasivo' then -1 else 1 end into sign_new
        from public.account a join public.account_type t on t.id = a.type where a.id = new.account_id;
        update public.account set current_balance = current_balance +
            (case when new.type = 'INCOME' then new.amount else -new.amount end) * sign_new
            where id = new.account_id;
    end if;
    if tg_op = 'DELETE' then return old; else return new; end if;
end;
$$;

revoke all on function public.contribute_to_goal(uuid,uuid,numeric,uuid) from public, anon;
revoke all on function public.delete_goal_with_refund(uuid) from public, anon;
grant execute on function public.contribute_to_goal(uuid,uuid,numeric,uuid) to authenticated;
grant execute on function public.delete_goal_with_refund(uuid) to authenticated;

commit;

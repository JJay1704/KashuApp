# Documentación de Base de Datos y Supabase — KashuApp

Este documento describe la arquitectura de datos, el esquema relacional de tablas, las políticas de seguridad (Row Level Security) y la lógica de consistencia transaccional (Triggers ACID) implementadas en Supabase (PostgreSQL) para **KashuApp**.

---

## 1. Arquitectura de Datos y Principios de Integridad

- **Motor de Base de Datos:** PostgreSQL en Supabase.
- **Seguridad:** Row Level Security (RLS) activado en todas las tablas para garantizar que cada usuario únicamente pueda consultar y manipular sus propios registros mediante `auth.uid() = user_id`.
- **Consistencia Transaccional (ACID):** El balance actual de cada cuenta (`current_balance` en la tabla `account`) **no se calcula manualmente en el cliente Android**. Se actualiza atómicamente a través de un **Trigger en PostgreSQL** ante eventos de `INSERT`, `UPDATE` o `DELETE` en la tabla `transaction`. Esto previene condiciones de carrera (*race conditions*) e inconsistencias en la app.

---

## 2. Esquema Relacional de Tablas

### 2.1. `account` (Cuentas Financieras)
Representa las cuentas del usuario (bancos, billeteras digitales, efectivo, tarjetas).

| Columna | Tipo SQL | Restricciones / Defecto | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | `uuid` | `PRIMARY KEY`, `DEFAULT gen_random_uuid()` | Identificador único de la cuenta. |
| `user_id` | `uuid` | `REFERENCES auth.users(id) ON DELETE CASCADE` | Propietario de la cuenta. |
| `name` | `text` | `NOT NULL` | Nombre descriptivo (ej. "BCP Sueldo", "Efectivo"). |
| `type` | `text` | `NOT NULL`, `DEFAULT 'Banco'` | Tipo de cuenta ("Banco", "Efectivo", "Billetera", "Tarjeta"). |
| `initial_balance` | `numeric(12,2)` | `NOT NULL`, `DEFAULT 0.00` | Saldo con el que se dio de alta la cuenta. |
| `current_balance` | `numeric(12,2)` | `NOT NULL`, `DEFAULT 0.00` | Saldo actual calculado automáticamente por Trigger. |
| `currency` | `text` | `NOT NULL`, `DEFAULT 'PEN'` | Código de divisa ISO 4217 ("PEN", "USD"). |
| `color` | `text` | `DEFAULT '#34D399'` | Color hexadecimal para la interfaz en Android. |
| `icon` | `text` | `DEFAULT 'account_balance'` | Nombre del icono Material en Compose. |
| `is_active` | `boolean` | `NOT NULL`, `DEFAULT true` | Estado activo/inactivo (las inactivas se excluyen del balance). |
| `created_at` | `timestamptz` | `DEFAULT now()` | Marca temporal de creación. |
| `updated_at` | `timestamptz` | `DEFAULT now()` | Marca temporal de última actualización. |

---

### 2.2. `category` (Categorías de Transacción)
Clasificación para gastos e ingresos. Admite categorías por defecto del sistema y personalizadas por usuario.

| Columna | Tipo SQL | Restricciones / Defecto | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | `uuid` | `PRIMARY KEY`, `DEFAULT gen_random_uuid()` | Identificador de la categoría. |
| `user_id` | `uuid` | `REFERENCES auth.users(id) ON DELETE CASCADE`, `NULLABLE` | Si es `NULL`, aplica como categoría predeterminada global. |
| `name` | `text` | `NOT NULL` | Nombre (ej. "Alimentación", "Transporte", "Sueldo"). |
| `type` | `text` | `NOT NULL` | Tipo de movimiento: `'INCOME'` o `'EXPENSE'`. |
| `icon` | `text` | `NOT NULL`, `DEFAULT 'category'` | Identificador del icono para Compose. |
| `color` | `text` | `NOT NULL`, `DEFAULT '#34D399'` | Color distintivo hexadecimal. |
| `is_default` | `boolean` | `NOT NULL`, `DEFAULT false` | Indica si es categoría preconfigurada del sistema. |
| `created_at` | `timestamptz` | `DEFAULT now()` | Marca temporal de creación. |

---

### 2.3. `transaction` (Movimientos Financieros)
Almacena cada ingreso o gasto registrado manualmente.

| Columna | Tipo SQL | Restricciones / Defecto | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | `uuid` | `PRIMARY KEY`, `DEFAULT gen_random_uuid()` | Identificador del movimiento. |
| `user_id` | `uuid` | `REFERENCES auth.users(id) ON DELETE CASCADE` | Usuario que realizó el movimiento. |
| `account_id` | `uuid` | `NOT NULL`, `REFERENCES account(id) ON DELETE RESTRICT` | Cuenta afectada. |
| `category_id` | `uuid` | `NOT NULL`, `REFERENCES category(id) ON DELETE RESTRICT` | Categoría asignada. |
| `amount` | `numeric(12,2)` | `NOT NULL`, `CHECK (amount > 0)` | Monto del movimiento (siempre mayor a cero). |
| `type` | `text` | `NOT NULL` | `'INCOME'` (Ingreso) o `'EXPENSE'` (Gasto). |
| `description` | `text` | `NULLABLE` | Detalle o nota opcional. |
| `transaction_date` | `date` | `NOT NULL`, `DEFAULT CURRENT_DATE` | Fecha contable de la operación. |
| `created_at` | `timestamptz` | `DEFAULT now()` | Marca temporal de registro. |
| `updated_at` | `timestamptz` | `DEFAULT now()` | Marca temporal de edición. |

---

### 2.4. `goal` (Metas de Ahorro)
Permite a los usuarios trazar objetivos financieros con fecha límite y seguimiento de avance.

| Columna | Tipo SQL | Restricciones / Defecto | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | `uuid` | `PRIMARY KEY`, `DEFAULT gen_random_uuid()` | Identificador de la meta. |
| `user_id` | `uuid` | `REFERENCES auth.users(id) ON DELETE CASCADE` | Propietario de la meta. |
| `account_id` | `uuid` | `NULLABLE`, `REFERENCES account(id) ON DELETE SET NULL` | Cuenta de ahorro vinculada. |
| `category_id` | `uuid` | `NULLABLE`, `REFERENCES category(id) ON DELETE SET NULL` | Categoría vinculada. |
| `title` | `text` | `NOT NULL` | Nombre o título de la meta (ej. "Fondo de Emergencia"). |
| `target_amount` | `numeric(12,2)` | `NOT NULL`, `CHECK (target_amount > 0)` | Monto objetivo a alcanzar. |
| `current_amount` | `numeric(12,2)` | `NOT NULL`, `DEFAULT 0.00` | Monto acumulado al momento. |
| `currency` | `text` | `NOT NULL`, `DEFAULT 'PEN'` | Divisa de la meta. |
| `deadline` | `date` | `NOT NULL` | Fecha límite para completar el objetivo. |
| `color` | `text` | `DEFAULT '#34D399'` | Color distintivo de la meta. |
| `icon` | `text` | `DEFAULT 'savings'` | Icono visual en la tarjeta. |
| `is_completed` | `boolean` | `NOT NULL`, `DEFAULT false` | Bandera de cumplimiento. |
| `created_at` | `timestamptz` | `DEFAULT now()` | Marca temporal de creación. |
| `updated_at` | `timestamptz` | `DEFAULT now()` | Marca temporal de actualización. |

---

### 2.5. `budget` (Presupuestos Mensuales)
Establece límites de gasto por categoría para un período mensual.

| Columna | Tipo SQL | Restricciones / Defecto | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | `uuid` | `PRIMARY KEY`, `DEFAULT gen_random_uuid()` | Identificador del presupuesto. |
| `user_id` | `uuid` | `REFERENCES auth.users(id) ON DELETE CASCADE` | Propietario del presupuesto. |
| `category_id` | `uuid` | `NOT NULL`, `REFERENCES category(id) ON DELETE RESTRICT` | Categoría con límite. |
| `amount_limit` | `numeric(12,2)` | `NOT NULL`, `CHECK (amount_limit > 0)` | Tope monetario mensual asignado. |
| `month` | `integer` | `NOT NULL`, `CHECK (month BETWEEN 1 AND 12)` | Mes calendario (1-12). |
| `year` | `integer` | `NOT NULL` | Año calendario (ej. 2026). |
| `created_at` | `timestamptz` | `DEFAULT now()` | Marca temporal de creación. |
| `updated_at` | `timestamptz` | `DEFAULT now()` | Marca temporal de actualización. |

---

## 3. Triggers y Funciones de PostgreSQL (Garantía ACID)

Para cumplir con el principio de integridad y evitar cálculos manuales o desincronizados en la aplicación móvil, Supabase ejecuta una función de recálculo disparada por eventos en la tabla `transaction`:

```sql
-- Función para sincronizar current_balance en account
CREATE OR REPLACE FUNCTION update_account_balance()
RETURNS TRIGGER AS $$
BEGIN
    -- Caso 1: Nueva transacción insertada
    IF (TG_OP = 'INSERT') THEN
        IF NEW.type = 'INCOME' THEN
            UPDATE account 
            SET current_balance = current_balance + NEW.amount, updated_at = now() 
            WHERE id = NEW.account_id;
        ELSIF NEW.type = 'EXPENSE' THEN
            UPDATE account 
            SET current_balance = current_balance - NEW.amount, updated_at = now() 
            WHERE id = NEW.account_id;
        END IF;

    -- Caso 2: Transacción eliminada
    ELSIF (TG_OP = 'DELETE') THEN
        IF OLD.type = 'INCOME' THEN
            UPDATE account 
            SET current_balance = current_balance - OLD.amount, updated_at = now() 
            WHERE id = OLD.account_id;
        ELSIF OLD.type = 'EXPENSE' THEN
            UPDATE account 
            SET current_balance = current_balance + OLD.amount, updated_at = now() 
            WHERE id = OLD.account_id;
        END IF;

    -- Caso 3: Transacción editada
    ELSIF (TG_OP = 'UPDATE') THEN
        -- Revertir impacto anterior
        IF OLD.type = 'INCOME' THEN
            UPDATE account SET current_balance = current_balance - OLD.amount WHERE id = OLD.account_id;
        ELSIF OLD.type = 'EXPENSE' THEN
            UPDATE account SET current_balance = current_balance + OLD.amount WHERE id = OLD.account_id;
        END IF;

        -- Aplicar nuevo impacto
        IF NEW.type = 'INCOME' THEN
            UPDATE account SET current_balance = current_balance + NEW.amount, updated_at = now() WHERE id = NEW.account_id;
        ELSIF NEW.type = 'EXPENSE' THEN
            UPDATE account SET current_balance = current_balance - NEW.amount, updated_at = now() WHERE id = NEW.account_id;
        END IF;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Disparador asociado a la tabla transaction
CREATE TRIGGER trg_update_account_balance
AFTER INSERT OR UPDATE OR DELETE ON transaction
FOR EACH ROW EXECUTE FUNCTION update_account_balance();
```

---

## 4. Políticas de Seguridad (Row Level Security - RLS)

Todas las tablas de KashuApp aplican RLS estricto para aislamiento multi-inquilino (*multi-tenant*):

```sql
-- Habilitar RLS en cada tabla
ALTER TABLE account ENABLE ROW LEVEL SECURITY;
ALTER TABLE category ENABLE ROW LEVEL SECURITY;
ALTER TABLE transaction ENABLE ROW LEVEL SECURITY;
ALTER TABLE goal ENABLE ROW LEVEL SECURITY;
ALTER TABLE budget ENABLE ROW LEVEL SECURITY;

-- Política de ejemplo para account (igual criterio en transaction, goal y budget)
CREATE POLICY "Users can only access their own accounts" 
ON account 
FOR ALL 
TO authenticated 
USING (auth.uid() = user_id) 
WITH CHECK (auth.uid() = user_id);

-- En category, se permite leer categorías por defecto del sistema (user_id IS NULL)
CREATE POLICY "Users can view default or own categories"
ON category
FOR SELECT
TO authenticated
USING (user_id IS NULL OR auth.uid() = user_id);
```

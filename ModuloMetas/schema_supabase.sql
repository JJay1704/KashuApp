-- ==============================================================================
-- KashuApp - Módulo de Metas de Ahorro (HU-07 / RF-07)
-- Script DDL para Supabase (PostgreSQL + RLS + Triggers)
-- ==============================================================================

-- 1. Creación de la tabla 'goal'
CREATE TABLE IF NOT EXISTS public.goal (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    account_id UUID REFERENCES public.account(id) ON DELETE SET NULL,     -- Opcional (cuenta asociada)
    category_id UUID REFERENCES public.category(id) ON DELETE SET NULL,   -- Opcional (categoría asociada)
    title VARCHAR(150) NOT NULL,
    target_amount NUMERIC(12, 2) NOT NULL CHECK (target_amount > 0),
    current_amount NUMERIC(12, 2) NOT NULL DEFAULT 0.00 CHECK (current_amount >= 0),
    currency VARCHAR(10) NOT NULL DEFAULT 'PEN',                         -- Moneda (PEN, USD, etc.)
    deadline DATE NOT NULL,
    color VARCHAR(20) DEFAULT '#34D399',
    icon VARCHAR(50) DEFAULT 'savings',
    is_completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 2. Habilitar Row Level Security (RLS) para proteger los datos
ALTER TABLE public.goal ENABLE ROW LEVEL SECURITY;

-- 3. Políticas de Seguridad (RLS): cada usuario solo accede a sus propias metas
CREATE POLICY "Los usuarios pueden ver únicamente sus propias metas"
    ON public.goal FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Los usuarios pueden crear sus propias metas"
    ON public.goal FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Los usuarios pueden actualizar sus propias metas"
    ON public.goal FOR UPDATE
    USING (auth.uid() = user_id);

CREATE POLICY "Los usuarios pueden eliminar sus propias metas"
    ON public.goal FOR DELETE
    USING (auth.uid() = user_id);

-- 4. Trigger automático: actualiza 'updated_at' y marca 'is_completed' si alcanza la meta
CREATE OR REPLACE FUNCTION public.handle_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = timezone('utc'::text, now());
    IF NEW.current_amount >= NEW.target_amount THEN
        NEW.is_completed = TRUE;
    ELSE
        NEW.is_completed = FALSE;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_update_goal_timestamp ON public.goal;
CREATE TRIGGER trigger_update_goal_timestamp
    BEFORE UPDATE ON public.goal
    FOR EACH ROW
    EXECUTE FUNCTION public.handle_updated_at();

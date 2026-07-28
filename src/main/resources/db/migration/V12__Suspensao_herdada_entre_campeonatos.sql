-- Suspensões herdadas de campeonato anterior (já geradas, ainda não cumpridas)
ALTER TABLE campeonato_suspensao
    ADD COLUMN IF NOT EXISTS "campeonatoSuspensaoHerdada" BOOLEAN NOT NULL DEFAULT FALSE;

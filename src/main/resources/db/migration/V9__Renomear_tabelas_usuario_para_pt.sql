-- Módulo 7.1 — Padronização dos nomes físicos das tabelas (PT + snake_case).
-- Preserva dados: apenas RENAME das tabelas criadas em inglês no V7.
-- FKs e índices permanecem vinculados à tabela após o rename no PostgreSQL/H2.

ALTER TABLE IF EXISTS user_statistics RENAME TO estatisticas_usuario;
ALTER TABLE IF EXISTS user_club_usage RENAME TO uso_clube_usuario;
ALTER TABLE IF EXISTS user_rivalry RENAME TO rivalidade_usuario;
ALTER TABLE IF EXISTS head_to_head_match RENAME TO historico_confronto_usuario;

-- Colunas de rivalidade (caso V8 não tenha rodado / ddl-auto ainda não tenha criado)
ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS "campeonatosDisputados" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS "titulosUsuarioA" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS "titulosUsuarioB" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS "maiorGoleadaGolsA" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS "maiorGoleadaGolsB" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS "maiorGoleadaMargem" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS maior_goleada_vencedor_user_id BIGINT;

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS "maiorGoleadaCampeonatoNome" VARCHAR(120);

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS "maiorSequenciaVitoriasA" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS "maiorSequenciaVitoriasB" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS "sequenciaAtual" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS sequencia_atual_user_id BIGINT;

ALTER TABLE historico_confronto_usuario
    ADD COLUMN IF NOT EXISTS "maiorGoleadaGolsA" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE historico_confronto_usuario
    ADD COLUMN IF NOT EXISTS "maiorGoleadaGolsB" INTEGER NOT NULL DEFAULT 0;

-- Módulo 7.1 (documental): colunas agregadas de RivalidadeUsuario / HistoricoConfrontoUsuario.
-- Schema efetivo via spring.jpa.hibernate.ddl-auto=update (+ migrator de nomenclatura).
-- Nomes físicos finais das tabelas: ver V9 (estatisticas_usuario, rivalidade_usuario, etc.).
-- Este script mantém referência aos nomes antigos (V7) para ambientes que ainda não rodaram o V9.

ALTER TABLE user_rivalry
    ADD COLUMN IF NOT EXISTS "campeonatosDisputados" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE user_rivalry
    ADD COLUMN IF NOT EXISTS "titulosUsuarioA" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE user_rivalry
    ADD COLUMN IF NOT EXISTS "titulosUsuarioB" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE user_rivalry
    ADD COLUMN IF NOT EXISTS "maiorGoleadaGolsA" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE user_rivalry
    ADD COLUMN IF NOT EXISTS "maiorGoleadaGolsB" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE user_rivalry
    ADD COLUMN IF NOT EXISTS "maiorGoleadaMargem" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE user_rivalry
    ADD COLUMN IF NOT EXISTS maior_goleada_vencedor_user_id BIGINT;

ALTER TABLE user_rivalry
    ADD COLUMN IF NOT EXISTS "maiorGoleadaCampeonatoNome" VARCHAR(120);

ALTER TABLE user_rivalry
    ADD COLUMN IF NOT EXISTS "maiorSequenciaVitoriasA" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE user_rivalry
    ADD COLUMN IF NOT EXISTS "maiorSequenciaVitoriasB" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE user_rivalry
    ADD COLUMN IF NOT EXISTS "sequenciaAtual" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE user_rivalry
    ADD COLUMN IF NOT EXISTS sequencia_atual_user_id BIGINT;

ALTER TABLE head_to_head_match
    ADD COLUMN IF NOT EXISTS "maiorGoleadaGolsA" INTEGER NOT NULL DEFAULT 0;

ALTER TABLE head_to_head_match
    ADD COLUMN IF NOT EXISTS "maiorGoleadaGolsB" INTEGER NOT NULL DEFAULT 0;

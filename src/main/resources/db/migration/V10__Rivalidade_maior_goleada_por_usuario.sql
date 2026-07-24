-- Prompt 7.2: maior goleada por competidor (snake_case explícito para o naming do Hibernate).

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS maior_goleada_a_gols_pro INTEGER NOT NULL DEFAULT 0;

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS maior_goleada_a_gols_contra INTEGER NOT NULL DEFAULT 0;

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS maior_goleada_a_margem INTEGER NOT NULL DEFAULT 0;

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS maior_goleada_a_clube_pro VARCHAR(120);

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS maior_goleada_a_clube_contra VARCHAR(120);

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS maior_goleada_a_campeonato_nome VARCHAR(120);

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS maior_goleada_b_gols_pro INTEGER NOT NULL DEFAULT 0;

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS maior_goleada_b_gols_contra INTEGER NOT NULL DEFAULT 0;

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS maior_goleada_b_margem INTEGER NOT NULL DEFAULT 0;

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS maior_goleada_b_clube_pro VARCHAR(120);

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS maior_goleada_b_clube_contra VARCHAR(120);

ALTER TABLE rivalidade_usuario
    ADD COLUMN IF NOT EXISTS maior_goleada_b_campeonato_nome VARCHAR(120);

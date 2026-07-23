-- Extrai desempenho esportivo do clube para EstatisticaClube (1:1).
-- Nomes snake_case (padrão Hibernate deste projeto).
-- Ambiente em desenvolvimento: script idempotente.
-- A cópia/normalização também roda no startup (EstatisticaClubeDataMigrator).

CREATE TABLE IF NOT EXISTS estatisticas_clube (
    id BIGSERIAL PRIMARY KEY,
    clube_id BIGINT NOT NULL,
    clube_rank VARCHAR(8),
    clube_gols_pro INTEGER DEFAULT 0,
    clube_gols_contra INTEGER DEFAULT 0,
    clube_vitorias INTEGER DEFAULT 0,
    clube_empates INTEGER DEFAULT 0,
    clube_derrotas INTEGER DEFAULT 0,
    clube_titulos INTEGER DEFAULT 0,
    CONSTRAINT uk_estatisticas_clube_clube UNIQUE (clube_id),
    CONSTRAINT fk_estatisticas_clube_clube FOREIGN KEY (clube_id) REFERENCES clube (clube_id)
);

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'clube'
          AND column_name = 'clube_gols_pro'
    ) THEN
        INSERT INTO estatisticas_clube (
            clube_id, clube_rank, clube_gols_pro, clube_gols_contra,
            clube_vitorias, clube_empates, clube_derrotas, clube_titulos
        )
        SELECT
            c.clube_id,
            c.clube_rank,
            COALESCE(c.clube_gols_pro, 0),
            COALESCE(c.clube_gols_contra, 0),
            COALESCE(c.clube_vitorias, 0),
            COALESCE(c.clube_empates, 0),
            COALESCE(c.clube_derrotas, 0),
            COALESCE(c.clube_titulos, 0)
        FROM clube c
        WHERE NOT EXISTS (
            SELECT 1 FROM estatisticas_clube e WHERE e.clube_id = c.clube_id
        );
    ELSIF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'clube'
          AND column_name = 'clube_rank'
    ) THEN
        INSERT INTO estatisticas_clube (
            clube_id, clube_rank, clube_gols_pro, clube_gols_contra,
            clube_vitorias, clube_empates, clube_derrotas, clube_titulos
        )
        SELECT c.clube_id, c.clube_rank, 0, 0, 0, 0, 0, 0
        FROM clube c
        WHERE NOT EXISTS (
            SELECT 1 FROM estatisticas_clube e WHERE e.clube_id = c.clube_id
        );
    END IF;
END $$;

INSERT INTO estatisticas_clube (
    clube_id, clube_gols_pro, clube_gols_contra,
    clube_vitorias, clube_empates, clube_derrotas, clube_titulos
)
SELECT c.clube_id, 0, 0, 0, 0, 0, 0
FROM clube c
WHERE NOT EXISTS (
    SELECT 1 FROM estatisticas_clube e WHERE e.clube_id = c.clube_id
);

CREATE OR REPLACE FUNCTION normalize_club_rank(val TEXT) RETURNS TEXT AS $$
DECLARE
    cleaned TEXT;
    last_token TEXT;
BEGIN
    IF val IS NULL OR TRIM(val) = '' THEN
        RETURN NULL;
    END IF;
    cleaned := UPPER(TRIM(val));
    cleaned := REPLACE(cleaned, 'RANK_', '');
    cleaned := REPLACE(cleaned, 'RANK ', '');
    cleaned := REPLACE(cleaned, ' ', '');
    IF POSITION('/' IN cleaned) > 0 THEN
        last_token := split_part(cleaned, '/', array_length(string_to_array(cleaned, '/'), 1));
    ELSE
        last_token := cleaned;
    END IF;
    IF last_token IN ('S','A','B','C','D','E') THEN
        RETURN last_token;
    END IF;
    IF RIGHT(last_token, 1) IN ('S','A','B','C','D','E') THEN
        RETURN RIGHT(last_token, 1);
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

UPDATE estatisticas_clube
SET clube_rank = normalize_club_rank(clube_rank)
WHERE clube_rank IS NOT NULL;

UPDATE campeonato_clube
SET campeonato_clube_rank = normalize_club_rank(campeonato_clube_rank)
WHERE campeonato_clube_rank IS NOT NULL;

UPDATE historico_clube_campeonato
SET historico_clube_rank_anterior = COALESCE(normalize_club_rank(historico_clube_rank_anterior), 'E'),
    historico_clube_rank_novo = COALESCE(normalize_club_rank(historico_clube_rank_novo), 'E');

UPDATE campeonato_distribuicao_rank
SET campeonato_distribuicao_rank_rank = normalize_club_rank(campeonato_distribuicao_rank_rank)
WHERE campeonato_distribuicao_rank_rank IS NOT NULL;

DROP FUNCTION IF EXISTS normalize_club_rank(TEXT);

ALTER TABLE clube DROP COLUMN IF EXISTS clube_gols_pro;
ALTER TABLE clube DROP COLUMN IF EXISTS clube_gols_contra;
ALTER TABLE clube DROP COLUMN IF EXISTS clube_vitorias;
ALTER TABLE clube DROP COLUMN IF EXISTS clube_empates;
ALTER TABLE clube DROP COLUMN IF EXISTS clube_derrotas;
ALTER TABLE clube DROP COLUMN IF EXISTS clube_titulos;
ALTER TABLE clube DROP COLUMN IF EXISTS clube_rank;

DROP TABLE IF EXISTS clube_estatistica;

CREATE INDEX IF NOT EXISTS idx_estatisticas_clube_clube ON estatisticas_clube (clube_id);
CREATE INDEX IF NOT EXISTS idx_estatisticas_clube_rank ON estatisticas_clube (clube_rank);

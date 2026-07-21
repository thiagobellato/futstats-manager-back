-- Pontuação da classificação do campeonato
ALTER TABLE campeonato_clube ADD COLUMN IF NOT EXISTS "campeonatoClubePontos" INTEGER DEFAULT 0;
ALTER TABLE campeonato_clube ADD COLUMN IF NOT EXISTS "campeonatoClubeVitorias" INTEGER DEFAULT 0;
ALTER TABLE campeonato_clube ADD COLUMN IF NOT EXISTS "campeonatoClubeEmpates" INTEGER DEFAULT 0;
ALTER TABLE campeonato_clube ADD COLUMN IF NOT EXISTS "campeonatoClubeDerrotas" INTEGER DEFAULT 0;

-- Suspensões automáticas por cartão vermelho (escopo do campeonato)
CREATE TABLE IF NOT EXISTS campeonato_suspensao (
    "campeonatoSuspensaoId" BIGSERIAL PRIMARY KEY,
    campeonato_id BIGINT NOT NULL,
    "campeonatoSuspensaoIdentidade" VARCHAR(64) NOT NULL,
    partida_origem_id BIGINT NOT NULL,
    partida_cumprimento_id BIGINT,
    "campeonatoSuspensaoAtiva" BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_suspensao_campeonato FOREIGN KEY (campeonato_id) REFERENCES campeonato ("campeonatoId"),
    CONSTRAINT fk_suspensao_partida_origem FOREIGN KEY (partida_origem_id) REFERENCES campeonato_partida ("campeonatoPartidaId"),
    CONSTRAINT fk_suspensao_partida_cumprimento FOREIGN KEY (partida_cumprimento_id) REFERENCES campeonato_partida ("campeonatoPartidaId")
);

CREATE INDEX IF NOT EXISTS idx_suspensao_campeonato_ativa
    ON campeonato_suspensao (campeonato_id, "campeonatoSuspensaoAtiva");

CREATE INDEX IF NOT EXISTS idx_suspensao_identidade
    ON campeonato_suspensao (campeonato_id, "campeonatoSuspensaoIdentidade");

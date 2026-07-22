-- Estatísticas acumuladas do clube no banco global
ALTER TABLE clube ADD COLUMN IF NOT EXISTS "clubeGolsPro" INTEGER DEFAULT 0;
ALTER TABLE clube ADD COLUMN IF NOT EXISTS "clubeGolsContra" INTEGER DEFAULT 0;
ALTER TABLE clube ADD COLUMN IF NOT EXISTS "clubeVitorias" INTEGER DEFAULT 0;
ALTER TABLE clube ADD COLUMN IF NOT EXISTS "clubeEmpates" INTEGER DEFAULT 0;
ALTER TABLE clube ADD COLUMN IF NOT EXISTS "clubeDerrotas" INTEGER DEFAULT 0;
ALTER TABLE clube ADD COLUMN IF NOT EXISTS "clubeTitulos" INTEGER DEFAULT 0;

-- Data de finalização oficial do campeonato
ALTER TABLE campeonato ADD COLUMN IF NOT EXISTS "campeonatoDataFinalizacao" TIMESTAMP;

-- Registro definitivo do campeão (base para histórico e campeão protegido)
CREATE TABLE IF NOT EXISTS campeonato_resultado (
    "campeonatoResultadoId" BIGSERIAL PRIMARY KEY,
    campeonato_id BIGINT NOT NULL UNIQUE,
    "campeonatoResultadoNome" VARCHAR(255) NOT NULL,
    campeao_clube_origem_id BIGINT NOT NULL,
    "campeonatoResultadoCampeaoCompetidor" INTEGER,
    "campeonatoResultadoCampeaoCompetidorNome" VARCHAR(255),
    vice_clube_origem_id BIGINT,
    "campeonatoResultadoDataConquista" TIMESTAMP NOT NULL,
    "campeonatoResultadoQuantidadeParticipantes" INTEGER NOT NULL,
    "campeonatoResultadoFormato" INTEGER NOT NULL,
    CONSTRAINT fk_resultado_campeonato FOREIGN KEY (campeonato_id) REFERENCES campeonato ("campeonatoId"),
    CONSTRAINT fk_resultado_campeao FOREIGN KEY (campeao_clube_origem_id) REFERENCES clube ("clubeId"),
    CONSTRAINT fk_resultado_vice FOREIGN KEY (vice_clube_origem_id) REFERENCES clube ("clubeId")
);

-- Histórico de campanha por clube em cada campeonato
CREATE TABLE IF NOT EXISTS historico_clube_campeonato (
    "historicoClubeCampeonatoId" BIGSERIAL PRIMARY KEY,
    clube_id BIGINT NOT NULL,
    campeonato_id BIGINT NOT NULL,
    "historicoClubePosicaoFinal" INTEGER,
    "historicoClubeRankAnterior" VARCHAR(32) NOT NULL,
    "historicoClubeRankNovo" VARCHAR(32) NOT NULL,
    "historicoClubeTituloConquistado" BOOLEAN NOT NULL DEFAULT FALSE,
    "historicoClubeEliminado" BOOLEAN NOT NULL DEFAULT FALSE,
    "historicoClubeFaseAlcancada" INTEGER,
    "historicoClubeJogos" INTEGER DEFAULT 0,
    "historicoClubeVitorias" INTEGER DEFAULT 0,
    "historicoClubeEmpates" INTEGER DEFAULT 0,
    "historicoClubeDerrotas" INTEGER DEFAULT 0,
    "historicoClubeGolsPro" INTEGER DEFAULT 0,
    "historicoClubeGolsContra" INTEGER DEFAULT 0,
    CONSTRAINT fk_hist_clube_clube FOREIGN KEY (clube_id) REFERENCES clube ("clubeId"),
    CONSTRAINT fk_hist_clube_campeonato FOREIGN KEY (campeonato_id) REFERENCES campeonato ("campeonatoId"),
    CONSTRAINT uk_hist_clube_campeonato UNIQUE (clube_id, campeonato_id)
);

-- Histórico agregado do atleta no campeonato
CREATE TABLE IF NOT EXISTS historico_atleta_campeonato (
    "historicoAtletaCampeonatoId" BIGSERIAL PRIMARY KEY,
    atleta_id BIGINT NOT NULL,
    campeonato_id BIGINT NOT NULL,
    "historicoAtletaGols" INTEGER DEFAULT 0,
    "historicoAtletaAssistencias" INTEGER DEFAULT 0,
    "historicoAtletaCartoesAmarelos" INTEGER DEFAULT 0,
    "historicoAtletaCartoesVermelhos" INTEGER DEFAULT 0,
    "historicoAtletaTituloConquistado" BOOLEAN NOT NULL DEFAULT FALSE,
    "historicoAtletaTransferencias" INTEGER DEFAULT 0,
    "historicoAtletaClubesDefendidos" VARCHAR(1000),
    CONSTRAINT fk_hist_atleta_atleta FOREIGN KEY (atleta_id) REFERENCES atleta ("atletaId"),
    CONSTRAINT fk_hist_atleta_campeonato FOREIGN KEY (campeonato_id) REFERENCES campeonato ("campeonatoId"),
    CONSTRAINT uk_hist_atleta_campeonato UNIQUE (atleta_id, campeonato_id)
);

CREATE INDEX IF NOT EXISTS idx_hist_clube_campeonato ON historico_clube_campeonato (campeonato_id);
CREATE INDEX IF NOT EXISTS idx_hist_atleta_campeonato ON historico_atleta_campeonato (campeonato_id);
CREATE INDEX IF NOT EXISTS idx_resultado_campeao ON campeonato_resultado (campeao_clube_origem_id);

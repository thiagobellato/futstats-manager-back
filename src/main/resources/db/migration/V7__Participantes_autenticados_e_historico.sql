-- Módulo 7: participantes autenticados e histórico entre usuários.
-- Script documental/idempotente (schema também via ddl-auto=update).

ALTER TABLE campeonato
    ADD COLUMN IF NOT EXISTS "campeonatoAutenticado" BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE IF NOT EXISTS campeonato_participante (
    "campeonatoParticipanteId" BIGSERIAL PRIMARY KEY,
    campeonato_id BIGINT NOT NULL,
    user_id BIGINT,
    "displayName" VARCHAR(80) NOT NULL,
    authenticated BOOLEAN NOT NULL DEFAULT FALSE,
    side INTEGER NOT NULL,
    CONSTRAINT uk_camp_participante_lado UNIQUE (campeonato_id, side),
    CONSTRAINT fk_camp_participante_campeonato FOREIGN KEY (campeonato_id) REFERENCES campeonato ("campeonatoId"),
    CONSTRAINT fk_camp_participante_user FOREIGN KEY (user_id) REFERENCES usuario ("userId")
);

CREATE TABLE IF NOT EXISTS head_to_head_match (
    "headToHeadMatchId" BIGSERIAL PRIMARY KEY,
    campeonato_id BIGINT NOT NULL,
    participant_a_id BIGINT NOT NULL,
    participant_b_id BIGINT NOT NULL,
    winner_participant_id BIGINT,
    champion_clube_id BIGINT,
    "championshipName" VARCHAR(120) NOT NULL,
    "finishedAt" TIMESTAMP NOT NULL,
    "matchesPlayed" INTEGER NOT NULL DEFAULT 0,
    "goalsParticipantA" INTEGER NOT NULL DEFAULT 0,
    "goalsParticipantB" INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT uk_h2h_campeonato UNIQUE (campeonato_id),
    CONSTRAINT fk_h2h_campeonato FOREIGN KEY (campeonato_id) REFERENCES campeonato ("campeonatoId"),
    CONSTRAINT fk_h2h_part_a FOREIGN KEY (participant_a_id) REFERENCES campeonato_participante ("campeonatoParticipanteId"),
    CONSTRAINT fk_h2h_part_b FOREIGN KEY (participant_b_id) REFERENCES campeonato_participante ("campeonatoParticipanteId"),
    CONSTRAINT fk_h2h_winner FOREIGN KEY (winner_participant_id) REFERENCES campeonato_participante ("campeonatoParticipanteId"),
    CONSTRAINT fk_h2h_clube FOREIGN KEY (champion_clube_id) REFERENCES clube ("clubeId")
);

CREATE TABLE IF NOT EXISTS user_statistics (
    "userStatisticsId" BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    "championshipsPlayed" INTEGER NOT NULL DEFAULT 0,
    "championshipsWon" INTEGER NOT NULL DEFAULT 0,
    "runnerUps" INTEGER NOT NULL DEFAULT 0,
    wins INTEGER NOT NULL DEFAULT 0,
    draws INTEGER NOT NULL DEFAULT 0,
    losses INTEGER NOT NULL DEFAULT 0,
    "goalsFor" INTEGER NOT NULL DEFAULT 0,
    "goalsAgainst" INTEGER NOT NULL DEFAULT 0,
    "goalDifference" INTEGER NOT NULL DEFAULT 0,
    "clubsUsed" INTEGER NOT NULL DEFAULT 0,
    favorite_clube_id BIGINT,
    most_titles_clube_id BIGINT,
    "longestWinningStreak" INTEGER NOT NULL DEFAULT 0,
    "longestUnbeatenStreak" INTEGER NOT NULL DEFAULT 0,
    "currentWinningStreak" INTEGER NOT NULL DEFAULT 0,
    "currentUnbeatenStreak" INTEGER NOT NULL DEFAULT 0,
    "lastChampionshipDate" TIMESTAMP,
    CONSTRAINT uk_user_statistics_user UNIQUE (user_id),
    CONSTRAINT fk_user_stats_user FOREIGN KEY (user_id) REFERENCES usuario ("userId"),
    CONSTRAINT fk_user_stats_fav FOREIGN KEY (favorite_clube_id) REFERENCES clube ("clubeId"),
    CONSTRAINT fk_user_stats_titles FOREIGN KEY (most_titles_clube_id) REFERENCES clube ("clubeId")
);

CREATE TABLE IF NOT EXISTS user_club_usage (
    "userClubUsageId" BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    clube_id BIGINT NOT NULL,
    "timesUsed" INTEGER NOT NULL DEFAULT 0,
    titles INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT uk_user_club_usage UNIQUE (user_id, clube_id),
    CONSTRAINT fk_user_club_usage_user FOREIGN KEY (user_id) REFERENCES usuario ("userId"),
    CONSTRAINT fk_user_club_usage_clube FOREIGN KEY (clube_id) REFERENCES clube ("clubeId")
);

CREATE TABLE IF NOT EXISTS user_rivalry (
    "userRivalryId" BIGSERIAL PRIMARY KEY,
    user_a_id BIGINT NOT NULL,
    user_b_id BIGINT NOT NULL,
    matches INTEGER NOT NULL DEFAULT 0,
    "winsUserA" INTEGER NOT NULL DEFAULT 0,
    "winsUserB" INTEGER NOT NULL DEFAULT 0,
    draws INTEGER NOT NULL DEFAULT 0,
    "goalsUserA" INTEGER NOT NULL DEFAULT 0,
    "goalsUserB" INTEGER NOT NULL DEFAULT 0,
    "lastMatch" TIMESTAMP,
    last_winner_user_id BIGINT,
    CONSTRAINT uk_user_rivalry_pair UNIQUE (user_a_id, user_b_id),
    CONSTRAINT fk_rivalry_user_a FOREIGN KEY (user_a_id) REFERENCES usuario ("userId"),
    CONSTRAINT fk_rivalry_user_b FOREIGN KEY (user_b_id) REFERENCES usuario ("userId"),
    CONSTRAINT fk_rivalry_last_winner FOREIGN KEY (last_winner_user_id) REFERENCES usuario ("userId")
);

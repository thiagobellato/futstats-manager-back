-- Gols contra (own goals) do atleta no banco global e no snapshot do campeonato
ALTER TABLE estatisticas_atleta ADD COLUMN IF NOT EXISTS "atletaGolsContra" INTEGER DEFAULT 0;
ALTER TABLE campeonato_atleta ADD COLUMN IF NOT EXISTS "campeonatoAtletaGolsContra" INTEGER DEFAULT 0;
ALTER TABLE historico_atleta_campeonato ADD COLUMN IF NOT EXISTS "historicoAtletaGolsContra" INTEGER DEFAULT 0;

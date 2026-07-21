-- Atletas criados apenas no campeonato não possuem origem global.
ALTER TABLE campeonato_atleta
    ALTER COLUMN campeonato_atleta_origem_id DROP NOT NULL;

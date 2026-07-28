-- Motivo tipado da suspensão (vermelho, segundo amarelo, acúmulo de amarelos)
ALTER TABLE campeonato_suspensao
    ADD COLUMN IF NOT EXISTS "campeonatoSuspensaoMotivo" VARCHAR(32);

UPDATE campeonato_suspensao
SET "campeonatoSuspensaoMotivo" = 'CARTAO_VERMELHO'
WHERE "campeonatoSuspensaoMotivo" IS NULL;

-- Criar tabela de clubes
CREATE TABLE clube (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    fundacao DATE,
    pais VARCHAR(100)
);

-- Criar tabela de atletas
CREATE TABLE atleta (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    idade INT,
    posicao VARCHAR(50),
    clube_id INT,
    CONSTRAINT fk_atleta_clube FOREIGN KEY (clube_id) REFERENCES clube (id) ON DELETE SET NULL
);

-- Criar tabela de estatísticas
CREATE TABLE estatistica (
    id SERIAL PRIMARY KEY,
    atleta_id INT NOT NULL,
    clube_id INT NOT NULL,
    gols INT DEFAULT 0,
    assistencias INT DEFAULT 0,
    data_inicio DATE NOT NULL,
    data_fim DATE,
    CONSTRAINT fk_estatistica_atleta FOREIGN KEY (atleta_id) REFERENCES atleta (id) ON DELETE CASCADE,
    CONSTRAINT fk_estatistica_clube FOREIGN KEY (clube_id) REFERENCES clube (id) ON DELETE CASCADE
);

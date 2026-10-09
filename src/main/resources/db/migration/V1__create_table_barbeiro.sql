
CREATE TABLE barbeiro (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome           VARCHAR(150) NOT NULL,
    especialidade  VARCHAR(100),
    telefone       VARCHAR(20)  NOT NULL,
    ativo          BOOLEAN      NOT NULL DEFAULT true,
    criado_em      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uk_barbeiro_telefone UNIQUE (telefone)
);
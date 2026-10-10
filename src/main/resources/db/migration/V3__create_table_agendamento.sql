CREATE TABLE agendamento (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    barbeiro_id       BIGINT        NOT NULL,
    servico_id        BIGINT        NOT NULL,
    nome_cliente      VARCHAR(150)  NOT NULL,
    telefone_cliente  VARCHAR(20)   NOT NULL,
    data_hora_inicio  TIMESTAMPTZ   NOT NULL,
    data_hora_fim     TIMESTAMPTZ   NOT NULL,
    valor_cobrado     NUMERIC(10,2) NOT NULL,
    status            VARCHAR(20)   NOT NULL DEFAULT 'AGENDADO',
    criado_em         TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT fk_agendamento_barbeiro
        FOREIGN KEY (barbeiro_id) REFERENCES barbeiro (id),
    CONSTRAINT fk_agendamento_servico
        FOREIGN KEY (servico_id) REFERENCES servico(id),
    CONSTRAINT ck_agendamento_status
        CHECK (status IN ('AGENDADO', 'CONFIRMADO', 'EM_ANDAMENTO', 'CONCLUIDO', 'CANCELADO')),
    CONSTRAINT ck_agendamento_horario
        CHECK (data_hora_fim > data_hora_inicio)
);

-- Regra de negócio: no máximo 1 agendamento ativo por telefone
CREATE UNIQUE INDEX uk_agendamento_telefone_ativo
    ON agendamento (telefone_cliente)
    WHERE status IN ('AGENDADO', 'CONFIRMADO', 'EM_ANDAMENTO');

-- Desempenho: acelera a checagem de conflito de horário do barbeiro
CREATE INDEX ix_agendamento_barbeiro_horario
    ON agendamento (barbeiro_id, data_hora_inicio, data_hora_fim)
    WHERE status IN ('AGENDADO', 'CONFIRMADO', 'EM_ANDAMENTO');
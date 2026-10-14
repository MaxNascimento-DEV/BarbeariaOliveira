-- V5__create_table_horario_trabalho.sql
CREATE TABLE horario_trabalho (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    barbeiro_id      BIGINT      NOT NULL,
    dia_semana       VARCHAR(15) NOT NULL,
    hora_abertura    TIME        NOT NULL,
    hora_fechamento  TIME        NOT NULL,
    almoco_inicio    TIME,
    almoco_fim       TIME,

    CONSTRAINT fk_horario_trabalho_barbeiro
        FOREIGN KEY (barbeiro_id) REFERENCES barbeiro (id),
    CONSTRAINT uk_horario_trabalho_barbeiro_dia
        UNIQUE (barbeiro_id, dia_semana),
    CONSTRAINT ck_horario_trabalho_dia_semana
        CHECK (dia_semana IN ('SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA',
                              'SEXTA', 'SABADO', 'DOMINGO')),
    CONSTRAINT ck_horario_trabalho_expediente
        CHECK (hora_fechamento > hora_abertura),
    CONSTRAINT ck_horario_trabalho_almoco_completo
        CHECK ((almoco_inicio IS NULL) = (almoco_fim IS NULL)),
    CONSTRAINT ck_horario_trabalho_almoco_dentro
        CHECK (almoco_inicio IS NULL
               OR (almoco_inicio >= hora_abertura
                   AND almoco_fim <= hora_fechamento
                   AND almoco_fim > almoco_inicio))
);
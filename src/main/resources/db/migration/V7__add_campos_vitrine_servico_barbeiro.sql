-- Campos usados pela vitrine do site (front-end): descrição/ícone/categoria do serviço e foto do barbeiro
ALTER TABLE servico
    ADD COLUMN descricao VARCHAR(255),
    ADD COLUMN icone     VARCHAR(20) NOT NULL DEFAULT 'scissors',
    ADD COLUMN categoria VARCHAR(20) NOT NULL DEFAULT 'cabelo';

ALTER TABLE servico
    ADD CONSTRAINT ck_servico_icone
        CHECK (icone IN ('scissors', 'beard', 'combo', 'razor', 'spa', 'color')),
    ADD CONSTRAINT ck_servico_categoria
        CHECK (categoria IN ('cabelo', 'barba', 'cuidados'));

ALTER TABLE barbeiro
    ADD COLUMN foto_url VARCHAR(500);

-- Logins passam a ter perfil: ADMIN (dono) ou BARBEIRO (vê só a própria agenda)
ALTER TABLE usuario_admin RENAME TO usuario;
ALTER TABLE usuario RENAME CONSTRAINT uk_usuario_admin_email TO uk_usuario_email;

ALTER TABLE usuario
    ADD COLUMN nome        VARCHAR(150),
    ADD COLUMN perfil      VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
    ADD COLUMN barbeiro_id BIGINT,
    ADD COLUMN trocar_senha BOOLEAN    NOT NULL DEFAULT false;

-- Admin(s) já existentes
UPDATE usuario SET nome = 'Administrador' WHERE nome IS NULL;
ALTER TABLE usuario ALTER COLUMN nome SET NOT NULL;

ALTER TABLE usuario
    ADD CONSTRAINT ck_usuario_perfil CHECK (perfil IN ('ADMIN', 'BARBEIRO')),
    ADD CONSTRAINT fk_usuario_barbeiro FOREIGN KEY (barbeiro_id) REFERENCES barbeiro (id),
    -- cada barbeiro tem no máximo um login
    ADD CONSTRAINT uk_usuario_barbeiro UNIQUE (barbeiro_id),
    -- BARBEIRO precisa estar ligado a um barbeiro; ADMIN não
    ADD CONSTRAINT ck_usuario_barbeiro_perfil CHECK ((perfil = 'BARBEIRO') = (barbeiro_id IS NOT NULL));

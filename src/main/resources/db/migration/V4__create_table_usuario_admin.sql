CREATE TABLE usuario_admin(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
    email VARCHAR(150) NOT NULL, 
    senha_hash VARCHAR(100) NOT NULL, 
    ativo BOOLEAN NOT NULL DEFAULT true, 
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uk_usuario_admin_email UNIQUE(email)
);
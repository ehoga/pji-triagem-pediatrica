-- =========================================================================
-- Schema inicial do PediTriagem
-- =========================================================================

-- -------------------------------------------------------------------------
-- Usuário
-- -------------------------------------------------------------------------
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,

                       nome VARCHAR(100) NOT NULL,
                       cpf VARCHAR(11) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       senha VARCHAR(255) NOT NULL,

                       tipo_usuario VARCHAR(30) NOT NULL,

                       criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
                       atualizado_em TIMESTAMP NULL,

                       ativo BOOLEAN NOT NULL DEFAULT TRUE,
                       bloqueado BOOLEAN NOT NULL DEFAULT FALSE,
                       bloqueado_temporariamente BOOLEAN NOT NULL DEFAULT FALSE,
                       tentativas INTEGER NOT NULL DEFAULT 0,

                       CONSTRAINT chk_users_nome_min
                           CHECK (LENGTH(TRIM(nome)) >= 2),

                       CONSTRAINT chk_users_email_format
                           CHECK (email ~* '^[^@\s]+@[^@\s]+\.[^@\s]+$'),

    CONSTRAINT chk_users_cpf_format
        CHECK (cpf ~ '^[0-9]{11}$'),

    CONSTRAINT chk_users_tentativas_min
        CHECK (tentativas >= 0)
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_cpf ON users(cpf);
CREATE INDEX idx_users_tipo_usuario ON users(tipo_usuario);

-- -------------------------------------------------------------------------
-- Criança
-- -------------------------------------------------------------------------
CREATE TABLE crianca (
                         id BIGSERIAL PRIMARY KEY,

                         usuario_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,

                         nome VARCHAR(100) NOT NULL,
                         cpf VARCHAR(11) UNIQUE,

                         data_nascimento DATE NOT NULL,
                         peso_kg NUMERIC(5,2),

                         avatar_emoji VARCHAR(8) NOT NULL DEFAULT '🌸',

                         criado_em TIMESTAMP NOT NULL DEFAULT NOW(),

                         CONSTRAINT chk_crianca_nome_min
                             CHECK (LENGTH(TRIM(nome)) >= 1),

                         CONSTRAINT chk_crianca_cpf_format
                             CHECK (cpf IS NULL OR cpf ~ '^[0-9]{11}$'),

    CONSTRAINT chk_crianca_data_passada
        CHECK (data_nascimento <= CURRENT_DATE),

    CONSTRAINT chk_crianca_peso_positivo
        CHECK (peso_kg IS NULL OR peso_kg > 0)
);

CREATE INDEX idx_crianca_usuario ON crianca(usuario_id);
CREATE INDEX idx_crianca_cpf ON crianca(cpf);

-- -------------------------------------------------------------------------
-- Sintoma
-- -------------------------------------------------------------------------
CREATE TABLE sintoma (
                         id BIGSERIAL PRIMARY KEY,

                         codigo VARCHAR(40) NOT NULL UNIQUE,
                         nome VARCHAR(100) NOT NULL,

                         descricao_curta VARCHAR(255),
                         icone_ref VARCHAR(40),
                         cor_hex VARCHAR(7),

                         ordem INT NOT NULL DEFAULT 0,

                         CONSTRAINT chk_sintoma_cor_hex
                             CHECK (
                                 cor_hex IS NULL
                                     OR cor_hex ~* '^#[0-9a-f]{6}$'
)
    );

-- -------------------------------------------------------------------------
-- Pergunta
-- -------------------------------------------------------------------------
CREATE TABLE pergunta (
                          id BIGSERIAL PRIMARY KEY,

                          sintoma_id BIGINT NOT NULL
                              REFERENCES sintoma(id) ON DELETE CASCADE,

                          codigo VARCHAR(40) NOT NULL,
                          texto VARCHAR(500) NOT NULL,
                          sub VARCHAR(255),

                          tipo VARCHAR(30) NOT NULL,

                          ordem INT NOT NULL DEFAULT 0,

                          CONSTRAINT uq_pergunta_codigo_sintoma
                              UNIQUE (sintoma_id, codigo)
);

CREATE INDEX idx_pergunta_sintoma ON pergunta(sintoma_id);

-- -------------------------------------------------------------------------
-- Opções da pergunta
-- -------------------------------------------------------------------------
CREATE TABLE opcao_pergunta (
                                id BIGSERIAL PRIMARY KEY,

                                pergunta_id BIGINT NOT NULL
                                    REFERENCES pergunta(id) ON DELETE CASCADE,

                                codigo VARCHAR(40) NOT NULL,
                                texto VARCHAR(500) NOT NULL,

                                score INT NOT NULL,

                                red_flag BOOLEAN NOT NULL DEFAULT FALSE,

                                ordem INT NOT NULL DEFAULT 0,

                                CONSTRAINT uq_opcao_codigo_pergunta
                                    UNIQUE (pergunta_id, codigo),

                                CONSTRAINT chk_opcao_score_range
                                    CHECK (score BETWEEN 0 AND 10)
);

CREATE INDEX idx_opcao_pergunta
    ON opcao_pergunta(pergunta_id);

-- -------------------------------------------------------------------------
-- Peso perguntas YES/NO
-- -------------------------------------------------------------------------
CREATE TABLE peso_yesno (
                            id BIGSERIAL PRIMARY KEY,

                            pergunta_id BIGINT NOT NULL UNIQUE
                                REFERENCES pergunta(id) ON DELETE CASCADE,

                            score_yes INT NOT NULL,
                            score_no INT NOT NULL,
                            score_dunno INT NOT NULL,

                            red_flag_on_yes BOOLEAN NOT NULL DEFAULT FALSE,
                            red_flag_on_no BOOLEAN NOT NULL DEFAULT FALSE,

                            CONSTRAINT chk_yesno_score_yes_range
                                CHECK (score_yes BETWEEN 0 AND 10),

                            CONSTRAINT chk_yesno_score_no_range
                                CHECK (score_no BETWEEN 0 AND 10),

                            CONSTRAINT chk_yesno_score_dunno_range
                                CHECK (score_dunno BETWEEN 0 AND 10)
);

-- -------------------------------------------------------------------------
-- Avaliação
-- -------------------------------------------------------------------------
CREATE TABLE avaliacao (
                           id BIGSERIAL PRIMARY KEY,

                           crianca_id BIGINT NOT NULL
                               REFERENCES crianca(id) ON DELETE CASCADE,

                           sintoma_id BIGINT NOT NULL
                               REFERENCES sintoma(id),

                           classificacao VARCHAR(30) NOT NULL,

                           score INT NOT NULL,

                           red_flag_detected BOOLEAN NOT NULL DEFAULT FALSE,

                           respostas JSONB NOT NULL,

                           protocolo_versao VARCHAR(20) NOT NULL DEFAULT '1.0.0',

                           criado_em TIMESTAMP NOT NULL DEFAULT NOW(),

                           CONSTRAINT chk_avaliacao_score_min
                               CHECK (score >= 0)
);

CREATE INDEX idx_avaliacao_crianca_data
    ON avaliacao(crianca_id, criado_em DESC);

CREATE INDEX idx_avaliacao_classificacao
    ON avaliacao(classificacao);
-- 00_create_tables.sql  (corrigé)

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--------------------------------------------------------------------
-- TABLE parking_slots
--------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS parking_slots (
                                             id           UUID          PRIMARY KEY DEFAULT uuid_generate_v4(),
    code         VARCHAR(3)    NOT NULL UNIQUE,
    "row"        CHAR(1)       NOT NULL,
    number       INTEGER       NOT NULL,
    has_charger  BOOLEAN       NOT NULL DEFAULT FALSE
    );

--------------------------------------------------------------------
-- TABLE users
--------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
                                     id                    UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    firstname             TEXT         NOT NULL,
    lastname              TEXT         NOT NULL,
    email                 TEXT         NOT NULL UNIQUE,
    password_hash         TEXT         NOT NULL,
    role                  VARCHAR(20)  NOT NULL,
    session_token         TEXT,
    is_hybrid_or_electric BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_users_role CHECK (role IN ('MANAGER','EMPLOYEE','SECRETARY'))
    );

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users
(
    id               UUID PRIMARY KEY      DEFAULT gen_random_uuid(),

    keycloak_subject VARCHAR(255) NOT NULL,
    email            VARCHAR(320) NOT NULL,
    email_verified   BOOLEAN      NOT NULL DEFAULT FALSE,

    first_name       VARCHAR(120),
    last_name        VARCHAR(120),
    display_name     VARCHAR(180),

    status           VARCHAR(40)  NOT NULL,

    last_login_at    TIMESTAMPTZ,

    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at       TIMESTAMPTZ,

    CONSTRAINT uq_users_keycloak_subject UNIQUE (keycloak_subject),
    CONSTRAINT uq_users_email UNIQUE (email),

    CONSTRAINT chk_users_status CHECK (
        status IN ('ACTIVE', 'DISABLED', 'DELETED')
        )
);

CREATE INDEX idx_users_keycloak_subject ON users (keycloak_subject);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_status ON users (status);
CREATE INDEX idx_users_deleted_at ON users (deleted_at);


CREATE TABLE user_files
(
    id                UUID PRIMARY KEY      DEFAULT gen_random_uuid(),

    owner_user_id     UUID         NOT NULL,

    storage_key       VARCHAR(500) NOT NULL,
    public_url        VARCHAR(1000),
    original_filename VARCHAR(255),
    mime_type         VARCHAR(120) NOT NULL,
    size_bytes        BIGINT       NOT NULL,

    kind              VARCHAR(40)  NOT NULL,
    status            VARCHAR(40)  NOT NULL,

    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at        TIMESTAMPTZ,

    CONSTRAINT fk_user_files_owner_user
        FOREIGN KEY (owner_user_id)
            REFERENCES users (id)
            ON DELETE RESTRICT,

    CONSTRAINT uq_user_files_storage_key UNIQUE (storage_key),

    CONSTRAINT chk_user_files_kind CHECK (
        kind IN ('AVATAR', 'RESUME', 'ATTACHMENT')
        ),

    CONSTRAINT chk_user_files_status CHECK (
        status IN ('PENDING', 'READY', 'FAILED', 'DELETED')
        ),

    CONSTRAINT chk_user_files_size_positive CHECK (
        size_bytes >= 0
        )
);

CREATE INDEX idx_user_files_owner_user_id ON user_files (owner_user_id);
CREATE INDEX idx_user_files_kind ON user_files (kind);
CREATE INDEX idx_user_files_status ON user_files (status);
CREATE INDEX idx_user_files_deleted_at ON user_files (deleted_at);


CREATE TABLE user_profiles
(
    id                      UUID PRIMARY KEY     DEFAULT gen_random_uuid(),

    user_id                 UUID        NOT NULL,
    avatar_file_id          UUID,

    avatar_url              VARCHAR(1000),

    career_goal             VARCHAR(60),
    target_role             VARCHAR(180),
    experience_level        VARCHAR(60),

    preferred_language      VARCHAR(10) NOT NULL DEFAULT 'fr',

    onboarding_status       VARCHAR(40) NOT NULL,
    onboarding_completed_at TIMESTAMPTZ,

    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_user_profiles_user_id UNIQUE (user_id),

    CONSTRAINT fk_user_profiles_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_user_profiles_avatar_file
        FOREIGN KEY (avatar_file_id)
            REFERENCES user_files (id)
            ON DELETE SET NULL,

    CONSTRAINT chk_user_profiles_career_goal CHECK (
        career_goal IS NULL OR career_goal IN (
                                               'FIND_JOB',
                                               'FIND_INTERNSHIP',
                                               'CHANGE_CAREER',
                                               'PREPARE_INTERVIEW',
                                               'IMPROVE_RESUME',
                                               'TRACK_OPPORTUNITIES'
            )
        ),

    CONSTRAINT chk_user_profiles_experience_level CHECK (
        experience_level IS NULL OR experience_level IN (
                                                         'STUDENT',
                                                         'JUNIOR',
                                                         'MEDIOR',
                                                         'SENIOR',
                                                         'CAREER_CHANGE'
            )
        ),

    CONSTRAINT chk_user_profiles_onboarding_status CHECK (
        onboarding_status IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED')
        )
);

CREATE INDEX idx_user_profiles_user_id ON user_profiles (user_id);
CREATE INDEX idx_user_profiles_avatar_file_id ON user_profiles (avatar_file_id);
CREATE INDEX idx_user_profiles_onboarding_status ON user_profiles (onboarding_status);
CREATE INDEX idx_user_profiles_career_goal ON user_profiles (career_goal);


CREATE TABLE subscriptions
(
    id                   UUID PRIMARY KEY     DEFAULT gen_random_uuid(),

    user_id              UUID        NOT NULL,

    plan                 VARCHAR(40) NOT NULL,
    status               VARCHAR(40) NOT NULL,

    current_period_start TIMESTAMPTZ,
    current_period_end   TIMESTAMPTZ,

    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_subscriptions_user_id UNIQUE (user_id),

    CONSTRAINT fk_subscriptions_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_subscriptions_plan CHECK (
        plan IN ('FREE', 'PREMIUM')
        ),

    CONSTRAINT chk_subscriptions_status CHECK (
        status IN ('ACTIVE', 'PAST_DUE', 'CANCELED', 'TRIALING', 'EXPIRED')
        )
);

CREATE INDEX idx_subscriptions_user_id ON subscriptions (user_id);
CREATE INDEX idx_subscriptions_plan ON subscriptions (plan);
CREATE INDEX idx_subscriptions_status ON subscriptions (status);


CREATE TABLE ai_credit_wallets
(
    id               UUID PRIMARY KEY     DEFAULT gen_random_uuid(),

    user_id          UUID        NOT NULL,

    monthly_limit    INT         NOT NULL,
    used_this_period INT         NOT NULL,
    remaining        INT         NOT NULL,

    period_start     DATE        NOT NULL,
    period_end       DATE        NOT NULL,

    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_ai_credit_wallets_user_id UNIQUE (user_id),

    CONSTRAINT fk_ai_credit_wallets_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_ai_credit_wallets_monthly_limit CHECK (monthly_limit >= 0),
    CONSTRAINT chk_ai_credit_wallets_used_this_period CHECK (used_this_period >= 0),
    CONSTRAINT chk_ai_credit_wallets_remaining CHECK (remaining >= 0),
    CONSTRAINT chk_ai_credit_wallets_period CHECK (period_end >= period_start)
);

CREATE INDEX idx_ai_credit_wallets_user_id ON ai_credit_wallets (user_id);
CREATE INDEX idx_ai_credit_wallets_period ON ai_credit_wallets (period_start, period_end);

-- V7__organizations_billing_entitlements_invitations.sql
-- Trajectiv
--
-- Scope:
-- - migrate legacy personal subscriptions to user_subscriptions
-- - introduce plans and plan entitlements
-- - organizations and active memberships
-- - organization invitations
-- - organization subscriptions
-- - user/organization entitlement grants
-- - organization AI credit wallets
--
-- Assumes V1..V6 have already run and that V6 added ai_credit_wallets.version
-- plus its wallet consistency constraints.

-- ============================================================
-- 1. PLAN CATALOG
-- ============================================================

CREATE TABLE plans (
                       id uuid PRIMARY KEY,
                       code varchar(80) NOT NULL,
                       audience varchar(30) NOT NULL,
                       label varchar(255) NOT NULL,
                       active boolean NOT NULL DEFAULT true,

                       CONSTRAINT uk_plans_code
                           UNIQUE (code),

                       CONSTRAINT chk_plans_code_not_blank
                           CHECK (btrim(code) <> ''),

                       CONSTRAINT chk_plans_audience
                           CHECK (audience IN ('B2C', 'B2B'))
);

CREATE INDEX idx_plans_audience
    ON plans(audience);

CREATE INDEX idx_plans_active
    ON plans(active);

INSERT INTO plans (
    id,
    code,
    audience,
    label,
    active
)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'FREE', 'B2C', 'Free', true),
    ('00000000-0000-0000-0000-000000000002', 'STARTER', 'B2C', 'Starter', true),
    ('00000000-0000-0000-0000-000000000003', 'PRO', 'B2C', 'Pro', true),
    ('00000000-0000-0000-0000-000000000101', 'ORGANIZATION_STARTER', 'B2B', 'Organization Starter', true),
    ('00000000-0000-0000-0000-000000000102', 'ORGANIZATION_FORMATION', 'B2B', 'Organization Formation', true),
    ('00000000-0000-0000-0000-000000000103', 'ORGANIZATION_CENTER', 'B2B', 'Organization Center', true);

-- ============================================================
-- 2. MIGRATE LEGACY subscriptions -> user_subscriptions
-- ============================================================

ALTER TABLE subscriptions
    RENAME TO user_subscriptions;

ALTER TABLE user_subscriptions
    RENAME COLUMN plan TO plan_code;

ALTER TABLE user_subscriptions
    ALTER COLUMN plan_code TYPE varchar(80)
        USING upper(plan_code::text);

-- Legacy PREMIUM is now represented by PRO.
UPDATE user_subscriptions
SET plan_code = 'PRO'
WHERE upper(plan_code) = 'PREMIUM';

ALTER TABLE user_subscriptions
    ADD COLUMN stripe_subscription_id varchar(255),
    ADD COLUMN stripe_price_id varchar(255),
    ADD COLUMN cancel_at_period_end boolean NOT NULL DEFAULT false,
    ADD COLUMN trial_end timestamptz;

ALTER TABLE user_subscriptions
    ADD CONSTRAINT uk_user_subscriptions_user_id
        UNIQUE (user_id),

    ADD CONSTRAINT uk_user_subscriptions_stripe_subscription_id
        UNIQUE (stripe_subscription_id),

    ADD CONSTRAINT fk_user_subscriptions_plan_code
        FOREIGN KEY (plan_code)
            REFERENCES plans(code)
            ON UPDATE RESTRICT
            ON DELETE RESTRICT,

    ADD CONSTRAINT chk_user_subscriptions_status
        CHECK (
            status IN (
                       'ACTIVE',
                       'TRIALING',
                       'PAST_DUE',
                       'CANCELED',
                       'EXPIRED'
                )
            ),

    ADD CONSTRAINT chk_user_subscriptions_period
        CHECK (
            (
                current_period_start IS NULL
                    AND current_period_end IS NULL
                )
                OR
            (
                current_period_start IS NOT NULL
                    AND current_period_end IS NOT NULL
                    AND current_period_end >= current_period_start
                )
            );

DROP INDEX IF EXISTS idx_subscriptions_user_id;
DROP INDEX IF EXISTS idx_subscriptions_plan;
DROP INDEX IF EXISTS idx_subscriptions_status;

CREATE INDEX idx_user_subscriptions_user_id
    ON user_subscriptions(user_id);

CREATE INDEX idx_user_subscriptions_plan_code
    ON user_subscriptions(plan_code);

CREATE INDEX idx_user_subscriptions_status
    ON user_subscriptions(status);

-- ============================================================
-- 3. ORGANIZATIONS
-- ============================================================

CREATE TABLE organizations (
                               id uuid PRIMARY KEY,
                               slug varchar(120) NOT NULL,
                               name varchar(180) NOT NULL,
                               avatar_url varchar(1000),
                               status varchar(40) NOT NULL,
                               created_at timestamptz NOT NULL,
                               updated_at timestamptz NOT NULL,

                               CONSTRAINT uk_organizations_slug
                                   UNIQUE (slug),

                               CONSTRAINT chk_organizations_slug_not_blank
                                   CHECK (btrim(slug) <> ''),

                               CONSTRAINT chk_organizations_slug_normalized
                                   CHECK (slug = lower(slug)),

                               CONSTRAINT chk_organizations_name_not_blank
                                   CHECK (btrim(name) <> ''),

                               CONSTRAINT chk_organizations_status
                                   CHECK (
                                       status IN (
                                                  'ACTIVE',
                                                  'SUSPENDED',
                                                  'ARCHIVED'
                                           )
                                       )
);

CREATE INDEX idx_organizations_slug
    ON organizations(slug);

CREATE INDEX idx_organizations_status
    ON organizations(status);

-- ============================================================
-- 4. ORGANIZATION MEMBERS
-- ============================================================
-- Invitations are stored in organization_invitations.
-- Therefore organization_members deliberately has no INVITED status.

CREATE TABLE organization_members (
                                      id uuid PRIMARY KEY,
                                      user_id uuid NOT NULL,
                                      organization_id uuid NOT NULL,
                                      role varchar(60) NOT NULL,
                                      status varchar(40) NOT NULL,
                                      joined_at timestamptz,
                                      created_at timestamptz NOT NULL,
                                      updated_at timestamptz NOT NULL,

                                      CONSTRAINT fk_organization_members_user
                                          FOREIGN KEY (user_id)
                                              REFERENCES users(id)
                                              ON UPDATE RESTRICT
                                              ON DELETE RESTRICT,

                                      CONSTRAINT fk_organization_members_organization
                                          FOREIGN KEY (organization_id)
                                              REFERENCES organizations(id)
                                              ON UPDATE RESTRICT
                                              ON DELETE RESTRICT,

                                      CONSTRAINT uk_organization_members_user_org
                                          UNIQUE (user_id, organization_id),

                                      CONSTRAINT chk_organization_members_role
                                          CHECK (
                                              role IN (
                                                       'ORGANIZATION_OWNER',
                                                       'ORGANIZATION_ADMIN',
                                                       'RECRUITER',
                                                       'COACH',
                                                       'TRAINER',
                                                       'LEARNER'
                                                  )
                                              ),

                                      CONSTRAINT chk_organization_members_status
                                          CHECK (
                                              status IN (
                                                         'ACTIVE',
                                                         'SUSPENDED',
                                                         'REMOVED'
                                                  )
                                              ),

                                      CONSTRAINT chk_organization_members_joined_at
                                          CHECK (
                                              status = 'REMOVED'
                                                  OR joined_at IS NOT NULL
                                              )
);

CREATE INDEX idx_organization_members_user_id
    ON organization_members(user_id);

CREATE INDEX idx_organization_members_organization_id
    ON organization_members(organization_id);

CREATE INDEX idx_organization_members_status
    ON organization_members(status);

CREATE INDEX idx_organization_members_role
    ON organization_members(role);

CREATE INDEX idx_organization_members_org_status
    ON organization_members(organization_id, status);

-- ============================================================
-- 5. ORGANIZATION INVITATIONS
-- ============================================================

CREATE TABLE organization_invitations (
                                          id uuid PRIMARY KEY,
                                          organization_id uuid NOT NULL,
                                          email varchar(320) NOT NULL,
                                          role varchar(60) NOT NULL,
                                          token_hash varchar(128) NOT NULL,
                                          status varchar(40) NOT NULL,
                                          invited_by_user_id uuid NOT NULL,
                                          accepted_by_user_id uuid,
                                          expires_at timestamptz NOT NULL,
                                          accepted_at timestamptz,
                                          revoked_at timestamptz,
                                          created_at timestamptz NOT NULL,
                                          updated_at timestamptz NOT NULL,

                                          CONSTRAINT fk_org_invitations_organization
                                              FOREIGN KEY (organization_id)
                                                  REFERENCES organizations(id)
                                                  ON UPDATE RESTRICT
                                                  ON DELETE RESTRICT,

                                          CONSTRAINT fk_org_invitations_invited_by_user
                                              FOREIGN KEY (invited_by_user_id)
                                                  REFERENCES users(id)
                                                  ON UPDATE RESTRICT
                                                  ON DELETE RESTRICT,

                                          CONSTRAINT fk_org_invitations_accepted_by_user
                                              FOREIGN KEY (accepted_by_user_id)
                                                  REFERENCES users(id)
                                                  ON UPDATE RESTRICT
                                                  ON DELETE RESTRICT,

                                          CONSTRAINT uk_org_invitations_token_hash
                                              UNIQUE (token_hash),

                                          CONSTRAINT chk_org_invitations_email_not_blank
                                              CHECK (btrim(email) <> ''),

                                          CONSTRAINT chk_org_invitations_email_normalized
                                              CHECK (email = lower(email)),

                                          CONSTRAINT chk_org_invitations_role
                                              CHECK (
                                                  role IN (
                                                           'ORGANIZATION_ADMIN',
                                                           'RECRUITER',
                                                           'COACH',
                                                           'TRAINER',
                                                           'LEARNER'
                                                      )
                                                  ),

                                          CONSTRAINT chk_org_invitations_status
                                              CHECK (
                                                  status IN (
                                                             'PENDING',
                                                             'ACCEPTED',
                                                             'REVOKED',
                                                             'EXPIRED'
                                                      )
                                                  ),

                                          CONSTRAINT chk_org_invitations_expiration
                                              CHECK (expires_at > created_at),

                                          CONSTRAINT chk_org_invitations_acceptance
                                              CHECK (
                                                  (
                                                      status = 'ACCEPTED'
                                                          AND accepted_by_user_id IS NOT NULL
                                                          AND accepted_at IS NOT NULL
                                                          AND revoked_at IS NULL
                                                      )
                                                      OR
                                                  (
                                                      status <> 'ACCEPTED'
                                                          AND accepted_by_user_id IS NULL
                                                          AND accepted_at IS NULL
                                                      )
                                                  ),

                                          CONSTRAINT chk_org_invitations_revocation
                                              CHECK (
                                                  (
                                                      status = 'REVOKED'
                                                          AND revoked_at IS NOT NULL
                                                      )
                                                      OR
                                                  (
                                                      status <> 'REVOKED'
                                                          AND revoked_at IS NULL
                                                      )
                                                  )
);

CREATE INDEX idx_org_invitations_organization_id
    ON organization_invitations(organization_id);

CREATE INDEX idx_org_invitations_email
    ON organization_invitations(email);

CREATE INDEX idx_org_invitations_status
    ON organization_invitations(status);

CREATE INDEX idx_org_invitations_expires_at
    ON organization_invitations(expires_at);

CREATE INDEX idx_org_invitations_org_status
    ON organization_invitations(organization_id, status);

CREATE UNIQUE INDEX uk_org_invitations_pending_email
    ON organization_invitations(
                                organization_id,
                                lower(email)
        )
    WHERE status = 'PENDING';

-- ============================================================
-- 6. ORGANIZATION SUBSCRIPTIONS
-- ============================================================

CREATE TABLE organization_subscriptions (
                                            id uuid PRIMARY KEY,
                                            organization_id uuid NOT NULL,
                                            plan_code varchar(80) NOT NULL,
                                            status varchar(40) NOT NULL,
                                            stripe_subscription_id varchar(255),
                                            stripe_price_id varchar(255),
                                            current_period_start timestamptz,
                                            current_period_end timestamptz,
                                            cancel_at_period_end boolean NOT NULL DEFAULT false,
                                            seat_limit integer,
                                            created_at timestamptz NOT NULL,
                                            updated_at timestamptz NOT NULL,

                                            CONSTRAINT uk_org_subscriptions_organization_id
                                                UNIQUE (organization_id),

                                            CONSTRAINT uk_org_subscriptions_stripe_subscription_id
                                                UNIQUE (stripe_subscription_id),

                                            CONSTRAINT fk_org_subscriptions_organization
                                                FOREIGN KEY (organization_id)
                                                    REFERENCES organizations(id)
                                                    ON UPDATE RESTRICT
                                                    ON DELETE RESTRICT,

                                            CONSTRAINT fk_org_subscriptions_plan_code
                                                FOREIGN KEY (plan_code)
                                                    REFERENCES plans(code)
                                                    ON UPDATE RESTRICT
                                                    ON DELETE RESTRICT,

                                            CONSTRAINT chk_org_subscriptions_plan_code
                                                CHECK (plan_code LIKE 'ORGANIZATION\_%' ESCAPE '\'),

                                            CONSTRAINT chk_org_subscriptions_status
                                                CHECK (
                                                    status IN (
                                                               'ACTIVE',
                                                               'TRIALING',
                                                               'PAST_DUE',
                                                               'CANCELED',
                                                               'EXPIRED'
                                                        )
                                                    ),

                                            CONSTRAINT chk_org_subscriptions_period
                                                CHECK (
                                                    (
                                                        current_period_start IS NULL
                                                            AND current_period_end IS NULL
                                                        )
                                                        OR
                                                    (
                                                        current_period_start IS NOT NULL
                                                            AND current_period_end IS NOT NULL
                                                            AND current_period_end >= current_period_start
                                                        )
                                                    ),

                                            CONSTRAINT chk_org_subscriptions_seat_limit
                                                CHECK (
                                                    seat_limit IS NULL
                                                        OR seat_limit > 0
                                                    )
);

CREATE INDEX idx_org_subscriptions_organization_id
    ON organization_subscriptions(organization_id);

CREATE INDEX idx_org_subscriptions_plan_code
    ON organization_subscriptions(plan_code);

CREATE INDEX idx_org_subscriptions_status
    ON organization_subscriptions(status);

-- ============================================================
-- 7. PLAN ENTITLEMENTS
-- ============================================================

CREATE TABLE plan_entitlements (
                                   id uuid PRIMARY KEY,
                                   plan_id uuid NOT NULL,
                                   feature_key varchar(100) NOT NULL,
                                   allowed boolean NOT NULL,
                                   quota_monthly integer,
                                   quota_total integer,
                                   max_items integer,
                                   reset_period varchar(30),

                                   CONSTRAINT fk_plan_entitlements_plan
                                       FOREIGN KEY (plan_id)
                                           REFERENCES plans(id)
                                           ON UPDATE RESTRICT
                                           ON DELETE CASCADE,

                                   CONSTRAINT uk_plan_entitlement_feature
                                       UNIQUE (plan_id, feature_key),

                                   CONSTRAINT chk_plan_entitlements_feature_key_not_blank
                                       CHECK (btrim(feature_key) <> ''),

                                   CONSTRAINT chk_plan_entitlements_quota_monthly
                                       CHECK (
                                           quota_monthly IS NULL
                                               OR quota_monthly >= 0
                                           ),

                                   CONSTRAINT chk_plan_entitlements_quota_total
                                       CHECK (
                                           quota_total IS NULL
                                               OR quota_total >= 0
                                           ),

                                   CONSTRAINT chk_plan_entitlements_max_items
                                       CHECK (
                                           max_items IS NULL
                                               OR max_items >= 0
                                           )
);

CREATE INDEX idx_plan_entitlements_plan_id
    ON plan_entitlements(plan_id);

CREATE INDEX idx_plan_entitlements_feature_key
    ON plan_entitlements(feature_key);

-- Minimal known limits preserving the legacy behavior.
-- Additional entitlements should be inserted only after product quotas are validated.
INSERT INTO plan_entitlements (
    id,
    plan_id,
    feature_key,
    allowed,
    quota_monthly,
    quota_total,
    max_items,
    reset_period
)
VALUES
    (
        '10000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000001',
        'AI_CREDITS_MONTHLY_LIMIT',
        true,
        20,
        NULL,
        NULL,
        'MONTHLY'
    ),
    (
        '10000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000003',
        'AI_CREDITS_MONTHLY_LIMIT',
        true,
        200,
        NULL,
        NULL,
        'MONTHLY'
    );

-- ============================================================
-- 8. USER ENTITLEMENT GRANTS
-- ============================================================

CREATE TABLE user_entitlement_grants (
                                         id uuid PRIMARY KEY,
                                         user_id uuid NOT NULL,
                                         feature_key varchar(100) NOT NULL,
                                         source varchar(40) NOT NULL,
                                         allowed boolean,
                                         quota_monthly integer,
                                         quota_total integer,
                                         max_items integer,
                                         valid_from timestamptz,
                                         valid_until timestamptz,

                                         CONSTRAINT fk_user_entitlement_grants_user
                                             FOREIGN KEY (user_id)
                                                 REFERENCES users(id)
                                                 ON UPDATE RESTRICT
                                                 ON DELETE RESTRICT,

                                         CONSTRAINT chk_user_entitlement_grants_source
                                             CHECK (
                                                 source IN (
                                                            'MIGRATION',
                                                            'BETA_PROGRAM',
                                                            'PROMOTION',
                                                            'PARTNER_GRANT',
                                                            'COMPENSATION',
                                                            'PURCHASED_PACK',
                                                            'ADMIN_GRANT'
                                                     )
                                                 ),

                                         CONSTRAINT chk_user_entitlement_grants_values
                                             CHECK (
                                                 allowed IS NOT NULL
                                                     OR quota_monthly IS NOT NULL
                                                     OR quota_total IS NOT NULL
                                                     OR max_items IS NOT NULL
                                                 ),

                                         CONSTRAINT chk_user_entitlement_grants_quota_monthly
                                             CHECK (
                                                 quota_monthly IS NULL
                                                     OR quota_monthly >= 0
                                                 ),

                                         CONSTRAINT chk_user_entitlement_grants_quota_total
                                             CHECK (
                                                 quota_total IS NULL
                                                     OR quota_total >= 0
                                                 ),

                                         CONSTRAINT chk_user_entitlement_grants_max_items
                                             CHECK (
                                                 max_items IS NULL
                                                     OR max_items >= 0
                                                 ),

                                         CONSTRAINT chk_user_entitlement_grants_period
                                             CHECK (
                                                 valid_from IS NULL
                                                     OR valid_until IS NULL
                                                     OR valid_until >= valid_from
                                                 )
);

CREATE INDEX idx_user_entitlement_grants_user_id
    ON user_entitlement_grants(user_id);

CREATE INDEX idx_user_entitlement_grants_user_feature
    ON user_entitlement_grants(user_id, feature_key);

CREATE INDEX idx_user_entitlement_grants_validity
    ON user_entitlement_grants(valid_from, valid_until);

-- ============================================================
-- 9. ORGANIZATION ENTITLEMENT GRANTS
-- ============================================================

CREATE TABLE organization_entitlement_grants (
                                                 id uuid PRIMARY KEY,
                                                 organization_id uuid NOT NULL,
                                                 feature_key varchar(100) NOT NULL,
                                                 source varchar(40) NOT NULL,
                                                 allowed boolean,
                                                 quota_monthly integer,
                                                 quota_total integer,
                                                 max_items integer,
                                                 valid_from timestamptz,
                                                 valid_until timestamptz,

                                                 CONSTRAINT fk_org_entitlement_grants_organization
                                                     FOREIGN KEY (organization_id)
                                                         REFERENCES organizations(id)
                                                         ON UPDATE RESTRICT
                                                         ON DELETE RESTRICT,

                                                 CONSTRAINT chk_org_entitlement_grants_source
                                                     CHECK (
                                                         source IN (
                                                                    'MIGRATION',
                                                                    'BETA_PROGRAM',
                                                                    'PROMOTION',
                                                                    'PARTNER_GRANT',
                                                                    'COMPENSATION',
                                                                    'PURCHASED_PACK',
                                                                    'ADMIN_GRANT'
                                                             )
                                                         ),

                                                 CONSTRAINT chk_org_entitlement_grants_values
                                                     CHECK (
                                                         allowed IS NOT NULL
                                                             OR quota_monthly IS NOT NULL
                                                             OR quota_total IS NOT NULL
                                                             OR max_items IS NOT NULL
                                                         ),

                                                 CONSTRAINT chk_org_entitlement_grants_quota_monthly
                                                     CHECK (
                                                         quota_monthly IS NULL
                                                             OR quota_monthly >= 0
                                                         ),

                                                 CONSTRAINT chk_org_entitlement_grants_quota_total
                                                     CHECK (
                                                         quota_total IS NULL
                                                             OR quota_total >= 0
                                                         ),

                                                 CONSTRAINT chk_org_entitlement_grants_max_items
                                                     CHECK (
                                                         max_items IS NULL
                                                             OR max_items >= 0
                                                         ),

                                                 CONSTRAINT chk_org_entitlement_grants_period
                                                     CHECK (
                                                         valid_from IS NULL
                                                             OR valid_until IS NULL
                                                             OR valid_until >= valid_from
                                                         )
);

CREATE INDEX idx_organization_entitlement_grants_organization_id
    ON organization_entitlement_grants(organization_id);

CREATE INDEX idx_org_entitlement_grants_org_feature
    ON organization_entitlement_grants(
                                       organization_id,
                                       feature_key
        );

CREATE INDEX idx_org_entitlement_grants_validity
    ON organization_entitlement_grants(
                                       valid_from,
                                       valid_until
        );

-- ============================================================
-- 10. ORGANIZATION AI CREDIT WALLETS
-- ============================================================

CREATE TABLE organization_ai_credit_wallets (
                                                id uuid PRIMARY KEY,
                                                organization_id uuid NOT NULL,
                                                monthly_limit integer NOT NULL,
                                                used_this_period integer NOT NULL,
                                                remaining integer NOT NULL,
                                                period_start date NOT NULL,
                                                period_end date NOT NULL,
                                                version bigint NOT NULL DEFAULT 0,
                                                created_at timestamptz NOT NULL,
                                                updated_at timestamptz NOT NULL,

                                                CONSTRAINT uk_org_ai_credit_wallets_organization_id
                                                    UNIQUE (organization_id),

                                                CONSTRAINT fk_org_ai_credit_wallets_organization
                                                    FOREIGN KEY (organization_id)
                                                        REFERENCES organizations(id)
                                                        ON UPDATE RESTRICT
                                                        ON DELETE RESTRICT,

                                                CONSTRAINT chk_org_ai_wallet_monthly_limit
                                                    CHECK (monthly_limit >= 0),

                                                CONSTRAINT chk_org_ai_wallet_used
                                                    CHECK (
                                                        used_this_period >= 0
                                                            AND used_this_period <= monthly_limit
                                                        ),

                                                CONSTRAINT chk_org_ai_wallet_remaining
                                                    CHECK (
                                                        remaining = monthly_limit - used_this_period
                                                        ),

                                                CONSTRAINT chk_org_ai_wallet_period
                                                    CHECK (
                                                        period_end >= period_start
                                                        )
);

CREATE INDEX idx_org_ai_credit_wallets_organization_id
    ON organization_ai_credit_wallets(organization_id);

CREATE INDEX idx_org_ai_credit_wallets_period
    ON organization_ai_credit_wallets(
                                      period_start,
                                      period_end
        );
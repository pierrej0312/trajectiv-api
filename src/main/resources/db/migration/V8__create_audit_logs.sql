CREATE TABLE audit_logs (
                            id uuid PRIMARY KEY,

                            actor_user_id uuid,
                            organization_id uuid,

                            action varchar(100) NOT NULL,
                            target_type varchar(80) NOT NULL,
                            target_id uuid,

                            outcome varchar(30) NOT NULL,

                            metadata jsonb NOT NULL DEFAULT '{}'::jsonb,

                            correlation_id varchar(100),
                            ip_address varchar(64),
                            user_agent varchar(512),

                            occurred_at timestamptz NOT NULL,

                            CONSTRAINT fk_audit_logs_actor_user
                                FOREIGN KEY (actor_user_id)
                                    REFERENCES users(id)
                                    ON UPDATE RESTRICT
                                    ON DELETE SET NULL,

                            CONSTRAINT fk_audit_logs_organization
                                FOREIGN KEY (organization_id)
                                    REFERENCES organizations(id)
                                    ON UPDATE RESTRICT
                                    ON DELETE SET NULL,

                            CONSTRAINT chk_audit_logs_action_not_blank
                                CHECK (btrim(action) <> ''),

                            CONSTRAINT chk_audit_logs_target_type_not_blank
                                CHECK (btrim(target_type) <> ''),

                            CONSTRAINT chk_audit_logs_outcome
                                CHECK (
                                    outcome IN (
                                                'SUCCESS',
                                                'FAILURE'
                                        )
                                    )
);

CREATE INDEX idx_audit_logs_actor_user_id
    ON audit_logs(actor_user_id);

CREATE INDEX idx_audit_logs_organization_id
    ON audit_logs(organization_id);

CREATE INDEX idx_audit_logs_action
    ON audit_logs(action);

CREATE INDEX idx_audit_logs_target
    ON audit_logs(target_type, target_id);

CREATE INDEX idx_audit_logs_occurred_at
    ON audit_logs(occurred_at DESC);

CREATE INDEX idx_audit_logs_organization_occurred_at
    ON audit_logs(
                  organization_id,
                  occurred_at DESC
        );
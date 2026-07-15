UPDATE ai_credit_wallets
SET remaining = monthly_limit - used_this_period
WHERE remaining <> monthly_limit - used_this_period;

ALTER TABLE ai_credit_wallets
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE ai_credit_wallets
    ADD CONSTRAINT chk_ai_wallet_monthly_limit
        CHECK (monthly_limit >= 0);

ALTER TABLE ai_credit_wallets
    ADD CONSTRAINT chk_ai_wallet_used
        CHECK (
            used_this_period >= 0
                AND used_this_period <= monthly_limit
            );

ALTER TABLE ai_credit_wallets
    ADD CONSTRAINT chk_ai_wallet_remaining
        CHECK (
            remaining = monthly_limit - used_this_period
            );

ALTER TABLE ai_credit_wallets
    ADD CONSTRAINT chk_ai_wallet_period
        CHECK (
            period_end >= period_start
            );
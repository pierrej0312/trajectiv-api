ALTER TABLE user_avatar_customizations
    ADD COLUMN top_style VARCHAR(60),
    ADD COLUMN bottom_style VARCHAR(60);

UPDATE user_avatar_customizations
SET top_style = 'SWEATER_01'
WHERE top_style IS NULL;

UPDATE user_avatar_customizations
SET bottom_style = 'JEANS_01'
WHERE bottom_style IS NULL;

ALTER TABLE user_avatar_customizations
    ALTER COLUMN top_style SET DEFAULT 'SWEATER_01',
    ALTER COLUMN top_style SET NOT NULL,
    ALTER COLUMN bottom_style SET DEFAULT 'JEANS_01',
    ALTER COLUMN bottom_style SET NOT NULL;

ALTER TABLE user_avatar_customizations
    ADD CONSTRAINT chk_user_avatar_customizations_top_style
        CHECK (top_style IN ('SHIRT_01', 'SWEATER_01')),

    ADD CONSTRAINT chk_user_avatar_customizations_bottom_style
        CHECK (bottom_style IN ('JEANS_01'));

CREATE INDEX idx_user_avatar_customizations_top_style
    ON user_avatar_customizations(top_style);

CREATE INDEX idx_user_avatar_customizations_bottom_style
    ON user_avatar_customizations(bottom_style);
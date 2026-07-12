-- V5__merge_shirt_and_sweater_avatar_top_style.sql

-- 1. Retirer l’ancienne contrainte.
ALTER TABLE user_avatar_customizations
    DROP CONSTRAINT IF EXISTS chk_user_avatar_customizations_top_style;

-- 2. Retirer temporairement l’ancien default.
ALTER TABLE user_avatar_customizations
    ALTER COLUMN top_style DROP DEFAULT;

-- 3. Migrer les anciennes valeurs.
UPDATE user_avatar_customizations
SET top_style = 'SHIRT_SWEATER_01'
WHERE top_style IN (
                    'SHIRT_01',
                    'SWEATER_01'
    );

-- 4. Sécuriser les éventuelles valeurs nulles.
UPDATE user_avatar_customizations
SET top_style = 'SHIRT_SWEATER_01'
WHERE top_style IS NULL;

-- 5. Nouveau default.
ALTER TABLE user_avatar_customizations
    ALTER COLUMN top_style
        SET DEFAULT 'SHIRT_SWEATER_01';

-- 6. La colonne reste obligatoire.
ALTER TABLE user_avatar_customizations
    ALTER COLUMN top_style SET NOT NULL;

-- 7. Nouvelle contrainte.
ALTER TABLE user_avatar_customizations
    ADD CONSTRAINT chk_user_avatar_customizations_top_style
        CHECK (
            top_style IN (
                'SHIRT_SWEATER_01'
                )
            );

COMMENT ON COLUMN user_avatar_customizations.top_style IS
    'Style de haut. SHIRT_SWEATER_01 affiche ensemble shirt_01 et sweater_01.';
CREATE TABLE user_avatar_customizations (
                                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                            user_id UUID NOT NULL UNIQUE,

                                            body_type VARCHAR(60) NOT NULL,
                                            skin_tone VARCHAR(60) NOT NULL,
                                            skin_intensity SMALLINT NOT NULL DEFAULT 0,

                                            hair_style VARCHAR(60) NOT NULL,
                                            hair_color VARCHAR(7) NOT NULL,

                                            beard_style VARCHAR(60) NOT NULL,
                                            beard_color VARCHAR(7) NOT NULL,

                                            source_photo_file_id UUID NULL,
                                            face_texture_file_id UUID NULL,

                                            created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                            updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

                                            CONSTRAINT fk_user_avatar_customizations_user
                                                FOREIGN KEY (user_id)
                                                    REFERENCES users(id)
                                                    ON DELETE CASCADE,

                                            CONSTRAINT fk_user_avatar_customizations_source_photo_file
                                                FOREIGN KEY (source_photo_file_id)
                                                    REFERENCES user_files(id)
                                                    ON DELETE SET NULL,

                                            CONSTRAINT fk_user_avatar_customizations_face_texture_file
                                                FOREIGN KEY (face_texture_file_id)
                                                    REFERENCES user_files(id)
                                                    ON DELETE SET NULL,

                                            CONSTRAINT chk_user_avatar_customizations_skin_intensity
                                                CHECK (skin_intensity BETWEEN -2 AND 2),

                                            CONSTRAINT chk_user_avatar_customizations_hair_color
                                                CHECK (hair_color ~ '^#[0-9A-Fa-f]{6}$'),

    CONSTRAINT chk_user_avatar_customizations_beard_color
        CHECK (beard_color ~ '^#[0-9A-Fa-f]{6}$')
);

CREATE INDEX idx_user_avatar_customizations_user_id
    ON user_avatar_customizations(user_id);

CREATE INDEX idx_user_avatar_customizations_body_type
    ON user_avatar_customizations(body_type);

CREATE INDEX idx_user_avatar_customizations_hair_style
    ON user_avatar_customizations(hair_style);
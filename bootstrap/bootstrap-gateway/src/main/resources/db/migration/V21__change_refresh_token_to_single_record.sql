DELETE FROM refresh_tokens;

ALTER TABLE refresh_tokens
    DROP COLUMN token_value,
    ADD COLUMN current_token VARCHAR(255) NOT NULL,
    ADD COLUMN previous_token VARCHAR(255) NULL,
    ADD COLUMN refreshed_at TIMESTAMP NULL,
    ADD CONSTRAINT uk_refresh_tokens_user_family UNIQUE (user_id, family);

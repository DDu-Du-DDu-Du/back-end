CREATE TABLE IF NOT EXISTS refresh_tokens
(
    token_value VARCHAR(255) NOT NULL,
    user_id     BIGINT       NOT NULL,
    family      INT          NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_refresh_token_id PRIMARY KEY (token_value)
);
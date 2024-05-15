CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id          BIGINT AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    family      INT          NOT NULL,
    token_value VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_refresh_token_id PRIMARY KEY (id),
    CONSTRAINT fk_refresh_token_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE TABLE IF NOT EXISTS announcements
(
    id         BIGINT AUTO_INCREMENT,
    user_id    BIGINT        NOT NULL,
    title      VARCHAR(50)   NOT NULL,
    contents   VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_announcement_id PRIMARY KEY (id),
    CONSTRAINT fk_announcement_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS reminders
(
    id          BIGINT AUTO_INCREMENT,
    user_id     BIGINT    NOT NULL,
    todo_id     BIGINT    NOT NULL,
    reminds_at  TIMESTAMP NOT NULL,
    reminded_at TIMESTAMP NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_reminder_id PRIMARY KEY (id),
    CONSTRAINT fk_reminder_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_reminder_todo_id FOREIGN KEY (todo_id) REFERENCES todos (id) ON DELETE CASCADE
);

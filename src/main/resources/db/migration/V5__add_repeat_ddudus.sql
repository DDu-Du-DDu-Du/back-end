-- REPEATABLE DDUDU
CREATE TABLE IF NOT EXISTS repeat_ddudus
(
    id          BIGINT AUTO_INCREMENT,
    goal_id     BIGINT       NOT NULL,
    name        VARCHAR(50)  NOT NULL,
    repeat_type VARCHAR(20)  NOT NULL,
    repeat_info VARCHAR(255) NULL,
    start_date  DATE         NOT NULL,
    end_date    DATE         NOT NULL,
    begin_at    TIME         NULL,
    end_at      TIME         NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_repeat_ddudus_id PRIMARY KEY (id),
    CONSTRAINT fk_repeat_ddudus_goal_id FOREIGN KEY (goal_id) REFERENCES goals (id)
);

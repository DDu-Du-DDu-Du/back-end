-- Drop previous draft notification schema
DROP TABLE IF EXISTS notifications;

-- 뚜두에 미리알림 정보 추가
ALTER TABLE ddudus
    ADD remind_at TIME NULL;

-- DAILY BRIEFING LOG
CREATE TABLE IF NOT EXISTS daily_briefing_logs (
    id             BIGINT AUTO_INCREMENT,
    user_id        BIGINT NOT NULL,
    briefing_date  DATE NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_daily_briefing_log_id PRIMARY KEY (id),
    CONSTRAINT uq_daily_briefing UNIQUE (user_id, briefing_date),
    CONSTRAINT fk_daily_briefing_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

-- NOTIFICATION EVENT
CREATE TABLE IF NOT EXISTS notification_events (
    id            BIGINT AUTO_INCREMENT,
    type_code     VARCHAR(20) NOT NULL,
    sender_id     BIGINT NULL,
    receiver_id   BIGINT NOT NULL,
    context_id    BIGINT NOT NULL,
    fired_at      TIMESTAMP NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_notification_event_id PRIMARY KEY (id),
    CONSTRAINT fk_notification_event_sender_id FOREIGN KEY (sender_id) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_notifiaction_event_receiver_id FOREIGN KEY (receiver_id) REFERENCES users (id)
);

-- NOTIFICATION INBOX
CREATE TABLE IF NOT EXISTS notification_inboxes (
    id            BIGINT AUTO_INCREMENT,
    user_id       BIGINT NOT NULL,
    sender_id     BIGINT NULL,
    event_id      BIGINT NOT NULL,
    type_code     VARCHAR(20) NOT NULL,
    title         VARCHAR(50) NULL,
    body          VARCHAR(200) NULL,
    read_at       TIMESTAMP NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_notification_inbox_id PRIMARY KEY (id),
    CONSTRAINT fk_notification_inbox_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_notification_inbox_sender_id FOREIGN KEY (sender_id) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_notification_inbox_event_id FOREIGN KEY (event_id) REFERENCES notification_events (id)
);

-- DEVICE TOKEN
CREATE TABLE IF NOT EXISTS notification_device_tokens (
    id            BIGINT AUTO_INCREMENT,
    user_id       BIGINT NOT NULL,
    channel       VARCHAR(16) NOT NULL,
    token         VARCHAR(512) NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_notification_device_token_id PRIMARY KEY (id),
    CONSTRAINT fk_notification_device_token_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);
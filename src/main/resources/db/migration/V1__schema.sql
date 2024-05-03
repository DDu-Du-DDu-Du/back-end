-- USER
CREATE TABLE IF NOT EXISTS users
(
    id                     BIGINT AUTO_INCREMENT,
    nickname               VARCHAR(20)   NOT NULL,
    username               VARCHAR(20)   NOT NULL,
    introduction           VARCHAR(50)   NULL,
    profile_image_url      VARCHAR(1024) NULL,
    authority              VARCHAR(15)   NOT NULL DEFAULT 'NORMAL',
    status                 VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    follows_after_approval TINYINT(1)    NOT NULL DEFAULT 0,
    template_notification  TINYINT(1)    NOT NULL DEFAULT 1,
    ddudu_notification     TINYINT(1)    NOT NULL DEFAULT 1,
    created_at             TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_user_id PRIMARY KEY (id),
    CONSTRAINT uk_user_username UNIQUE (username)
);

-- GOAL
CREATE TABLE IF NOT EXISTS goals
(
    id         BIGINT AUTO_INCREMENT,
    user_id    BIGINT      NOT NULL,
    name       VARCHAR(50) NOT NULL,
    color      CHAR(6)     NOT NULL DEFAULT '191919',
    privacy    VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',
    status     VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_goal_id PRIMARY KEY (id),
    CONSTRAINT fk_goal_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT ck_color CHECK ( CHAR_LENGTH(color) = 6 )
);

-- DDUDU
CREATE TABLE IF NOT EXISTS ddudus
(
    id           BIGINT AUTO_INCREMENT,
    user_id      BIGINT      NOT NULL,
    goal_id      BIGINT      NOT NULL,
    name         VARCHAR(50) NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'UNCOMPLETED',
    begin_at     TIMESTAMP   NOT NULL,
    end_at       TIMESTAMP   NULL,
    is_postponed TINYINT(1)  NOT NULL DEFAULT 0,
    created_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_todo_id PRIMARY KEY (id),
    CONSTRAINT fk_todo_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_todo_goal_id FOREIGN KEY (goal_id) REFERENCES goals (id)
);

-- FOLLOWING
CREATE TABLE IF NOT EXISTS followings
(
    follower_id BIGINT      NOT NULL,
    followee_id BIGINT      NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'FOLLOWING',
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_friend_follower_followee PRIMARY KEY (follower_id, followee_id),
    CONSTRAINT fk_follower_id FOREIGN KEY (follower_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_followee_id FOREIGN KEY (followee_id) REFERENCES users (id) ON DELETE CASCADE
);

-- PERIOD GOAL
CREATE TABLE IF NOT EXISTS period_goals
(
    id         BIGINT AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    contents   VARCHAR(255) NOT NULL,
    type       VARCHAR(15)  NOT NULL,
    plan_date  DATE         NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_period_goal_id PRIMARY KEY (id),
    CONSTRAINT fk_period_goal_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

-- ACHIEVEMENTS
CREATE TABLE IF NOT EXISTS achievements
(
    id              BIGINT AUTO_INCREMENT,
    name            VARCHAR(20)   NOT NULL,
    description     VARCHAR(255)  NOT NULL,
    badge_image_url VARCHAR(1024) NOT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_achievement_id PRIMARY KEY (id)
);

-- USER ACHIEVEMENTS
CREATE TABLE IF NOT EXISTS user_achievements
(
    id             BIGINT AUTO_INCREMENT,
    user_id        BIGINT     NOT NULL,
    achievement_id BIGINT     NOT NULL,
    is_main        TINYINT(1) NOT NULL DEFAULT 0,
    created_at     TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_user_achievement PRIMARY KEY (id),
    CONSTRAINT fk_user_achievement_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_achievement_achievement_id FOREIGN KEY (achievement_id) REFERENCES achievements (id) ON DELETE CASCADE
);

-- NOTIFICATION
CREATE TABLE IF NOT EXISTS notifications
(
    id          BIGINT AUTO_INCREMENT,
    receiver_id BIGINT       NOT NULL,
    message     VARCHAR(255) NOT NULL,
    type        VARCHAR(15)  NOT NULL,
    is_read     TINYINT(1)   NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_notification_id PRIMARY KEY (id),
    CONSTRAINT fk_notification_receiver_id FOREIGN KEY (receiver_id) REFERENCES users (id)
);

-- TEMPLATES
CREATE TABLE IF NOT EXISTS templates
(
    id              BIGINT AUTO_INCREMENT,
    goal_id         BIGINT       NULL,
    create_by       BIGINT       NOT NULL,
    title           VARCHAR(50)  NOT NULL,
    description     VARCHAR(1000),
    time_estimation VARCHAR(20)  NULL,
    sharing_message VARCHAR(100) NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_template_id PRIMARY KEY (id),
    CONSTRAINT fk_template_goal_id FOREIGN KEY (goal_id) REFERENCES goals (id),
    CONSTRAINT fk_template_create_by FOREIGN KEY (create_by) REFERENCES users (id)
);

-- Likes
CREATE TABLE IF NOT EXISTS likes
(
    id         BIGINT AUTO_INCREMENT,
    user_id    BIGINT    NOT NULL,
    todo_id    BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_like_id PRIMARY KEY (id),
    CONSTRAINT fk_like_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_like_todo_id FOREIGN KEY (todo_id) REFERENCES ddudus (id) ON DELETE CASCADE
);


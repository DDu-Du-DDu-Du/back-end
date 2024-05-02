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
    id          BIGINT AUTO_INCREMENT,
    follower_id BIGINT      NOT NULL,
    followee_id BIGINT      NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'FOLLOWING',
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_friend_id PRIMARY KEY (id),
    CONSTRAINT fk_follower_id FOREIGN KEY (follower_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_followee_id FOREIGN KEY (followee_id) REFERENCES users (id) ON DELETE CASCADE
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


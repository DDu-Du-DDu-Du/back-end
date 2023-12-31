-- USER
CREATE TABLE IF NOT EXISTS users
(
    id                BIGINT       AUTO_INCREMENT,
    optional_username VARCHAR(20)  NULL,
    email             VARCHAR(50)  NOT NULL,
    password          VARCHAR(255) NOT NULL,
    nickname          VARCHAR(20)  NOT NULL,
    introduction      VARCHAR(50)  NULL,
    authority         VARCHAR(15)  NOT NULL DEFAULT 'NORMAL',
    status            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted        TINYINT(1)   NOT NULL DEFAULT 0,
    CONSTRAINT pk_user_id PRIMARY KEY (id),
    CONSTRAINT uk_user_optional_username UNIQUE (optional_username),
    CONSTRAINT uk_user_email UNIQUE (email)
);

-- GOAL
CREATE TABLE IF NOT EXISTS goal
(
    id         BIGINT      AUTO_INCREMENT,
    user_id    BIGINT      NOT NULL,
    name       VARCHAR(50) NOT NULL,
    color      CHAR(6)     NOT NULL DEFAULT '191919',
    privacy    VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',
    status     VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1)  NOT NULL DEFAULT 0,
    CONSTRAINT pk_goal_id PRIMARY KEY (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT ck_color CHECK ( CHAR_LENGTH(color) = 6 )
);

-- TO DO
CREATE TABLE IF NOT EXISTS todo
(
    id         BIGINT      AUTO_INCREMENT,
    goal_id    BIGINT      NOT NULL,
    name       VARCHAR(50) NOT NULL,
    status     VARCHAR(20) NOT NULL DEFAULT 'UNCOMPLETED',
    begin_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_at     TIMESTAMP   NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1)  NOT NULL DEFAULT 0,
    CONSTRAINT pk_todo_id PRIMARY KEY (id),
    CONSTRAINT fk_goal_id FOREIGN KEY (goal_id) REFERENCES goal (id)
);

-- FRIEND
CREATE TABLE IF NOT EXISTS followings
(
    id          BIGINT AUTO_INCREMENT,
    follower_id BIGINT NOT NULL,
    followee_id BIGINT NOT NULL,
    status      VARCHAR(20) NOT NULL,
    CONSTRAINT pk_friend_id PRIMARY KEY (id),
    CONSTRAINT fk_follower_id FOREIGN KEY (follower_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_followee_id FOREIGN KEY (followee_id) REFERENCES users (id) ON DELETE CASCADE
);

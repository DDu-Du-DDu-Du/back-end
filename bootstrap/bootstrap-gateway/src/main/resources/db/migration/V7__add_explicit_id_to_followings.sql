ALTER TABLE followings
    DROP PRIMARY KEY,
    ADD COLUMN id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST,
    ADD CONSTRAINT uq_follower_followee UNIQUE (follower_id, followee_id);
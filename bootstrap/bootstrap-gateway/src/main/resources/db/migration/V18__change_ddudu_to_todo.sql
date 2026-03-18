ALTER TABLE users
    CHANGE COLUMN ddudu_notification todo_notification TINYINT(1) NOT NULL DEFAULT 1;

ALTER TABLE likes
    DROP FOREIGN KEY fk_like_todo_id;

ALTER TABLE likes
    CHANGE COLUMN ddudu_id todo_id BIGINT NOT NULL;

ALTER TABLE ddudus
    DROP FOREIGN KEY fk_ddudu_repeat_ddudu_id;

ALTER TABLE ddudus
    CHANGE COLUMN repeat_ddudu_id repeat_todo_id BIGINT NULL;

RENAME TABLE repeat_ddudus TO repeat_todos;

ALTER TABLE ddudus
    ADD CONSTRAINT fk_ddudu_repeat_todo_id FOREIGN KEY (repeat_todo_id) REFERENCES repeat_todos(id);

RENAME TABLE ddudus TO todos;
RENAME TABLE template_ddudus TO template_todos;

ALTER TABLE likes
    ADD CONSTRAINT fk_like_todo_id FOREIGN KEY (todo_id) REFERENCES todos (id) ON DELETE CASCADE;

ALTER TABLE ddudus
    ADD COLUMN repeat_ddudu_id BIGINT NULL,
    ADD CONSTRAINT fk_ddudu_repeat_ddudu_id FOREIGN KEY (repeat_ddudu_id) REFERENCES repeat_ddudus(id);

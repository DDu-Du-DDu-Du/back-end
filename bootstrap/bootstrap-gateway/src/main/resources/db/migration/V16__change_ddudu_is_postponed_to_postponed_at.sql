ALTER TABLE ddudus
    ADD COLUMN postponed_at TIMESTAMP NULL AFTER end_at;

ALTER TABLE ddudus
    DROP COLUMN is_postponed;

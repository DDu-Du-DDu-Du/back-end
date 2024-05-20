ALTER TABLE ddudus
    ADD scheduled_on DATE NOT NULL;
ALTER TABLE ddudus
    MODIFY begin_at TIME;
ALTER TABLE ddudus
    MODIFY end_at TIME;
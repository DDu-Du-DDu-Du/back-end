ALTER TABLE users
    ADD week_start_day VARCHAR(3) NOT NULL DEFAULT 'SUN';

ALTER TABLE users
    ADD dark_mode TINYINT(1) NOT NULL DEFAULT 0;

ALTER TABLE users
    ADD active_calendar TINYINT(1) NOT NULL DEFAULT 1;

ALTER TABLE users
    ADD priority_calendar INT NOT NULL DEFAULT 1;

ALTER TABLE users
    ADD active_dashboard TINYINT(1) NOT NULL DEFAULT 1;

ALTER TABLE users
    ADD priority_dashboard INT NOT NULL DEFAULT 2;

ALTER TABLE users
    ADD active_stats TINYINT(1) NOT NULL DEFAULT 1;

ALTER TABLE users
    ADD priority_stats INT NOT NULL DEFAULT 3;

ALTER TABLE users
    ADD realtime_sync_notion TINYINT(1) NOT NULL DEFAULT 0;

ALTER TABLE users
    ADD realtime_sync_google_calendar TINYINT(1) NOT NULL DEFAULT 0;

ALTER TABLE users
    ADD realtime_sync_microsoft_todo TINYINT(1) NOT NULL DEFAULT 0;

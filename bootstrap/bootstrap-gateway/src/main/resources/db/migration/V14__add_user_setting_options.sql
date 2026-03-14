ALTER TABLE users
    ADD COLUMN week_start_day VARCHAR(3) NOT NULL DEFAULT 'SUN' AFTER ddudu_notification,
    ADD COLUMN dark_mode TINYINT(1) NOT NULL DEFAULT 0 AFTER week_start_day,
    ADD COLUMN active_calendar TINYINT(1) NOT NULL DEFAULT 1 AFTER dark_mode,
    ADD COLUMN priority_calendar INT NOT NULL DEFAULT 1 AFTER active_calendar,
    ADD COLUMN active_dashboard TINYINT(1) NOT NULL DEFAULT 1 AFTER priority_calendar,
    ADD COLUMN priority_dashboard INT NOT NULL DEFAULT 2 AFTER active_dashboard,
    ADD COLUMN active_stats TINYINT(1) NOT NULL DEFAULT 1 AFTER priority_dashboard,
    ADD COLUMN priority_stats INT NOT NULL DEFAULT 3 AFTER active_stats,
    ADD COLUMN realtime_sync_notion TINYINT(1) NOT NULL DEFAULT 0 AFTER priority_stats,
    ADD COLUMN realtime_sync_google_calendar TINYINT(1) NOT NULL DEFAULT 0 AFTER realtime_sync_notion,
    ADD COLUMN realtime_sync_microsoft_todo TINYINT(1) NOT NULL DEFAULT 0 AFTER realtime_sync_google_calendar;

-- CLEAR
SET foreign_key_checks = 0;

TRUNCATE users;
TRUNCATE refresh_tokens;
TRUNCATE auth_providers;
TRUNCATE goals;
TRUNCATE todos;
TRUNCATE repeat_todos;
TRUNCATE notification_events;
TRUNCATE daily_briefing_logs;
TRUNCATE period_goals;
TRUNCATE notification_inboxes;
TRUNCATE notification_device_tokens;
TRUNCATE announcements;

SET foreign_key_checks = 1;

-- USERS
INSERT INTO users(id, nickname, username, introduction, profile_image_url, authority, status,
                  follows_after_approval, template_notification, todo_notification, week_start_day,
                  dark_mode, active_calendar, priority_calendar, active_dashboard,
                  priority_dashboard, active_stats, priority_stats, realtime_sync_notion,
                  realtime_sync_google_calendar, realtime_sync_microsoft_todo, created_at, updated_at)
VALUES (1, '결단력있는 딩고', 'determineddingoda25c93c', null, null, 'ADMIN', 'ACTIVE', 0, 1, 1,
        'SUN', 0, 1, 1, 1, 2, 1, 3, 0, 0, 0, '2024-05-17T16:13:48', '2024-05-17T16:13:48');
INSERT INTO users(id, nickname, username, introduction, profile_image_url, authority, status,
                  follows_after_approval, template_notification, todo_notification, week_start_day,
                  dark_mode, active_calendar, priority_calendar, active_dashboard,
                  priority_dashboard, active_stats, priority_stats, realtime_sync_notion,
                  realtime_sync_google_calendar, realtime_sync_microsoft_todo, created_at, updated_at)
VALUES (2, '짜증난 강아지', 'irritatedpuppy30b6f3ed', null, null, 'ADMIN', 'ACTIVE', 0, 1, 1,
        'SUN', 0, 1, 1, 1, 2, 1, 3, 0, 0, 0, '2024-08-18T21:13:02', '2024-08-18T21:13:02');
# 투두 구글 계정

-- REFRESH TOKEN
INSERT INTO refresh_tokens(id, user_id, family, current_token, previous_token, refreshed_at,
                           created_at, updated_at)
VALUES (1, 1, 1,
        'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJtYXJjby1kZHVkdSIsInN1YiI6IjEtMSIsImlhdCI6MTcxNTkzMDAyOH0.ig76OI9bD2Da0-NsIgWoaM3rzewvm_Y0HUbjOjJUOG-gBZHk_k5CCrCSynRSZXwcttqdiwLSKhHYzj5zUUf8ZQ',
        null,
        '2024-05-17T16:13:48',
        '2024-05-17T16:13:48', '2024-05-17T16:13:48');

-- AUTH PROVIDERS
INSERT INTO auth_providers(id, user_id, provider_type, provider_id, created_at, updated_at)
VALUES (1, 1, 'KAKAO', '3477771378', '2024-05-17T07:13:48', '2024-05-17T07:13:48');
INSERT INTO auth_providers(id, user_id, provider_type, provider_id, created_at, updated_at)
VALUES (2, 2, 'KAKAO', '3473310045', '2024-08-18T12:13:02', '2024-08-18T12:13:02');
# 투두 구글 계정

-- GOALS
INSERT INTO goals(id, user_id, name, color, privacy, status, priority, created_at, updated_at)
VALUES (1, 1, '프로젝트', '191919', 'PUBLIC', 'IN_PROGRESS', 1, '2024-05-17T07:13:48', '2024-05-17T07:13:48');
INSERT INTO goals(id, user_id, name, color, privacy, status, priority, created_at, updated_at)
VALUES (2, 1, 'Study', '999999', 'PRIVATE', 'IN_PROGRESS', 2, '2024-05-18T07:13:48',
        '2024-05-17T07:13:48');
INSERT INTO goals(id, user_id, name, color, privacy, status, priority, created_at, updated_at)
VALUES (3, 2, '프로젝트', '191919', 'PUBLIC', 'IN_PROGRESS', 1, '2024-08-18T21:13:02', '2024-08-18T21:13:02');
INSERT INTO goals(id, user_id, name, color, privacy, status, priority, created_at, updated_at)
VALUES (4, 2, '프로젝트2', '191919', 'PUBLIC', 'IN_PROGRESS', 2, '2024-08-18T21:13:02',
        '2024-08-18T21:13:02');

-- TODOS
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (1, 1, 1, '데일리 스크럼', 'COMPLETE', '11:00:00', '11:30:00', null, '2024-05-17T06:30:48',
        '2024-05-17T06:30:48', '2024-05-17');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (2, 1, 1, '투두투두 배포하기', 'UNCOMPLETED', null, null, null, '2024-05-17T07:13:48',
        '2024-05-17T07:13:48', '2024-05-17');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (3, 1, 1, '목표 도메인 리팩토링', 'UNCOMPLETED', null, null, null, '2024-05-17T07:30:48',
        '2024-05-17T07:30:48', '2024-05-17');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (4, 1, 2, '알고리즘 스터디1', 'UNCOMPLETED', '20:00:00', '21:00:00', null, '2024-05-17T07:30:48',
        '2024-05-17T07:30:48', '2024-05-17');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (5, 1, 2, '알고리즘 스터디2', 'UNCOMPLETED', '20:00:00', '21:00:00', null, '2024-05-17T07:30:48',
        '2024-05-17T07:30:48', '2024-05-17');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (6, 2, 3, '데일리 스크럼', 'COMPLETE', '09:00:00', '09:30:00', null, '2024-08-01T08:15:00',
        '2024-08-01T08:15:00', '2024-08-01');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (7, 2, 3, '주간 회의', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-23T09:45:00', '2024-07-23T09:45:00', '2024-07-23');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (8, 2, 3, '코드 리뷰', 'COMPLETE', '14:00:00', '14:30:00', null, '2024-08-05T12:30:00',
        '2024-08-05T12:30:00', '2024-08-05');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (9, 2, 3, '팀 회의 준비', 'COMPLETE', '13:00:00', '13:30:00', null, '2024-08-06T13:00:00',
        '2024-08-06T13:00:00', '2024-08-06');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (10, 2, 3, '기술 블로그 작성', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-18T11:00:00', '2024-07-18T11:00:00', '2024-07-18');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (11, 2, 3, '프로젝트 미팅', 'COMPLETE', '10:00:00', '10:30:00', '2024-07-15T10:15:00', '2024-07-15T10:15:00', '2024-07-15T10:15:00', '2024-07-15');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (12, 2, 3, '데일리 체크인', 'COMPLETE', '11:00:00', '11:15:00', null, '2024-08-11T11:00:00',
        '2024-08-11T11:00:00', '2024-08-11');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (13, 2, 3, '코드 리뷰', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-30T12:00:00', '2024-07-30T12:00:00', '2024-07-30');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (14, 2, 3, '기술 문서 작성', 'COMPLETE', '15:00:00', '15:45:00', null, '2024-08-14T15:00:00',
        '2024-08-14T15:00:00', '2024-08-14');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (15, 2, 3, '팀 피드백', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-22T16:00:00', '2024-07-22T16:00:00', '2024-07-22');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (16, 2, 3, '데일리 스크럼', 'COMPLETE', '09:30:00', '10:00:00', null, '2024-08-12T09:30:00',
        '2024-08-12T09:30:00', '2024-08-12');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (17, 2, 3, '주간 계획 수립', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-16T10:45:00', '2024-07-16T10:45:00', '2024-07-16');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (18, 2, 3, '코드 리뷰', 'COMPLETE', '11:30:00', '12:00:00', null, '2024-08-03T11:30:00',
        '2024-08-03T11:30:00', '2024-08-03');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (19, 2, 3, '팀 회의', 'COMPLETE', '14:00:00', '14:30:00', null, '2024-08-04T14:00:00',
        '2024-08-04T14:00:00', '2024-08-04');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (20, 2, 3, '기술 블로그 작성', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-26T15:30:00', '2024-07-26T15:30:00', '2024-07-26');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (21, 2, 3, '주간 회의', 'COMPLETE', '10:00:00', '10:30:00', null, '2024-08-02T10:00:00',
        '2024-08-02T10:00:00', '2024-08-02');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (22, 2, 3, '코드 리뷰', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-28T13:00:00', '2024-07-28T13:00:00', '2024-07-28');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (23, 2, 3, '기술 문서 작성', 'COMPLETE', '13:30:00', '14:00:00', null, '2024-08-10T13:30:00',
        '2024-08-10T13:30:00', '2024-08-10');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (24, 2, 3, '팀 피드백', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-25T11:00:00', '2024-07-25T11:00:00', '2024-07-25');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (25, 2, 3, '데일리 스크럼', 'COMPLETE', '09:00:00', '09:30:00', null, '2024-08-07T08:15:00',
        '2024-08-07T08:15:00', '2024-08-07');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (26, 2, 3, '주간 회의', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-29T09:45:00', '2024-07-29T09:45:00', '2024-07-29');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (27, 2, 3, '코드 리뷰', 'COMPLETE', '14:00:00', '14:30:00', null, '2024-08-05T12:30:00',
        '2024-08-05T12:30:00', '2024-08-05');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (28, 2, 3, '팀 회의 준비', 'COMPLETE', '13:00:00', '13:30:00', null, '2024-08-06T13:00:00',
        '2024-08-06T13:00:00', '2024-08-06');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (29, 2, 3, '기술 블로그 작성', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-18T11:00:00', '2024-07-18T11:00:00', '2024-07-18');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (30, 2, 3, '프로젝트 미팅', 'COMPLETE', '10:00:00', '10:30:00', '2024-07-15T10:15:00', '2024-07-15T10:15:00', '2024-07-15T10:15:00', '2024-07-15');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (31, 2, 3, '데일리 체크인', 'COMPLETE', '11:00:00', '11:15:00', null, '2024-08-11T11:00:00',
        '2024-08-11T11:00:00', '2024-08-11');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (32, 2, 3, '코드 리뷰', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-30T12:00:00', '2024-07-30T12:00:00', '2024-07-30');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (33, 2, 3, '기술 문서 작성', 'COMPLETE', '15:00:00', '15:45:00', null, '2024-08-14T15:00:00',
        '2024-08-14T15:00:00', '2024-08-14');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (34, 2, 3, '팀 피드백', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-22T16:00:00', '2024-07-22T16:00:00', '2024-07-22');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (35, 2, 3, '데일리 스크럼', 'COMPLETE', '09:30:00', '10:00:00', null, '2024-08-12T09:30:00',
        '2024-08-12T09:30:00', '2024-08-12');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (36, 2, 3, '주간 계획 수립', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-16T10:45:00', '2024-07-16T10:45:00', '2024-07-16');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (37, 2, 3, '코드 리뷰', 'COMPLETE', '11:30:00', '12:00:00', null, '2024-08-03T11:30:00',
        '2024-08-03T11:30:00', '2024-08-03');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (38, 2, 3, '팀 회의', 'COMPLETE', '14:00:00', '14:30:00', null, '2024-08-04T14:00:00',
        '2024-08-04T14:00:00', '2024-08-04');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (39, 2, 3, '기술 블로그 작성', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-26T15:30:00', '2024-07-26T15:30:00', '2024-07-26');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (40, 2, 3, '주간 회의', 'COMPLETE', '10:00:00', '10:30:00', null, '2024-08-02T10:00:00',
        '2024-08-02T10:00:00', '2024-08-02');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (41, 2, 3, '코드 리뷰', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-28T13:00:00', '2024-07-28T13:00:00', '2024-07-28');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (42, 2, 3, '기술 문서 작성', 'COMPLETE', '13:30:00', '14:00:00', null, '2024-08-10T13:30:00',
        '2024-08-10T13:30:00', '2024-08-10');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (43, 2, 3, '팀 피드백', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-25T11:00:00', '2024-07-25T11:00:00', '2024-07-25');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (44, 2, 3, '데일리 스크럼', 'COMPLETE', '09:00:00', '09:30:00', null, '2024-08-01T08:15:00',
        '2024-08-01T08:15:00', '2024-08-01');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (45, 2, 3, '주간 회의', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-23T09:45:00', '2024-07-23T09:45:00', '2024-07-23');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (46, 2, 3, '코드 리뷰', 'COMPLETE', '14:00:00', '14:30:00', null, '2024-08-05T12:30:00',
        '2024-08-05T12:30:00', '2024-08-05');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (47, 2, 3, '팀 회의 준비', 'COMPLETE', '13:00:00', '13:30:00', null, '2024-08-06T13:00:00',
        '2024-08-06T13:00:00', '2024-08-06');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (48, 2, 3, '기술 블로그 작성', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-18T11:00:00', '2024-07-18T11:00:00', '2024-07-18');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (49, 2, 3, '프로젝트 미팅', 'COMPLETE', '10:00:00', '10:30:00', '2024-07-15T10:15:00', '2024-07-15T10:15:00', '2024-07-15T10:15:00', '2024-07-15');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (50, 2, 3, '데일리 체크인', 'COMPLETE', '11:00:00', '11:15:00', null, '2024-08-11T11:00:00',
        '2024-08-11T11:00:00', '2024-08-11');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (51, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', null, '2024-08-01T07:00:00',
        '2024-08-01T08:00:00', '2024-08-01');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (52, 2, 4, '요가 클래스', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-23T08:15:00', '2024-07-23T08:15:00', '2024-07-23');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (53, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', null, '2024-08-05T06:30:00',
        '2024-08-05T07:00:00', '2024-08-05');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (54, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', null, '2024-08-02T07:00:00',
        '2024-08-02T08:00:00', '2024-08-02');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (55, 2, 4, '요가 클래스', 'UNCOMPLETED', '10:00:00', null, null, '2024-07-25T08:15:00', '2024-07-25T08:15:00', '2024-07-25');

INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (56, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', null, '2024-08-03T06:30:00',
        '2024-08-03T07:00:00', '2024-08-03');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (57, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', null, '2024-08-06T07:00:00',
        '2024-08-06T08:00:00', '2024-08-06');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (58, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, null, '2024-07-27T08:15:00', '2024-07-27T08:15:00', '2024-07-27');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (59, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', null, '2024-08-07T06:30:00',
        '2024-08-07T07:00:00', '2024-08-07');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (60, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', null, '2024-08-08T07:00:00',
        '2024-08-08T08:00:00', '2024-08-08');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (61, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, null, '2024-07-29T08:15:00', '2024-07-29T08:15:00', '2024-07-29');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (62, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', null, '2024-08-09T06:30:00',
        '2024-08-09T07:00:00', '2024-08-09');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (63, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', null, '2024-08-10T07:00:00',
        '2024-08-10T08:00:00', '2024-08-10');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (64, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, null, '2024-07-31T08:15:00', '2024-07-31T08:15:00', '2024-07-31');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (65, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', null, '2024-08-11T06:30:00',
        '2024-08-11T07:00:00', '2024-08-11');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (66, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', null, '2024-08-12T07:00:00',
        '2024-08-12T08:00:00', '2024-08-12');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (67, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, null, '2024-07-19T08:15:00', '2024-07-19T08:15:00', '2024-07-19');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (68, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', null, '2024-08-13T06:30:00',
        '2024-08-13T07:00:00', '2024-08-13');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (69, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', null, '2024-08-14T07:00:00',
        '2024-08-14T08:00:00', '2024-08-14');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (70, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, null, '2024-07-21T08:15:00', '2024-07-21T08:15:00', '2024-07-21');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (71, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', null, '2024-08-15T06:30:00',
        '2024-08-15T07:00:00', '2024-08-15');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (72, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', null, '2024-08-16T07:00:00',
        '2024-08-16T08:00:00', '2024-08-16');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (73, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, null, '2024-07-30T08:15:00', '2024-07-30T08:15:00', '2024-07-30');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (74, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', null, '2024-08-17T06:30:00',
        '2024-08-17T07:00:00', '2024-08-17');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (75, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', null, '2024-08-18T07:00:00',
        '2024-08-18T08:00:00', '2024-08-18');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (76, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, null, '2024-07-28T08:15:00', '2024-07-28T08:15:00', '2024-07-28');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (77, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', null, '2024-08-19T06:30:00',
        '2024-08-19T07:00:00', '2024-08-19');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (78, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', null, '2024-08-20T07:00:00',
        '2024-08-20T08:00:00', '2024-08-20');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (79, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, null, '2024-07-22T08:15:00', '2024-07-22T08:15:00', '2024-07-22');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (80, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', null, '2024-08-21T06:30:00',
        '2024-08-21T07:00:00', '2024-08-21');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (81, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', null, '2024-08-22T07:00:00',
        '2024-08-22T08:00:00', '2024-08-22');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (82, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, null, '2024-07-17T08:15:00', '2024-07-17T08:15:00', '2024-07-17');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (83, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', null, '2024-08-23T06:30:00',
        '2024-08-23T07:00:00', '2024-08-23');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (84, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', null, '2024-08-24T07:00:00',
        '2024-08-24T08:00:00', '2024-08-24');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (85, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, null, '2024-07-16T08:15:00', '2024-07-16T08:15:00', '2024-07-16');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (86, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', null, '2024-08-25T06:30:00',
        '2024-08-25T07:00:00', '2024-08-25');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (87, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', null, '2024-08-26T07:00:00',
        '2024-08-26T08:00:00', '2024-08-26');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (88, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, null, '2024-07-18T08:15:00', '2024-07-18T08:15:00', '2024-07-18');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (89, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', null, '2024-08-27T06:30:00',
        '2024-08-27T07:00:00', '2024-08-27');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (90, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', null, '2024-08-28T07:00:00',
        '2024-08-28T08:00:00', '2024-08-28');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (91, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, null, '2024-07-24T08:15:00', '2024-07-24T08:15:00', '2024-07-24');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (92, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', null, '2024-08-29T06:30:00',
        '2024-08-29T07:00:00', '2024-08-29');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (93, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', null, '2024-08-30T07:00:00',
        '2024-08-30T08:00:00', '2024-08-30');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (94, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, null, '2024-07-26T08:15:00', '2024-07-26T08:15:00', '2024-07-26');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (95, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', null, '2024-08-31T06:30:00',
        '2024-08-31T07:00:00', '2024-08-31');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (96, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', null, '2024-09-01T07:00:00',
        '2024-09-01T08:00:00', '2024-09-01');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (97, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, null, '2024-07-20T08:15:00', '2024-07-20T08:15:00', '2024-07-20');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (98, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', null, '2024-09-02T06:30:00',
        '2024-09-02T07:00:00', '2024-09-02');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (99, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', null, '2024-09-03T07:00:00',
        '2024-09-03T08:00:00', '2024-09-03');
INSERT INTO todos(id, user_id, goal_id, name, status, begin_at, end_at, postponed_at,
                   created_at, updated_at, scheduled_on)
VALUES (100, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, null, '2024-07-23T08:15:00', '2024-07-23T08:15:00', '2024-07-23');


-- NOTIFICATION EVENTS/INBOXES FOR FIRST 50 TODOS OF USER 2
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (1, 'TODO_REMINDER', 2, 2, 6, '2024-08-01T08:50:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (1, 2, 2, 1, 'TODO_REMINDER', '데일리 스크럼', '투두 시작 10분 전에 알려드려요.', NULL, 6);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (2, 'TODO_REMINDER', 2, 2, 7, '2024-07-23T09:40:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (2, 2, 2, 2, 'TODO_REMINDER', '주간 회의', '투두 시작 20분 전에 알려드려요.', NULL, 7);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (3, 'TODO_REMINDER', 2, 2, 8, '2024-08-05T13:30:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (3, 2, 2, 3, 'TODO_REMINDER', '코드 리뷰', '투두 시작 30분 전에 알려드려요.', NULL, 8);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (4, 'TODO_REMINDER', 2, 2, 9, '2024-08-06T12:15:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (4, 2, 2, 4, 'TODO_REMINDER', '팀 회의 준비', '투두 시작 45분 전에 알려드려요.', NULL, 9);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (5, 'TODO_REMINDER', 2, 2, 10, '2024-07-18T09:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (5, 2, 2, 5, 'TODO_REMINDER', '기술 블로그 작성', '투두 시작 1시간 전에 알려드려요.', NULL, 10);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (6, 'TODO_REMINDER', 2, 2, 11, '2024-07-15T08:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (6, 2, 2, 6, 'TODO_REMINDER', '프로젝트 미팅', '투두 시작 2시간 전에 알려드려요.', NULL, 11);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (7, 'TODO_REMINDER', 2, 2, 12, '2024-08-11T08:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (7, 2, 2, 7, 'TODO_REMINDER', '데일리 체크인', '투두 시작 3시간 전에 알려드려요.', NULL, 12);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (8, 'TODO_REMINDER', 2, 2, 13, '2024-07-30T04:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (8, 2, 2, 8, 'TODO_REMINDER', '코드 리뷰', '투두 시작 6시간 전에 알려드려요.', NULL, 13);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (9, 'TODO_REMINDER', 2, 2, 14, '2024-08-14T03:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (9, 2, 2, 9, 'TODO_REMINDER', '기술 문서 작성', '투두 시작 12시간 전에 알려드려요.', NULL, 14);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (10, 'TODO_REMINDER', 2, 2, 15, '2024-07-21T10:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (10, 2, 2, 10, 'TODO_REMINDER', '팀 피드백', '투두 시작 1일 전에 알려드려요.', NULL, 15);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (11, 'TODO_REMINDER', 2, 2, 16, '2024-08-11T07:30:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (11, 2, 2, 11, 'TODO_REMINDER', '데일리 스크럼', '투두 시작 1일 2시간 전에 알려드려요.', NULL, 16);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (12, 'TODO_REMINDER', 2, 2, 17, '2024-07-15T09:30:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (12, 2, 2, 12, 'TODO_REMINDER', '주간 계획 수립', '투두 시작 1일 30분 전에 알려드려요.', NULL, 17);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (13, 'TODO_REMINDER', 2, 2, 18, '2024-08-03T09:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (13, 2, 2, 13, 'TODO_REMINDER', '코드 리뷰', '투두 시작 2시간 30분 전에 알려드려요.', NULL, 18);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (14, 'TODO_REMINDER', 2, 2, 19, '2024-08-04T12:45:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (14, 2, 2, 14, 'TODO_REMINDER', '팀 회의', '투두 시작 1시간 15분 전에 알려드려요.', NULL, 19);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (15, 'TODO_REMINDER', 2, 2, 20, '2024-07-26T09:55:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (15, 2, 2, 15, 'TODO_REMINDER', '기술 블로그 작성', '투두 시작 5분 전에 알려드려요.', NULL, 20);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (16, 'TODO_REMINDER', 2, 2, 21, '2024-08-02T09:50:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (16, 2, 2, 16, 'TODO_REMINDER', '주간 회의', '투두 시작 10분 전에 알려드려요.', NULL, 21);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (17, 'TODO_REMINDER', 2, 2, 22, '2024-07-28T09:40:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (17, 2, 2, 17, 'TODO_REMINDER', '코드 리뷰', '투두 시작 20분 전에 알려드려요.', NULL, 22);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (18, 'TODO_REMINDER', 2, 2, 23, '2024-08-10T13:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (18, 2, 2, 18, 'TODO_REMINDER', '기술 문서 작성', '투두 시작 30분 전에 알려드려요.', NULL, 23);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (19, 'TODO_REMINDER', 2, 2, 24, '2024-07-25T09:15:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (19, 2, 2, 19, 'TODO_REMINDER', '팀 피드백', '투두 시작 45분 전에 알려드려요.', NULL, 24);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (20, 'TODO_REMINDER', 2, 2, 25, '2024-08-07T08:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (20, 2, 2, 20, 'TODO_REMINDER', '데일리 스크럼', '투두 시작 1시간 전에 알려드려요.', NULL, 25);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (21, 'TODO_REMINDER', 2, 2, 26, '2024-07-29T08:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (21, 2, 2, 21, 'TODO_REMINDER', '주간 회의', '투두 시작 2시간 전에 알려드려요.', NULL, 26);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (22, 'TODO_REMINDER', 2, 2, 27, '2024-08-05T11:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (22, 2, 2, 22, 'TODO_REMINDER', '코드 리뷰', '투두 시작 3시간 전에 알려드려요.', NULL, 27);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (23, 'TODO_REMINDER', 2, 2, 28, '2024-08-06T07:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (23, 2, 2, 23, 'TODO_REMINDER', '팀 회의 준비', '투두 시작 6시간 전에 알려드려요.', NULL, 28);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (24, 'TODO_REMINDER', 2, 2, 29, '2024-07-17T22:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (24, 2, 2, 24, 'TODO_REMINDER', '기술 블로그 작성', '투두 시작 12시간 전에 알려드려요.', NULL, 29);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (25, 'TODO_REMINDER', 2, 2, 30, '2024-07-14T10:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (25, 2, 2, 25, 'TODO_REMINDER', '프로젝트 미팅', '투두 시작 1일 전에 알려드려요.', NULL, 30);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (26, 'TODO_REMINDER', 2, 2, 31, '2024-08-10T09:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (26, 2, 2, 26, 'TODO_REMINDER', '데일리 체크인', '투두 시작 1일 2시간 전에 알려드려요.', NULL, 31);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (27, 'TODO_REMINDER', 2, 2, 32, '2024-07-29T09:30:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (27, 2, 2, 27, 'TODO_REMINDER', '코드 리뷰', '투두 시작 1일 30분 전에 알려드려요.', NULL, 32);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (28, 'TODO_REMINDER', 2, 2, 33, '2024-08-14T12:30:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (28, 2, 2, 28, 'TODO_REMINDER', '기술 문서 작성', '투두 시작 2시간 30분 전에 알려드려요.', NULL, 33);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (29, 'TODO_REMINDER', 2, 2, 34, '2024-07-22T08:45:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (29, 2, 2, 29, 'TODO_REMINDER', '팀 피드백', '투두 시작 1시간 15분 전에 알려드려요.', NULL, 34);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (30, 'TODO_REMINDER', 2, 2, 35, '2024-08-12T09:25:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (30, 2, 2, 30, 'TODO_REMINDER', '데일리 스크럼', '투두 시작 5분 전에 알려드려요.', NULL, 35);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (31, 'TODO_REMINDER', 2, 2, 36, '2024-07-16T09:50:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (31, 2, 2, 31, 'TODO_REMINDER', '주간 계획 수립', '투두 시작 10분 전에 알려드려요.', NULL, 36);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (32, 'TODO_REMINDER', 2, 2, 37, '2024-08-03T11:10:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (32, 2, 2, 32, 'TODO_REMINDER', '코드 리뷰', '투두 시작 20분 전에 알려드려요.', NULL, 37);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (33, 'TODO_REMINDER', 2, 2, 38, '2024-08-04T13:30:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (33, 2, 2, 33, 'TODO_REMINDER', '팀 회의', '투두 시작 30분 전에 알려드려요.', NULL, 38);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (34, 'TODO_REMINDER', 2, 2, 39, '2024-07-26T09:15:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (34, 2, 2, 34, 'TODO_REMINDER', '기술 블로그 작성', '투두 시작 45분 전에 알려드려요.', NULL, 39);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (35, 'TODO_REMINDER', 2, 2, 40, '2024-08-02T09:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (35, 2, 2, 35, 'TODO_REMINDER', '주간 회의', '투두 시작 1시간 전에 알려드려요.', NULL, 40);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (36, 'TODO_REMINDER', 2, 2, 41, '2024-07-28T08:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (36, 2, 2, 36, 'TODO_REMINDER', '코드 리뷰', '투두 시작 2시간 전에 알려드려요.', NULL, 41);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (37, 'TODO_REMINDER', 2, 2, 42, '2024-08-10T10:30:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (37, 2, 2, 37, 'TODO_REMINDER', '기술 문서 작성', '투두 시작 3시간 전에 알려드려요.', NULL, 42);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (38, 'TODO_REMINDER', 2, 2, 43, '2024-07-25T04:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (38, 2, 2, 38, 'TODO_REMINDER', '팀 피드백', '투두 시작 6시간 전에 알려드려요.', NULL, 43);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (39, 'TODO_REMINDER', 2, 2, 44, '2024-07-31T21:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (39, 2, 2, 39, 'TODO_REMINDER', '데일리 스크럼', '투두 시작 12시간 전에 알려드려요.', NULL, 44);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (40, 'TODO_REMINDER', 2, 2, 45, '2024-07-22T10:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (40, 2, 2, 40, 'TODO_REMINDER', '주간 회의', '투두 시작 1일 전에 알려드려요.', NULL, 45);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (41, 'TODO_REMINDER', 2, 2, 46, '2024-08-04T12:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (41, 2, 2, 41, 'TODO_REMINDER', '코드 리뷰', '투두 시작 1일 2시간 전에 알려드려요.', NULL, 46);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (42, 'TODO_REMINDER', 2, 2, 47, '2024-08-05T12:30:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (42, 2, 2, 42, 'TODO_REMINDER', '팀 회의 준비', '투두 시작 1일 30분 전에 알려드려요.', NULL, 47);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (43, 'TODO_REMINDER', 2, 2, 48, '2024-07-18T07:30:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (43, 2, 2, 43, 'TODO_REMINDER', '기술 블로그 작성', '투두 시작 2시간 30분 전에 알려드려요.', NULL, 48);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (44, 'TODO_REMINDER', 2, 2, 49, '2024-07-15T08:45:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (44, 2, 2, 44, 'TODO_REMINDER', '프로젝트 미팅', '투두 시작 1시간 15분 전에 알려드려요.', NULL, 49);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (45, 'TODO_REMINDER', 2, 2, 50, '2024-08-11T10:55:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (45, 2, 2, 45, 'TODO_REMINDER', '데일리 체크인', '투두 시작 5분 전에 알려드려요.', NULL, 50);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (46, 'TODO_REMINDER', 2, 2, 51, '2024-08-01T06:50:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (46, 2, 2, 46, 'TODO_REMINDER', '헬스장 가기', '투두 시작 10분 전에 알려드려요.', NULL, 51);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (47, 'TODO_REMINDER', 2, 2, 52, '2024-07-23T09:40:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (47, 2, 2, 47, 'TODO_REMINDER', '요가 클래스', '투두 시작 20분 전에 알려드려요.', NULL, 52);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (48, 'TODO_REMINDER', 2, 2, 53, '2024-08-05T06:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (48, 2, 2, 48, 'TODO_REMINDER', '조깅', '투두 시작 30분 전에 알려드려요.', NULL, 53);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (49, 'TODO_REMINDER', 2, 2, 54, '2024-08-02T06:15:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (49, 2, 2, 49, 'TODO_REMINDER', '헬스장 가기', '투두 시작 45분 전에 알려드려요.', NULL, 54);
INSERT INTO notification_events(id, type_code, sender_id, receiver_id, context_id, will_fire_at) VALUES (50, 'TODO_REMINDER', 2, 2, 55, '2024-07-25T09:00:00');
INSERT INTO notification_inboxes(id, user_id, sender_id, event_id, type_code, title, body, read_at, context_id) VALUES (50, 2, 2, 50, 'TODO_REMINDER', '요가 클래스', '투두 시작 1시간 전에 알려드려요.', NULL, 55);

-- ANNOUNCEMENTS
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (1, 2, '공지사항 1', '서비스 점검 안내입니다.', '2024-08-01T09:00:00', '2024-08-01T09:00:00');
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (2, 2, '공지사항 2', '신규 기능이 배포되었습니다.', '2024-08-02T09:00:00', '2024-08-02T09:00:00');
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (3, 2, '공지사항 3', '알림 설정이 개선되었습니다.', '2024-08-03T09:00:00', '2024-08-03T09:00:00');
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (4, 2, '공지사항 4', '앱 안정화 업데이트가 적용되었습니다.', '2024-08-04T09:00:00', '2024-08-04T09:00:00');
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (5, 2, '공지사항 5', '데이터 동기화 성능이 향상되었습니다.', '2024-08-05T09:00:00', '2024-08-05T09:00:00');
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (6, 2, '공지사항 6', '일부 UI 문구가 수정되었습니다.', '2024-08-06T09:00:00', '2024-08-06T09:00:00');
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (7, 2, '공지사항 7', '로그인 세션 관리 정책이 변경되었습니다.', '2024-08-07T09:00:00', '2024-08-07T09:00:00');
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (8, 2, '공지사항 8', '푸시 알림 전송 지연 문제가 해결되었습니다.', '2024-08-08T09:00:00', '2024-08-08T09:00:00');
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (9, 2, '공지사항 9', '통계 화면 로딩 속도가 개선되었습니다.', '2024-08-09T09:00:00', '2024-08-09T09:00:00');
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (10, 2, '공지사항 10', '다음 주 정기 점검 일정이 공지되었습니다.', '2024-08-10T09:00:00', '2024-08-10T09:00:00');
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (11, 2, '공지사항 11', '공지사항 목록 개선 사항이 적용되었습니다.', NOW() - INTERVAL 9 DAY, NOW() - INTERVAL 9 DAY);
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (12, 2, '공지사항 12', '알림함 정렬 로직이 안정화되었습니다.', NOW() - INTERVAL 8 DAY, NOW() - INTERVAL 8 DAY);
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (13, 2, '공지사항 13', '앱 시작 속도 최적화가 반영되었습니다.', NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 7 DAY);
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (14, 2, '공지사항 14', '일부 화면의 접근성 문구를 정비했습니다.', NOW() - INTERVAL 6 DAY, NOW() - INTERVAL 6 DAY);
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (15, 2, '공지사항 15', '일시적인 푸시 중복 발송 문제가 해결되었습니다.', NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY);
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (16, 2, '공지사항 16', '검색 성능 개선을 위한 인덱스가 조정되었습니다.', NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY);
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (17, 2, '공지사항 17', '일부 알림 메시지 표현이 더 명확하게 변경되었습니다.', NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY);
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (18, 2, '공지사항 18', '앱 내부 오류 처리 메시지를 통합했습니다.', NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY);
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (19, 2, '공지사항 19', '운영 안정화를 위한 배포가 완료되었습니다.', NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY);
INSERT INTO announcements(id, user_id, title, contents, created_at, updated_at)
VALUES (20, 2, '공지사항 20', '최신 공지 노출 정책이 적용되었습니다.', NOW(), NOW());

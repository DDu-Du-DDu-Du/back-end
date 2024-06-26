-- CLEAR
SET foreign_key_checks = 0;

TRUNCATE users;
TRUNCATE refresh_tokens;
TRUNCATE auth_providers;
TRUNCATE goals;
TRUNCATE ddudus;

SET foreign_key_checks = 1;

-- USERS
INSERT INTO users(id, nickname, username, introduction, profile_image_url, authority, status,
                  follows_after_approval, template_notification, ddudu_notification, created_at,
                  updated_at)
VALUES (1, '결단력있는 딩고', 'determineddingoda25c93c', null, null, 'NORMAL', 'ACTIVE', 0, 1, 1,
        '2024-05-17T16:13:48', '2024-05-17T16:13:48');

-- REFRESH TOKEN
INSERT INTO refresh_tokens(id, user_id, family, token_value, created_at, updated_at)
VALUES (1, 1, 1,
        'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJtYXJjby1kZHVkdSIsInN1YiI6IjEtMSIsImlhdCI6MTcxNTkzMDAyOH0.ig76OI9bD2Da0-NsIgWoaM3rzewvm_Y0HUbjOjJUOG-gBZHk_k5CCrCSynRSZXwcttqdiwLSKhHYzj5zUUf8ZQ',
        '2024-05-17T16:13:48', '2024-05-17T16:13:48');

-- AUTH PROVIDERS
INSERT INTO auth_providers(id, user_id, provider_type, provider_id, created_at, updated_at)
VALUES (1, 1, 'KAKAO', '3477771378', '2024-05-17T07:13:48', '2024-05-17T07:13:48');

-- GOALS
INSERT INTO goals(id, user_id, name, color, privacy, status, created_at, updated_at)
VALUES (1, 1, '프로젝트', '191919', 'PUBLIC', 'IN_PROGRESS', '2024-05-17T07:13:48',
        '2024-05-17T07:13:48');
INSERT INTO goals(id, user_id, name, color, privacy, status, created_at, updated_at)
VALUES (2, 1, 'Study', '999999', 'PRIVATE', 'IN_PROGRESS', '2024-05-18T07:13:48',
        '2024-05-17T07:13:48');

-- DDUDUS
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (1, 1, 1, '데일리 스크럼', 'COMPLETE', '11:00:00', '11:30:00', 0, '2024-05-17T06:30:48',
        '2024-05-17T06:30:48', '2024-05-17');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (2, 1, 1, '뚜두뚜두 배포하기', 'UNCOMPLETED', null, null, 0, '2024-05-17T07:13:48',
        '2024-05-17T07:13:48', '2024-05-17');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (3, 1, 1, '목표 도메인 리팩토링', 'UNCOMPLETED', null, null, 0, '2024-05-17T07:30:48',
        '2024-05-17T07:30:48', '2024-05-17');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (4, 1, 2, '알고리즘 스터디1', 'UNCOMPLETED', '20:00:00', '21:00:00', 0, '2024-05-17T07:30:48',
        '2024-05-17T07:30:48', '2024-05-17');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (5, 1, 2, '알고리즘 스터디2', 'UNCOMPLETED', '20:00:00', '21:00:00', 0, '2024-05-17T07:30:48',
        '2024-05-17T07:30:48', '2024-05-17');

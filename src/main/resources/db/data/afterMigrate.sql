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
INSERT INTO users(id, nickname, username, introduction, profile_image_url, authority, status,
                  follows_after_approval, template_notification, ddudu_notification, created_at,
                  updated_at)
VALUES (2, '짜증난 강아지', 'irritatedpuppy30b6f3ed', null, null, 'NORMAL', 'ACTIVE', 0, 1, 1,
        '2024-08-18T21:13:02', '2024-08-18T21:13:02');
# 뚜두 구글 계정

-- REFRESH TOKEN
INSERT INTO refresh_tokens(id, user_id, family, token_value, created_at, updated_at)
VALUES (1, 1, 1,
        'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJtYXJjby1kZHVkdSIsInN1YiI6IjEtMSIsImlhdCI6MTcxNTkzMDAyOH0.ig76OI9bD2Da0-NsIgWoaM3rzewvm_Y0HUbjOjJUOG-gBZHk_k5CCrCSynRSZXwcttqdiwLSKhHYzj5zUUf8ZQ',
        '2024-05-17T16:13:48', '2024-05-17T16:13:48');

-- AUTH PROVIDERS
INSERT INTO auth_providers(id, user_id, provider_type, provider_id, created_at, updated_at)
VALUES (1, 1, 'KAKAO', '3477771378', '2024-05-17T07:13:48', '2024-05-17T07:13:48');
INSERT INTO auth_providers(id, user_id, provider_type, provider_id, created_at, updated_at)
VALUES (2, 2, 'KAKAO', '3473310045', '2024-08-18T12:13:02', '2024-08-18T12:13:02');
# 뚜두 구글 계정

-- GOALS
INSERT INTO goals(id, user_id, name, color, privacy, status, created_at, updated_at)
VALUES (1, 1, '프로젝트', '191919', 'PUBLIC', 'IN_PROGRESS', '2024-05-17T07:13:48',
        '2024-05-17T07:13:48');
INSERT INTO goals(id, user_id, name, color, privacy, status, created_at, updated_at)
VALUES (2, 1, 'Study', '999999', 'PRIVATE', 'IN_PROGRESS', '2024-05-18T07:13:48',
        '2024-05-17T07:13:48');
INSERT INTO goals(id, user_id, name, color, privacy, status, created_at, updated_at)
VALUES (3, 2, '프로젝트', '191919', 'PUBLIC', 'IN_PROGRESS', '2024-08-18T21:13:02',
        '2024-08-18T21:13:02');
INSERT INTO goals(id, user_id, name, color, privacy, status, created_at, updated_at)
VALUES (4, 2, '프로젝트2', '191919', 'PUBLIC', 'IN_PROGRESS', '2024-08-18T21:13:02',
        '2024-08-18T21:13:02');

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
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (6, 2, 3, '데일리 스크럼', 'COMPLETE', '09:00:00', '09:30:00', 0, '2024-08-01T08:15:00',
        '2024-08-01T08:15:00', '2024-08-01');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (7, 2, 3, '주간 회의', 'UNCOMPLETED', null, null, 1, '2024-07-23T09:45:00',
        '2024-07-23T09:45:00', '2024-07-23');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (8, 2, 3, '코드 리뷰', 'COMPLETE', '14:00:00', '14:30:00', 0, '2024-08-05T12:30:00',
        '2024-08-05T12:30:00', '2024-08-05');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (9, 2, 3, '팀 회의 준비', 'COMPLETE', '13:00:00', '13:30:00', 0, '2024-08-06T13:00:00',
        '2024-08-06T13:00:00', '2024-08-06');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (10, 2, 3, '기술 블로그 작성', 'UNCOMPLETED', null, null, 1, '2024-07-18T11:00:00',
        '2024-07-18T11:00:00', '2024-07-18');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (11, 2, 3, '프로젝트 미팅', 'COMPLETE', '10:00:00', '10:30:00', 1, '2024-07-15T10:15:00',
        '2024-07-15T10:15:00', '2024-07-15');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (12, 2, 3, '데일리 체크인', 'COMPLETE', '11:00:00', '11:15:00', 0, '2024-08-11T11:00:00',
        '2024-08-11T11:00:00', '2024-08-11');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (13, 2, 3, '코드 리뷰', 'UNCOMPLETED', null, null, 1, '2024-07-30T12:00:00',
        '2024-07-30T12:00:00', '2024-07-30');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (14, 2, 3, '기술 문서 작성', 'COMPLETE', '15:00:00', '15:45:00', 0, '2024-08-14T15:00:00',
        '2024-08-14T15:00:00', '2024-08-14');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (15, 2, 3, '팀 피드백', 'UNCOMPLETED', null, null, 1, '2024-07-22T16:00:00',
        '2024-07-22T16:00:00', '2024-07-22');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (16, 2, 3, '데일리 스크럼', 'COMPLETE', '09:30:00', '10:00:00', 0, '2024-08-12T09:30:00',
        '2024-08-12T09:30:00', '2024-08-12');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (17, 2, 3, '주간 계획 수립', 'UNCOMPLETED', null, null, 1, '2024-07-16T10:45:00',
        '2024-07-16T10:45:00', '2024-07-16');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (18, 2, 3, '코드 리뷰', 'COMPLETE', '11:30:00', '12:00:00', 0, '2024-08-03T11:30:00',
        '2024-08-03T11:30:00', '2024-08-03');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (19, 2, 3, '팀 회의', 'COMPLETE', '14:00:00', '14:30:00', 0, '2024-08-04T14:00:00',
        '2024-08-04T14:00:00', '2024-08-04');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (20, 2, 3, '기술 블로그 작성', 'UNCOMPLETED', null, null, 1, '2024-07-26T15:30:00',
        '2024-07-26T15:30:00', '2024-07-26');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (21, 2, 3, '주간 회의', 'COMPLETE', '10:00:00', '10:30:00', 0, '2024-08-02T10:00:00',
        '2024-08-02T10:00:00', '2024-08-02');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (22, 2, 3, '코드 리뷰', 'UNCOMPLETED', null, null, 1, '2024-07-28T13:00:00',
        '2024-07-28T13:00:00', '2024-07-28');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (23, 2, 3, '기술 문서 작성', 'COMPLETE', '13:30:00', '14:00:00', 0, '2024-08-10T13:30:00',
        '2024-08-10T13:30:00', '2024-08-10');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (24, 2, 3, '팀 피드백', 'UNCOMPLETED', null, null, 1, '2024-07-25T11:00:00',
        '2024-07-25T11:00:00', '2024-07-25');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (25, 2, 3, '데일리 스크럼', 'COMPLETE', '09:00:00', '09:30:00', 0, '2024-08-07T08:15:00',
        '2024-08-07T08:15:00', '2024-08-07');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (26, 2, 3, '주간 회의', 'UNCOMPLETED', null, null, 1, '2024-07-29T09:45:00',
        '2024-07-29T09:45:00', '2024-07-29');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (27, 2, 3, '코드 리뷰', 'COMPLETE', '14:00:00', '14:30:00', 0, '2024-08-05T12:30:00',
        '2024-08-05T12:30:00', '2024-08-05');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (28, 2, 3, '팀 회의 준비', 'COMPLETE', '13:00:00', '13:30:00', 0, '2024-08-06T13:00:00',
        '2024-08-06T13:00:00', '2024-08-06');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (29, 2, 3, '기술 블로그 작성', 'UNCOMPLETED', null, null, 1, '2024-07-18T11:00:00',
        '2024-07-18T11:00:00', '2024-07-18');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (30, 2, 3, '프로젝트 미팅', 'COMPLETE', '10:00:00', '10:30:00', 1, '2024-07-15T10:15:00',
        '2024-07-15T10:15:00', '2024-07-15');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (31, 2, 3, '데일리 체크인', 'COMPLETE', '11:00:00', '11:15:00', 0, '2024-08-11T11:00:00',
        '2024-08-11T11:00:00', '2024-08-11');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (32, 2, 3, '코드 리뷰', 'UNCOMPLETED', null, null, 1, '2024-07-30T12:00:00',
        '2024-07-30T12:00:00', '2024-07-30');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (33, 2, 3, '기술 문서 작성', 'COMPLETE', '15:00:00', '15:45:00', 0, '2024-08-14T15:00:00',
        '2024-08-14T15:00:00', '2024-08-14');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (34, 2, 3, '팀 피드백', 'UNCOMPLETED', null, null, 1, '2024-07-22T16:00:00',
        '2024-07-22T16:00:00', '2024-07-22');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (35, 2, 3, '데일리 스크럼', 'COMPLETE', '09:30:00', '10:00:00', 0, '2024-08-12T09:30:00',
        '2024-08-12T09:30:00', '2024-08-12');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (36, 2, 3, '주간 계획 수립', 'UNCOMPLETED', null, null, 1, '2024-07-16T10:45:00',
        '2024-07-16T10:45:00', '2024-07-16');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (37, 2, 3, '코드 리뷰', 'COMPLETE', '11:30:00', '12:00:00', 0, '2024-08-03T11:30:00',
        '2024-08-03T11:30:00', '2024-08-03');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (38, 2, 3, '팀 회의', 'COMPLETE', '14:00:00', '14:30:00', 0, '2024-08-04T14:00:00',
        '2024-08-04T14:00:00', '2024-08-04');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (39, 2, 3, '기술 블로그 작성', 'UNCOMPLETED', null, null, 1, '2024-07-26T15:30:00',
        '2024-07-26T15:30:00', '2024-07-26');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (40, 2, 3, '주간 회의', 'COMPLETE', '10:00:00', '10:30:00', 0, '2024-08-02T10:00:00',
        '2024-08-02T10:00:00', '2024-08-02');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (41, 2, 3, '코드 리뷰', 'UNCOMPLETED', null, null, 1, '2024-07-28T13:00:00',
        '2024-07-28T13:00:00', '2024-07-28');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (42, 2, 3, '기술 문서 작성', 'COMPLETE', '13:30:00', '14:00:00', 0, '2024-08-10T13:30:00',
        '2024-08-10T13:30:00', '2024-08-10');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (43, 2, 3, '팀 피드백', 'UNCOMPLETED', null, null, 1, '2024-07-25T11:00:00',
        '2024-07-25T11:00:00', '2024-07-25');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (44, 2, 3, '데일리 스크럼', 'COMPLETE', '09:00:00', '09:30:00', 0, '2024-08-01T08:15:00',
        '2024-08-01T08:15:00', '2024-08-01');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (45, 2, 3, '주간 회의', 'UNCOMPLETED', null, null, 1, '2024-07-23T09:45:00',
        '2024-07-23T09:45:00', '2024-07-23');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (46, 2, 3, '코드 리뷰', 'COMPLETE', '14:00:00', '14:30:00', 0, '2024-08-05T12:30:00',
        '2024-08-05T12:30:00', '2024-08-05');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (47, 2, 3, '팀 회의 준비', 'COMPLETE', '13:00:00', '13:30:00', 0, '2024-08-06T13:00:00',
        '2024-08-06T13:00:00', '2024-08-06');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (48, 2, 3, '기술 블로그 작성', 'UNCOMPLETED', null, null, 1, '2024-07-18T11:00:00',
        '2024-07-18T11:00:00', '2024-07-18');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (49, 2, 3, '프로젝트 미팅', 'COMPLETE', '10:00:00', '10:30:00', 1, '2024-07-15T10:15:00',
        '2024-07-15T10:15:00', '2024-07-15');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (50, 2, 3, '데일리 체크인', 'COMPLETE', '11:00:00', '11:15:00', 0, '2024-08-11T11:00:00',
        '2024-08-11T11:00:00', '2024-08-11');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (51, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', 0, '2024-08-01T07:00:00',
        '2024-08-01T08:00:00', '2024-08-01');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (52, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, 1, '2024-07-23T08:15:00',
        '2024-07-23T08:15:00', '2024-07-23');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (53, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', 0, '2024-08-05T06:30:00',
        '2024-08-05T07:00:00', '2024-08-05');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (54, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', 0, '2024-08-02T07:00:00',
        '2024-08-02T08:00:00', '2024-08-02');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (55, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, 1, '2024-07-25T08:15:00',
        '2024-07-25T08:15:00', '2024-07-25');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (56, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', 0, '2024-08-03T06:30:00',
        '2024-08-03T07:00:00', '2024-08-03');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (57, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', 0, '2024-08-06T07:00:00',
        '2024-08-06T08:00:00', '2024-08-06');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (58, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, 1, '2024-07-27T08:15:00',
        '2024-07-27T08:15:00', '2024-07-27');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (59, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', 0, '2024-08-07T06:30:00',
        '2024-08-07T07:00:00', '2024-08-07');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (60, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', 0, '2024-08-08T07:00:00',
        '2024-08-08T08:00:00', '2024-08-08');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (61, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, 1, '2024-07-29T08:15:00',
        '2024-07-29T08:15:00', '2024-07-29');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (62, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', 0, '2024-08-09T06:30:00',
        '2024-08-09T07:00:00', '2024-08-09');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (63, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', 0, '2024-08-10T07:00:00',
        '2024-08-10T08:00:00', '2024-08-10');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (64, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, 1, '2024-07-31T08:15:00',
        '2024-07-31T08:15:00', '2024-07-31');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (65, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', 0, '2024-08-11T06:30:00',
        '2024-08-11T07:00:00', '2024-08-11');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (66, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', 0, '2024-08-12T07:00:00',
        '2024-08-12T08:00:00', '2024-08-12');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (67, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, 1, '2024-07-19T08:15:00',
        '2024-07-19T08:15:00', '2024-07-19');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (68, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', 0, '2024-08-13T06:30:00',
        '2024-08-13T07:00:00', '2024-08-13');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (69, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', 0, '2024-08-14T07:00:00',
        '2024-08-14T08:00:00', '2024-08-14');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (70, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, 1, '2024-07-21T08:15:00',
        '2024-07-21T08:15:00', '2024-07-21');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (71, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', 0, '2024-08-15T06:30:00',
        '2024-08-15T07:00:00', '2024-08-15');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (72, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', 0, '2024-08-16T07:00:00',
        '2024-08-16T08:00:00', '2024-08-16');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (73, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, 1, '2024-07-30T08:15:00',
        '2024-07-30T08:15:00', '2024-07-30');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (74, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', 0, '2024-08-17T06:30:00',
        '2024-08-17T07:00:00', '2024-08-17');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (75, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', 0, '2024-08-18T07:00:00',
        '2024-08-18T08:00:00', '2024-08-18');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (76, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, 1, '2024-07-28T08:15:00',
        '2024-07-28T08:15:00', '2024-07-28');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (77, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', 0, '2024-08-19T06:30:00',
        '2024-08-19T07:00:00', '2024-08-19');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (78, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', 0, '2024-08-20T07:00:00',
        '2024-08-20T08:00:00', '2024-08-20');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (79, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, 1, '2024-07-22T08:15:00',
        '2024-07-22T08:15:00', '2024-07-22');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (80, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', 0, '2024-08-21T06:30:00',
        '2024-08-21T07:00:00', '2024-08-21');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (81, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', 0, '2024-08-22T07:00:00',
        '2024-08-22T08:00:00', '2024-08-22');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (82, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, 1, '2024-07-17T08:15:00',
        '2024-07-17T08:15:00', '2024-07-17');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (83, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', 0, '2024-08-23T06:30:00',
        '2024-08-23T07:00:00', '2024-08-23');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (84, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', 0, '2024-08-24T07:00:00',
        '2024-08-24T08:00:00', '2024-08-24');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (85, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, 1, '2024-07-16T08:15:00',
        '2024-07-16T08:15:00', '2024-07-16');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (86, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', 0, '2024-08-25T06:30:00',
        '2024-08-25T07:00:00', '2024-08-25');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (87, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', 0, '2024-08-26T07:00:00',
        '2024-08-26T08:00:00', '2024-08-26');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (88, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, 1, '2024-07-18T08:15:00',
        '2024-07-18T08:15:00', '2024-07-18');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (89, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', 0, '2024-08-27T06:30:00',
        '2024-08-27T07:00:00', '2024-08-27');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (90, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', 0, '2024-08-28T07:00:00',
        '2024-08-28T08:00:00', '2024-08-28');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (91, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, 1, '2024-07-24T08:15:00',
        '2024-07-24T08:15:00', '2024-07-24');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (92, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', 0, '2024-08-29T06:30:00',
        '2024-08-29T07:00:00', '2024-08-29');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (93, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', 0, '2024-08-30T07:00:00',
        '2024-08-30T08:00:00', '2024-08-30');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (94, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, 1, '2024-07-26T08:15:00',
        '2024-07-26T08:15:00', '2024-07-26');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (95, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', 0, '2024-08-31T06:30:00',
        '2024-08-31T07:00:00', '2024-08-31');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (96, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', 0, '2024-09-01T07:00:00',
        '2024-09-01T08:00:00', '2024-09-01');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (97, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, 1, '2024-07-20T08:15:00',
        '2024-07-20T08:15:00', '2024-07-20');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (98, 2, 4, '조깅', 'COMPLETE', '06:30:00', '07:00:00', 0, '2024-09-02T06:30:00',
        '2024-09-02T07:00:00', '2024-09-02');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (99, 2, 4, '헬스장 가기', 'COMPLETE', '07:00:00', '08:00:00', 0, '2024-09-03T07:00:00',
        '2024-09-03T08:00:00', '2024-09-03');
INSERT INTO ddudus(id, user_id, goal_id, name, status, begin_at, end_at, is_postponed,
                   created_at, updated_at, scheduled_on)
VALUES (100, 2, 4, '요가 클래스', 'UNCOMPLETED', null, null, 1, '2024-07-23T08:15:00',
        '2024-07-23T08:15:00', '2024-07-23');

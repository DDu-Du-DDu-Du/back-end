-- CLEAR
SET foreign_key_checks = 0;
TRUNCATE TABLE users;
TRUNCATE TABLE goal;
TRUNCATE TABLE todo;
TRUNCATE TABLE followings;
SET foreign_key_checks = 1;

-- USER
INSERT INTO users(id, email, password, nickname) VALUES (1, 'nicolette.mills@hotmail.com', '$2a$10$Q6KjyLPE6QrpqkGkFb0ezOqe0EaZZpaht11UgefOqNyUB0N0XjnQO', 'Kenya Dewit');
INSERT INTO users(id, email, password, nickname) VALUES (2, 'brian.mayer@hotmail.com', '$2a$10$Q6KjyLPE6QrpqkGkFb0ezOqe0EaZZpaht11UgefOqNyUB0N0XjnQO', 'Jack Pott');
INSERT INTO users(id, email, password, nickname) VALUES (3, 'shayne.bogisich@gmail.com', '$2a$10$Q6KjyLPE6QrpqkGkFb0ezOqe0EaZZpaht11UgefOqNyUB0N0XjnQO', 'Lou Pole');
INSERT INTO users(id, email, password, nickname) VALUES (4, 'josh.thiel@yahoo.com', '$2a$10$Q6KjyLPE6QrpqkGkFb0ezOqe0EaZZpaht11UgefOqNyUB0N0XjnQO', 'Candice B. Fureal');
INSERT INTO users(id, email, password, nickname) VALUES (5, 'stacia.beahan@gmail.com', '$2a$10$Q6KjyLPE6QrpqkGkFb0ezOqe0EaZZpaht11UgefOqNyUB0N0XjnQO', 'Marshall Law');
INSERT INTO users(id, optional_username, email, password, nickname) VALUES (6,'pinkblack', 'ddudu@ddudu.com', '$2a$10$oG6/71OlxogWxhxeLYckn.MbWYsXvGgMzP1zGq9sxcxScn3SekKyW', '뚜두뚜두');
INSERT INTO users(id, optional_username, email, password, nickname) VALUES (7,'devcourselove', 'love@devcourse.com', '$2a$10$eFFAmSR2O/W9lUCmKL5fe.OmH3j/T5/23iUvpRWHm7dBmupdOatOO', '데브코스 사랑함');
INSERT INTO users(id, optional_username, email, password, nickname) VALUES (8,'injaesong', 'injaesong0@gmail.com', '$2a$10$39InvdkmJm3.4xIy4kGCyuoohtLK5rGF09HEKpBFu2fgqfCzYLPiG', '송인재');
INSERT INTO users(id, optional_username, email, password, nickname) VALUES (9,'devcrazy', 'crazy@example.com', '$2a$10$5ZXbGs1etnGkrath/enkB.TdMj8U//jdmH3GXSRj3F3ACHi5LABHC', '개발 미친 사람');
INSERT INTO users(id, optional_username, email, password, nickname) VALUES (10,'dingmon', 'coding@mon.co.kr', '$2a$10$k3/gS0YD9tAr69nRZRBjQOmf.MiqXa29hdCSa.Kg0i5U4/gISOsDG', '코딩몬');

-- GOAL
INSERT INTO goal(id, user_id, name, color, privacy) VALUES (1, 1, 'dev course', '75D7E4', 'FOLLOWER');
INSERT INTO goal(id, user_id, name, color, privacy) VALUES (2, 1, 'study', 'F3D056', 'PUBLIC');
INSERT INTO goal(id, user_id, name, color, privacy) VALUES (3, 1, 'event', 'F1B5CA', 'PRIVATE');
INSERT INTO goal(id, user_id, name, color, privacy) VALUES (4, 1, 'etc', 'C7D567', 'PUBLIC');
INSERT INTO goal(id, user_id, name, privacy, status) VALUES (5, 1, 'college', 'PUBLIC', 'DONE');

-- TO DO
insert into todo(id, name, user_id, goal_id, status) values (1, '10시 30분 마르코 팀미팅', 1, 1, 'UNCOMPLETED');
insert into todo(id, name, user_id, goal_id, status, end_at) values (2, '9시 QR 출셕', 1, 1, 'COMPLETE', NOW());
insert into todo(id, name, user_id, goal_id, status) values (3, '1시 RBF', 1, 1, 'UNCOMPLETED');
insert into todo(id, name, user_id, goal_id, status) values (4, '2시 말코리즘 간단 리뷰', 1, 1, 'UNCOMPLETED');
insert into todo(id, name, user_id, goal_id, status, end_at, is_deleted) values (5, '9시 뚜두뚜두 스크럼', 1, 1, 'COMPLETE', NOW(), 1);

-- FOLLOWING
INSERT INTO followings(id, follower_id, followee_id, status) VALUES (1, 1, 2, 'FOLLOWING');
INSERT INTO followings(id, follower_id, followee_id, status) VALUES (2, 2, 1, 'FOLLOWING');
INSERT INTO followings(id, follower_id, followee_id, status) VALUES (3, 1, 10, 'FOLLOWING');
INSERT INTO followings(id, follower_id, followee_id, status) VALUES (4, 2, 10, 'FOLLOWING');
INSERT INTO followings(id, follower_id, followee_id, status) VALUES (5, 3, 10, 'FOLLOWING');
INSERT INTO followings(id, follower_id, followee_id, status) VALUES (6, 4, 10, 'FOLLOWING');
INSERT INTO followings(id, follower_id, followee_id, status) VALUES (7, 5, 10, 'FOLLOWING');
INSERT INTO followings(id, follower_id, followee_id, status) VALUES (8, 6, 10, 'FOLLOWING');
INSERT INTO followings(id, follower_id, followee_id, status) VALUES (9, 10, 6, 'FOLLOWING');
INSERT INTO followings(id, follower_id, followee_id, status) VALUES (10, 10, 7, 'FOLLOWING');

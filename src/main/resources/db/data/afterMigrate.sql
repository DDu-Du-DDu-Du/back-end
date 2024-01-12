-- CLEAR
SET foreign_key_checks = 0;
truncate table users;
truncate table goal;
truncate table todo;
truncate table followings;
truncate table likes;
SET foreign_key_checks = 1;

-- USER
insert into users(id, email, password, nickname) values (1, 'nicolette.mills@hotmail.com', '$2a$10$Q6KjyLPE6QrpqkGkFb0ezOqe0EaZZpaht11UgefOqNyUB0N0XjnQO', 'Kenya Dewit');
insert into users(id, email, password, nickname) values (2, 'brian.mayer@hotmail.com', '$2a$10$Q6KjyLPE6QrpqkGkFb0ezOqe0EaZZpaht11UgefOqNyUB0N0XjnQO', 'Jack Pott');
insert into users(id, email, password, nickname) values (3, 'shayne.bogisich@gmail.com', '$2a$10$Q6KjyLPE6QrpqkGkFb0ezOqe0EaZZpaht11UgefOqNyUB0N0XjnQO', 'Lou Pole');
insert into users(id, email, password, nickname) values (4, 'josh.thiel@yahoo.com', '$2a$10$Q6KjyLPE6QrpqkGkFb0ezOqe0EaZZpaht11UgefOqNyUB0N0XjnQO', 'Candice B. Fureal');
insert into users(id, email, password, nickname) values (5, 'stacia.beahan@gmail.com', '$2a$10$Q6KjyLPE6QrpqkGkFb0ezOqe0EaZZpaht11UgefOqNyUB0N0XjnQO', 'Marshall Law');
insert into users(id, optional_username, email, password, nickname) values (6,'pinkblack', 'ddudu@ddudu.com', '$2a$10$oG6/71OlxogWxhxeLYckn.MbWYsXvGgMzP1zGq9sxcxScn3SekKyW', '뚜두뚜두');
insert into users(id, optional_username, email, password, nickname) values (7,'devcourselove', 'love@devcourse.com', '$2a$10$eFFAmSR2O/W9lUCmKL5fe.OmH3j/T5/23iUvpRWHm7dBmupdOatOO', '데브코스 사랑함');
insert into users(id, optional_username, email, password, nickname) values (8,'injaesong', 'injaesong0@gmail.com', '$2a$10$39InvdkmJm3.4xIy4kGCyuoohtLK5rGF09HEKpBFu2fgqfCzYLPiG', '송인재');
insert into users(id, optional_username, email, password, nickname) values (9,'devcrazy', 'crazy@example.com', '$2a$10$5ZXbGs1etnGkrath/enkB.TdMj8U//jdmH3GXSRj3F3ACHi5LABHC', '개발 미친 사람');
insert into users(id, optional_username, email, password, nickname, follows_after_approval) values (10,'dingmon', 'coding@mon.co.kr', '$2a$10$k3/gS0YD9tAr69nRZRBjQOmf.MiqXa29hdCSa.Kg0i5U4/gISOsDG', '코딩몬', true);

-- GOAL
INSERT INTO goal(id, user_id, name, color, privacy) VALUES (1, 1, 'dev course', '75D7E4', 'PUBLIC');
insert into goal(id, user_id, name, color, privacy) values (2, 1, 'study', 'F3D056', 'PUBLIC');
insert into goal(id, user_id, name, color, privacy) values (3, 1, 'event', 'F1B5CA', 'PRIVATE');
insert into goal(id, user_id, name, color, privacy) values (4, 1, 'etc', 'C7D567', 'PUBLIC');
insert into goal(id, user_id, name, privacy, status) values (5, 1, 'college', 'PUBLIC', 'DONE');

-- TO DO
insert into todo(id, name, user_id, goal_id, status) values (1, '10시 30분 마르코 팀미팅', 1, 1, 'UNCOMPLETED');
insert into todo(id, name, user_id, goal_id, status, end_at) values (2, '9시 QR 출셕', 1, 1, 'COMPLETE', NOW());
insert into todo(id, name, user_id, goal_id, status) values (3, '1시 RBF', 1, 1, 'UNCOMPLETED');
insert into todo(id, name, user_id, goal_id, status) values (4, '2시 말코리즘 간단 리뷰', 1, 1, 'COMPLETE');
insert into todo(id, name, user_id, goal_id, status, end_at, is_deleted) values (5, '9시 뚜두뚜두 스크럼', 1, 1, 'COMPLETE', NOW(), 1);

-- FOLLOWING
insert into followings(id, follower_id, followee_id, status) values (1, 1, 2, 'FOLLOWING');
insert into followings(id, follower_id, followee_id, status) values (2, 2, 1, 'FOLLOWING');
insert into followings(id, follower_id, followee_id, status) values (3, 1, 10, 'FOLLOWING');
insert into followings(id, follower_id, followee_id, status) values (4, 2, 10, 'FOLLOWING');
insert into followings(id, follower_id, followee_id, status) values (5, 3, 10, 'FOLLOWING');
insert into followings(id, follower_id, followee_id, status) values (6, 4, 10, 'FOLLOWING');
insert into followings(id, follower_id, followee_id, status) values (7, 5, 10, 'FOLLOWING');
insert into followings(id, follower_id, followee_id, status) values (8, 6, 10, 'FOLLOWING');
insert into followings(id, follower_id, followee_id, status) values (9, 10, 6, 'FOLLOWING');
insert into followings(id, follower_id, followee_id, status) values (10, 10, 7, 'FOLLOWING');


-- LIKE
insert into likes(id, user_id, todo_id) values (1, 2, 2);
insert into likes(id, user_id, todo_id) values (2, 2, 4);
insert into likes(id, user_id, todo_id) values (3, 3, 2);
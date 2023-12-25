-- USER
truncate table users;
insert into users(id, email, password, nickname) values (1, 'nicolette.mills@hotmail.com', '$2a$10$Q6KjyLPE6QrpqkGkFb0ezOqe0EaZZpaht11UgefOqNyUB0N0XjnQO', 'Kenya Dewit');
insert into users(id, email, password, nickname) values (2, 'brian.mayer@hotmail.com', '$2a$10$Q6KjyLPE6QrpqkGkFb0ezOqe0EaZZpaht11UgefOqNyUB0N0XjnQO', 'Jack Pott');
insert into users(id, email, password, nickname) values (3, 'shayne.bogisich@gmail.com', '$2a$10$Q6KjyLPE6QrpqkGkFb0ezOqe0EaZZpaht11UgefOqNyUB0N0XjnQO', 'Lou Pole');
insert into users(id, email, password, nickname) values (4, 'josh.thiel@yahoo.com', '$2a$10$Q6KjyLPE6QrpqkGkFb0ezOqe0EaZZpaht11UgefOqNyUB0N0XjnQO', 'Candice B. Fureal');
insert into users(id, email, password, nickname) values (5, 'stacia.beahan@gmail.com', '$2a$10$Q6KjyLPE6QrpqkGkFb0ezOqe0EaZZpaht11UgefOqNyUB0N0XjnQO', 'Marshall Law');
insert into users(id, optional_username, email, password, nickname) values (6,'pinkblack', 'ddudu@ddudu.com', '$2a$10$oG6/71OlxogWxhxeLYckn.MbWYsXvGgMzP1zGq9sxcxScn3SekKyW', '뚜두뚜두');
insert into users(id, optional_username, email, password, nickname) values (7,'devcourselove', 'love@devcourse.com', '$2a$10$eFFAmSR2O/W9lUCmKL5fe.OmH3j/T5/23iUvpRWHm7dBmupdOatOO', '데브코스 사랑함');
insert into users(id, optional_username, email, password, nickname) values (8,'injaesong', 'injaesong0@gmail.com', '$2a$10$39InvdkmJm3.4xIy4kGCyuoohtLK5rGF09HEKpBFu2fgqfCzYLPiG', '송인재');
insert into users(id, optional_username, email, password, nickname) values (9,'devcrazy', 'crazy@example.com', '$2a$10$5ZXbGs1etnGkrath/enkB.TdMj8U//jdmH3GXSRj3F3ACHi5LABHC', '개발 미친 사람');
insert into users(id, optional_username, email, password, nickname) values (10,'dingmon', 'coding@mon.co.kr', '$2a$10$k3/gS0YD9tAr69nRZRBjQOmf.MiqXa29hdCSa.Kg0i5U4/gISOsDG', '코딩몬');

-- GOAL
insert into goal(id, name, privacy) values (1, 'dev course', 'PUBLIC');
insert into goal(id, name, privacy) values (2, 'book', 'PUBLIC');

-- TO DO
insert into todo(id, name, goal_id, status) values (1, '10시 30분 마르코 팀미팅', 1, 'UNCOMPLETED');
insert into todo(id, name, goal_id, status, end_at) values (2, '9시 QR 출셕', 1, 'COMPLETE', NOW());
insert into todo(id, name, goal_id, status) values (3, '1시 RBF', 1, 'UNCOMPLETED');
insert into todo(id, name, goal_id, status) values (4, '2시 말코리즘 간단 리뷰', 1, 'UNCOMPLETED');
insert into todo(id, name, goal_id, status, end_at) values (5, '9시 뚜두뚜두 스크럼', 1, 'COMPLETE', NOW());

-- AUTHORITIES
INSERT INTO authority (name) VALUES ('ROLE_ADMIN');
INSERT INTO authority (name) VALUES ('ROLE_NORMAL_USER');

-- NORMAL USERS
INSERT INTO normal_user (username, password, first_name, last_name)
VALUES ('user1', '$2a$10$l8J.2UoFqfOwj9t7GRAtAen1/t8Sz2HfAxYT9LehVxq58wa9LihEi', 'User1', 'Useric1');
INSERT INTO normal_user_authority (normal_user_id, authority_id) VALUES (1, 2);

-- SUBJECTS
INSERT INTO subject (commonname, surname, givenname, organization, organizationunit, country, email, isca, hascertificate)
VALUES ('a', 'a', 'a', 'a', 'a', 'aa', 'a@a.com', false, false);
INSERT INTO subject (commonname, surname, givenname, organization, organizationunit, country, email, isca, hascertificate)
VALUES ('b', 'b', 'b', 'b', 'b', 'bb', 'b@b.com', false, false);
INSERT INTO subject (commonname, surname, givenname, organization, organizationunit, country, email, isca, hascertificate)
VALUES ('c', 'c', 'c', 'c', 'c', 'cc', 'c@c.com', false, false);

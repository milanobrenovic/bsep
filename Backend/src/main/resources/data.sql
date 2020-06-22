INSERT INTO authority (name)
SELECT 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT name FROM authority WHERE name = 'ROLE_ADMIN');

INSERT INTO subject (commonname, surname, givenname, organization, organizationunit, country, email, isca, hascertificate)
VALUES ('a', 'a', 'a', 'a', 'a', 'aa', 'a@a.com', false, false);
INSERT INTO subject (commonname, surname, givenname, organization, organizationunit, country, email, isca, hascertificate)
VALUES ('b', 'b', 'b', 'b', 'b', 'bb', 'b@b.com', false, false);
INSERT INTO subject (commonname, surname, givenname, organization, organizationunit, country, email, isca, hascertificate)
VALUES ('c', 'c', 'c', 'c', 'c', 'cc', 'c@c.com', false, false);

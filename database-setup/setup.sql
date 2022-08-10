/* Setting up Database */
DROP DATABASE IF EXISTS prod;
CREATE DATABASE prod;
USE prod;

CREATE TABLE user (
                      id BIGINT AUTO_INCREMENT NOT NULL,
                      email VARCHAR(70) NOT NULL,
                      password VARCHAR(100) NOT NULL,
                      PRIMARY KEY(id)
);

/* Adding some initial data */
insert into user (email, password)
values ('1@mail.com', '$2a$12$W7BAG6324Ft0lR4bRZpPge/OsJUDX9NFyZ/3FVb1UVa4Fn12KU7KG');
/* password is encoded with BCrypt. Original was "123" */

/* Create TEST DB */
DROP DATABASE IF EXISTS test;
CREATE DATABASE test;
USE test;

/* Mirror the structure in a database for tests */
CREATE TABLE user LIKE prod.user;

/* Users to access the databases with limited permissions */
DROP USER IF EXISTS 'dbuser';
DROP USER IF EXISTS 'testuser';
CREATE USER 'dbuser' IDENTIFIED BY 'dbpassword';
CREATE USER 'testuser' IDENTIFIED BY 'testpassword';
GRANT SELECT,INSERT,UPDATE ON prod.* TO 'dbuser';
GRANT SELECT,INSERT,UPDATE,DELETE ON test.* TO 'testuser';


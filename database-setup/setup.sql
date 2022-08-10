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

CREATE TABLE wallet (
                        id BIGINT AUTO_INCREMENT NOT NULL,
                        user_id BIGINT,
                        profile_name VARCHAR(70) NOT NULL,
                        balance INT DEFAULT 0,
                        PRIMARY KEY(id)
);

/* Adding some initial data */
insert into user (id, email, password) /* password is "123" */
values (1, '1@mail.com', '$2a$12$W7BAG6324Ft0lR4bRZpPge/OsJUDX9NFyZ/3FVb1UVa4Fn12KU7KG');
insert into wallet (user_id, profile_name)
values (1, 'Profile One');


/* Create TEST DB */
DROP DATABASE IF EXISTS test;
CREATE DATABASE test;
USE test;

/* Mirror the structure in a database for tests */
CREATE TABLE user LIKE prod.user;
CREATE TABLE wallet LIKE prod.wallet;

/* Users to access the databases with limited permissions */
DROP USER IF EXISTS 'dbuser';
DROP USER IF EXISTS 'testuser';
CREATE USER 'dbuser' IDENTIFIED BY 'dbpassword';
CREATE USER 'testuser' IDENTIFIED BY 'testpassword';
GRANT SELECT,INSERT,UPDATE ON prod.* TO 'dbuser';
GRANT SELECT,INSERT,UPDATE,DELETE ON test.* TO 'testuser';


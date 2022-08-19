/* Setting up database */
DROP DATABASE IF EXISTS prod;
CREATE DATABASE prod;
USE prod;
CREATE TABLE user
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    email    VARCHAR(70) UNIQUE    NOT NULL,
    password VARCHAR(100)          NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE billing_details
(
    id BIGINT AUTO_INCREMENT NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE wallet
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    user_id            BIGINT,
    billing_details_id BIGINT,
    balance            INT DEFAULT 0,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (billing_details_id) REFERENCES billing_details (id)
);
CREATE TABLE connection
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    creator_wallet_id BIGINT,
    target_wallet_id  BIGINT,
    name_given        VARCHAR(70),
    PRIMARY KEY (id),
    FOREIGN KEY (creator_wallet_id) REFERENCES wallet (id),
    FOREIGN KEY (target_wallet_id) REFERENCES wallet (id)
);
CREATE TABLE transfer
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    connection_id  BIGINT,
    description    VARCHAR(70)           NOT NULL,
    amount         INT,
    time_completed TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (connection_id) REFERENCES connection (id)
);

/* Create TEST DB */
DROP DATABASE IF EXISTS test;
CREATE DATABASE test;

/* Mirror the structure in a database for tests */
USE test;
CREATE TABLE user LIKE prod.user;
CREATE TABLE wallet LIKE prod.wallet;
CREATE TABLE connection LIKE prod.connection;
CREATE TABLE transfer LIKE prod.transfer;
CREATE TABLE billing_details LIKE prod.billing_details;

/* Users to access the databases with limited permissions */
DROP USER IF EXISTS 'dbuser';
DROP USER IF EXISTS 'testuser';
CREATE USER 'dbuser' IDENTIFIED BY 'dbpassword';
CREATE USER 'testuser' IDENTIFIED BY 'testpassword';
GRANT SELECT, INSERT, UPDATE ON prod.* TO 'dbuser';
GRANT DELETE ON prod.connection TO 'dbuser';
GRANT SELECT, INSERT, UPDATE, DELETE ON test.* TO 'testuser';

/* Adding some example data to PROD DB. Passwords are "123" in all cases */
use prod;
SET @password_123 = '$2a$12$W7BAG6324Ft0lR4bRZpPge/OsJUDX9NFyZ/3FVb1UVa4Fn12KU7KG';

insert into user (id, email, password)
values (1, '1@mail.com', @password_123),
       (2, '2@mail.com', @password_123),
       (3, '3@mail.com', @password_123),
       (4, '4@mail.com', @password_123),
       (5, '5@mail.com', @password_123);

insert into wallet (user_id, balance)
values (1, 1000),
       (2, 1000),
       (3, 2500),
       (4, 800),
       (5, 0);

insert into connection (id, creator_wallet_id, target_wallet_id, name_given)
VALUES (1, 1, 2, 'Hayley'),
       (2, 1, 3, 'Clara'),
       (3, 1, 4, 'Smith');

insert into transfer (connection_id, time_completed, description, amount)
VALUES (3, '2022-08-01 11:11', 'Test with 20 cents', 20),
       (3, '2022-08-02 12:22', 'Movie tickets', 800),
       (2, '2022-08-03 13:33', 'Trip money', 2500),
       (1, '2022-08-04 14:44', 'Restaurant bill share', 1000);
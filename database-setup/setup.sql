/* Setting up database */
DROP DATABASE IF EXISTS prod;
CREATE DATABASE prod;
USE prod;
CREATE TABLE user
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    email    VARCHAR(70) UNIQUE    NOT NULL,
    password VARCHAR(100)          NOT NULL,
    PRIMARY KEY (id),
    INDEX (email)
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

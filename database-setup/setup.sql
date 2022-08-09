/* Setting up PROD DB */
create database prod;
/* Not yet implemented */

/* Setting up TEST DB */
create database test;
use test;

create table user(
                     id BIGINT AUTO_INCREMENT NOT NULL,
                     email VARCHAR(70) NOT NULL,
                     password VARCHAR(100) NOT NULL,
                     PRIMARY KEY(id)
);

insert into user (email, password)
values ('test@mail.com', '$2a$12$W7BAG6324Ft0lR4bRZpPge/OsJUDX9NFyZ/3FVb1UVa4Fn12KU7KG');
/* password is encoded with BCrypt. Original was "123" */
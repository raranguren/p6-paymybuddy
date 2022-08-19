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
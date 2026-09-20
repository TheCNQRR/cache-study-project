CREATE TABLE users
(
    id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username     TEXT UNIQUE NOT NULL,
    display_name TEXT,
    created_at   TIMESTAMP   NOT NULL DEFAULT now()
);

CREATE TABLE chats
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title      TEXT      NOT NULL,
    created_by BIGINT REFERENCES users (id),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE chat_members
(
    chat_id BIGINT REFERENCES chats (id),
    user_id BIGINT REFERENCES users (id),
    PRIMARY KEY (chat_id, user_id)
);

CREATE TABLE messages
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    chat_id    BIGINT REFERENCES chats (id),
    user_id    BIGINT REFERENCES users (id),
    text       TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);


INSERT INTO users(username, display_name)
VALUES ('user1', 'Pavel'),
       ('user2', 'Sergey'),
       ('user3', 'Ivan'),
       ('user4', 'Nikolay'),
       ('user5', 'Alexander'),
       ('user6', 'Boris'),
       ('user7', 'Petr');


INSERT INTO chats(title, created_by)
VALUES ('chat1', 1),
       ('chat2', 3),
       ('chat3', 1),
       ('chat4', 7),
       ('chat5', 4),
       ('chat6', 1),
       ('chat7', 7);


INSERT INTO chat_members(chat_id, user_id)
VALUES (1, 1),
       (1, 2),
       (1, 5),
       (1, 7),
       (3, 6),
       (3, 7),
       (7, 1),
       (7, 4);

INSERT INTO messages(chat_id, user_id, text)
VALUES (1, 1, 'Message1'),
       (1, 2, 'Message2'),
       (1, 1, 'Message3'),
       (1, 1, 'Message4'),
       (3, 6, 'Message1'),
       (3, 7, 'Message2'),
       (3, 6, 'Message3'),
       (3, 7, 'Message4'),
       (3, 6, 'Message5'),
       (3, 7, 'Message6'),
       (7, 1, 'Message1'),
       (7, 1, 'Message2'),
       (7, 4, 'Message3'),
       (7, 1, 'Message4'),
       (7, 4, 'Message5');

--Запрос 1
SELECT u.username, u.display_name
FROM users u
         JOIN chat_members cm ON cm.user_id = u.id
WHERE cm.chat_id = 1;

--Запрос 2
SELECT c.id, COUNT(m.id) as message_count
FROM chats c
         JOIN messages m ON m.chat_id = c.id
GROUP BY c.id;

--Запрос 3
SELECT u.id, COUNT(m.id) as message_count
FROM users u
         LEFT JOIN messages m ON m.user_id = u.id
GROUP BY u.id, u.username;

--Запрос 4
SELECT u.username, COUNT(m.id) as message_count
FROM users u
         JOIN messages m ON m.user_id = u.id
GROUP BY u.id, u.username
HAVING COUNT(m.id) >= 5
ORDER BY message_count DESC
LIMIT 5;

--Запрос 5
SELECT c.title, COUNT(cm.user_id) as members_count
FROM chats c
         JOIN chat_members cm ON c.id = cm.chat_id
GROUP BY (c.id, c.title)
HAVING COUNT(cm.user_id) > 10;

--Запрос 6
SELECT DISTINCT u.username
FROM chat_members cm1
         JOIN chat_members cm2 ON cm2.chat_id = cm1.chat_id
         JOIN users u ON u.id = cm2.user_id
WHERE cm1.user_id = 1 AND cm2.user_id <> 1;

--Запрос 7
SELECT DISTINCT ON (m.chat_id) m.chat_id,
                               m.text,
                               u.username
FROM messages m
         JOIN users u ON m.user_id = u.id
ORDER BY m.chat_id, m.created_at DESC;

--Запрос 8
BEGIN;

INSERT INTO users (username, display_name)
VALUES ('user1', 'Pavel')
RETURNING id as user_id
\gset

INSERT INTO chats (title, created_by)
VALUES ('chat1', :user_id)
RETURNING id as chat_id
\gset

INSERT INTO chat_members (chat_id, user_id)
VALUES (:chat_id, :user_id);

COMMIT;
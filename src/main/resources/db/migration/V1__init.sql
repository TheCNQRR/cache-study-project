CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       display_name VARCHAR(255),
                       created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE chats (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       created_by BIGINT NOT NULL REFERENCES users(id),
                       created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE chat_members (
                              chat_id BIGINT NOT NULL REFERENCES chats(id),
                              user_id BIGINT NOT NULL REFERENCES users(id),
                              PRIMARY KEY (chat_id, user_id)
);

CREATE TABLE messages (
                          id BIGSERIAL PRIMARY KEY,
                          chat_id BIGINT NOT NULL REFERENCES chats(id),
                          sender_id BIGINT NOT NULL REFERENCES users(id),
                          text TEXT NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT now()
);

INSERT INTO users (username, display_name) VALUES
                                               ('alice', 'Alice'),
                                               ('bob', 'Bob'),
                                               ('charlie', 'Charlie');

INSERT INTO chats (title, created_by) VALUES
                                          ('Привет', 1),
                                          ('Рабочий', 2);

INSERT INTO chat_members (chat_id, user_id) VALUES
                                                (1, 1), (1, 2),
                                                (2, 2), (2, 3);

INSERT INTO messages (chat_id, sender_id, text) VALUES
                                                    (1, 1, 'Привет, AliceBob!'),
                                                    (1, 2, 'Привет, Bob!'),
                                                    (2, 2, 'Что делаешь'),
                                                    (2, 3, 'Изучаю spring boot');
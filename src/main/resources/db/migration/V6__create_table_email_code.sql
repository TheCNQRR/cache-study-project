CREATE TABLE email_codes
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT      NOT NULL REFERENCES users (id),
    code_hash  VARCHAR(64) NOT NULL,
    expires_at TIMESTAMP   NOT NULL,
    confirmed  BOOLEAN     NOT NULL DEFAULT FALSE
)
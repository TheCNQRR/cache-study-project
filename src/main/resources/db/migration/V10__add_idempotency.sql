CREATE TABLE processed_request
(
    request_id VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    result     TEXT
);
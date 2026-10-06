ALTER TABLE notifications
    ALTER COLUMN event_id TYPE BIGINT USING event_id::BIGINT;
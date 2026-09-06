package by.java.enterprise.dto.response;

import java.sql.Timestamp;

public record ApiError(
        int code,
        String message,
        Timestamp timestamp,
        String path
) {
}

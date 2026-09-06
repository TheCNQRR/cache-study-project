package by.java.enterprise.dto.response;

import java.sql.Timestamp;
import java.util.Map;

public record ValidationError(
        int code,
        String message,
        Timestamp timestamp,
        String path,
        Map<String, String> errors
) {
}

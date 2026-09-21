package by.java.enterprise.dto.response;

import java.time.LocalDateTime;

public record UpdateUserResponse(
        long id,
        String username,
        String displayName,
        LocalDateTime createdAt
) {
}

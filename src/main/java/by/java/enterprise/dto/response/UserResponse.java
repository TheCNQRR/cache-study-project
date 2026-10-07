package by.java.enterprise.dto.response;

import java.time.LocalDateTime;

public record UserResponse(
        long id,
        String email,
        String username,
        String displayName,
        LocalDateTime createdAt
) {
}

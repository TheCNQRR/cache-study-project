package by.java.enterprise.dto.response;

import java.time.LocalDateTime;

public record CreateUserResponse(
        long id,
        String username,
        String displayName,
        LocalDateTime createdAt
) {
}

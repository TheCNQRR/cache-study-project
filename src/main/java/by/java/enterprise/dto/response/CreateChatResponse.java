package by.java.enterprise.dto.response;

import by.java.enterprise.model.User;

import java.time.LocalDateTime;

public record CreateChatResponse(
        long id,
        String title,
        User createdBy,
        LocalDateTime createdAt
) {
}

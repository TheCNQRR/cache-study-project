package by.java.enterprise.dto.response;

import by.java.enterprise.interfaces.Content;
import by.java.enterprise.model.Chat;
import by.java.enterprise.model.User;

import java.time.Instant;
import java.time.LocalDateTime;

public record CreateMessageResponse(
        long messageId,
        Chat chat,
        User sender,
        String text,
        LocalDateTime createdAt
) {
}

package by.java.enterprise.dto.response;

import by.java.enterprise.model.Chat;
import by.java.enterprise.model.User;

import java.time.LocalDateTime;

public record UpdateMessageResponse(
        long id,
        Chat chat,
        User sender,
        LocalDateTime createdAt
) {
}

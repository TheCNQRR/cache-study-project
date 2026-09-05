package by.java.enterprise.dto.response;

import by.java.enterprise.interfaces.Content;

import java.time.Instant;

public record CreateMessageResponse(
        long messageId,
        long chatId,
        long senderId,
        Content content,
        Instant sentAt
) {
}

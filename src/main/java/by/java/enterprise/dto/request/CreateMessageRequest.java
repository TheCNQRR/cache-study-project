package by.java.enterprise.dto.request;

import by.java.enterprise.interfaces.Content;

public record CreateMessageRequest(
        long chatId,
        long senderId,
        Content content
) {
}

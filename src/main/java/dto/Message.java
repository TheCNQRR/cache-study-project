package dto;

import java.time.Instant;

public record Message(
        Long id,
        Long chatId,
        Long senderId,
        Content content,
        Instant sentAt
) {
}

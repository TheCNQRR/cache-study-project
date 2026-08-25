package model;

import interfaces.Content;
import java.time.Instant;

public record Message(
        long id,
        long chatId,
        long senderId,
        Content content,
        Instant sentAt
) {
}

package by.java.enterprise.record;

import by.java.enterprise.interfaces.Content;
import java.time.Instant;

public record Message(
        long id,
        long chatId,
        long senderId,
        Content content,
        Instant sentAt,
        boolean isViewed
) {
}

package by.java.enterprise.model;

import by.java.enterprise.interfaces.Content;
import java.time.Instant;

public record Message(
        long id,
        long chatId,
        long senderId,
        Content content,
        Instant sentAt
        //TODO поле, которое отвечает за прочитанность сообщения
) {
}

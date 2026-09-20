package by.java.enterprise.dto.request;

public record CreateMessageRequest(
        long chatId,
        long senderId,
        String text
) {
}

package by.java.enterprise.dto.request;

public record ViewMessageRequest(
        long chatId,
        long messageId
) {
}

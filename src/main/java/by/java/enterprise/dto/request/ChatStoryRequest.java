package by.java.enterprise.dto.request;

public record ChatStoryRequest(
        long chatId,
        long offset,
        long limit
) {
}

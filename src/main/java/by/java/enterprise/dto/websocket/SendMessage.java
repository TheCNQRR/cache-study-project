package by.java.enterprise.dto.websocket;

public record SendMessage(Long chatId, Long senderId, String text) {
}

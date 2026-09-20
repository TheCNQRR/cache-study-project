package by.java.enterprise.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChatMemberId implements Serializable {

    Long chatId;
    Long userId;

    public ChatMemberId() {}
    public ChatMemberId(Long chatId, Long userId) {
        this.chatId = chatId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ChatMemberId chatMemberId)) return false;

        return Objects.equals(chatId, chatMemberId.chatId) && Objects.equals(userId, chatMemberId.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, userId);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}

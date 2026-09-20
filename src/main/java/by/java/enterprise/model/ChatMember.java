package by.java.enterprise.model;

import jakarta.persistence.*;

@Entity
@Table(name = "chat_members")
public class ChatMember {

    @EmbeddedId
    ChatMemberId id;

    @ManyToOne
    @MapsId("chatId")
    @JoinColumn(name = "chat_id")
    Chat chat;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User user;

    public ChatMember() {}
    public ChatMember(ChatMemberId id, Chat chat, User user) {
        this.id = id;
        this.chat = chat;
        this.user = user;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public ChatMemberId getId() {
        return id;
    }

    public void setId(ChatMemberId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

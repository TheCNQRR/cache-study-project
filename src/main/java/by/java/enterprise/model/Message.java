package by.java.enterprise.model;

import by.java.enterprise.message.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    User sender;

    @Column(nullable = false)
    String text;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MessageStatus status = MessageStatus.SENT;

    @Transient
    @JsonIgnore
    private MessageState state;

    public Message() {
        this.state = new SentState();
    }
    public Message(Chat chat, User sender, String text, LocalDateTime createdAt) {
        this.chat = chat;
        this.sender = sender;
        this.text = text;
    }

    @PostLoad
    void restoreState() {
        this.state = switch (status) {
            case SENT -> new SentState();
            case DELIVERED -> new DeliveredState();
            case READ -> new ReadState();
        };
    }

    public void deliver() {
        state.deliver(this);
    }

    public void read() {
        state.read(this);
    }

    public MessageState getState() {
        return state;
    }

    public void setState(MessageState newState) {
        this.state = newState;
        this.status = newState.status();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

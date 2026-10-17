package by.java.enterprise.message;

import by.java.enterprise.model.Message;

public interface MessageState {
    MessageStatus status();
    void deliver(Message message);
    void read(Message message);
}

package by.java.enterprise.message;

import by.java.enterprise.model.Message;

public class ReadState implements MessageState {
    @Override
    public MessageStatus status() { return MessageStatus.READ; }

    @Override
    public void deliver(Message message) {
        throw new IllegalStateException("Message is already read");
    }

    @Override
    public void read(Message message) {
        throw new IllegalStateException("Message is already read");
    }
}

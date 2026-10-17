package by.java.enterprise.message;

import by.java.enterprise.model.Message;

public class DeliveredState implements MessageState {
    @Override
    public MessageStatus status() { return MessageStatus.DELIVERED; }

    @Override
    public void deliver(Message message) {
        throw new IllegalStateException("Message is already delivered");
    }

    @Override
    public void read(Message message) {
        message.setState(new ReadState());
    }
}

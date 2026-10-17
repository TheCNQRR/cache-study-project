package by.java.enterprise.message;

import by.java.enterprise.model.Message;

public class SentState implements MessageState {
    @Override
    public MessageStatus status() { return MessageStatus.SENT; }

    @Override
    public void deliver(Message message) {
        message.setState(new DeliveredState());
    }

    @Override
    public void read(Message message) {
        throw new IllegalStateException("Cannot read message before it is delivered");
    }
}

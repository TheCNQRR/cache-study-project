import by.java.enterprise.message.DeliveredState;
import by.java.enterprise.message.ReadState;
import by.java.enterprise.message.SentState;
import by.java.enterprise.model.Message;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageTransitionsTest {

    @Test
    void newMessageStartsInSentState() {
        Message message = new Message();
        assertInstanceOf(SentState.class, message.getState());
    }

    @Test
    void sentToDeliveredIsAllowed() {
        Message message = new Message();
        message.deliver();
        assertInstanceOf(DeliveredState.class, message.getState());
    }

    @Test
    void deliveredToReadIsAllowed() {
        Message message = new Message();
        message.deliver();
        message.read();
        assertInstanceOf(ReadState.class, message.getState());
    }

    @Test
    void sentToReadIsForbidden() {
        Message message = new Message();
        assertThrows(IllegalStateException.class, message::read);
        assertInstanceOf(SentState.class, message.getState());
    }

    @Test
    void deliveredToDeliveredIsForbidden() {
        Message message = new Message();
        message.deliver();
        assertThrows(IllegalStateException.class, message::deliver);
        assertInstanceOf(DeliveredState.class, message.getState());
    }

    @Test
    void readToDeliveredIsForbidden() {
        Message message = new Message();
        message.deliver();
        message.read();
        assertThrows(IllegalStateException.class, message::deliver);
        assertInstanceOf(ReadState.class, message.getState());
    }

    @Test
    void readToReadIsForbidden() {
        Message message = new Message();
        message.deliver();
        message.read();
        assertThrows(IllegalStateException.class, message::read);
        assertInstanceOf(ReadState.class, message.getState());
    }
}

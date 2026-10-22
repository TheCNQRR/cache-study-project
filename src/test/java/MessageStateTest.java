import by.java.enterprise.message.DeliveredState;
import by.java.enterprise.message.ReadState;
import by.java.enterprise.message.SentState;
import by.java.enterprise.model.Message;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MessageStateTest {

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
    void invalidTransitionThrowsException() {
        Message message = new Message();

        assertThrows(IllegalStateException.class, message::read);
        assertInstanceOf(SentState.class, message.getState());

        message.deliver();

        assertThrows(IllegalStateException.class, message::deliver);

        message.read();

        assertThrows(IllegalStateException.class, message::deliver);
        assertThrows(IllegalStateException.class, message::read);
    }
}

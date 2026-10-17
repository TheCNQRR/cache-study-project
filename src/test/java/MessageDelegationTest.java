import by.java.enterprise.message.MessageState;
import by.java.enterprise.model.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageDelegationTest {

    @Mock
    MessageState mockState;

    @Test
    void deliver_delegatesToCurrentState() {
        Message message = new Message();
        message.setState(mockState);

        message.deliver();

        verify(mockState).deliver(message);
        verify(mockState, never()).read(any());
    }
}

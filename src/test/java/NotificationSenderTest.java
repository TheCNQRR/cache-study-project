import by.java.enterprise.notification.ChannelType;
import by.java.enterprise.notification.NotificationChannel;
import by.java.enterprise.notification.NotificationMessage;
import by.java.enterprise.notification.NotificationSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationSenderTest {

    @Mock
    NotificationChannel emailChannel;

    @Mock
    NotificationChannel pushChannel;

    @Mock
    NotificationMessage message;

    @Test
    void sendDelegatesToCorrectChannel() {
        when(emailChannel.type()).thenReturn(ChannelType.EMAIL);
        when(pushChannel.type()).thenReturn(ChannelType.PUSH);
        NotificationSender sender = new NotificationSender(List.of(emailChannel, pushChannel));

        sender.send(ChannelType.EMAIL, message);

        verify(emailChannel).send(message);
        verify(pushChannel, never()).send(any());
    }

    @Test
    void sendUnknownChannelThrowsException() {
        when(emailChannel.type()).thenReturn(ChannelType.EMAIL);
        NotificationSender sender = new NotificationSender(List.of(emailChannel));

        assertThrows(IllegalArgumentException.class,
                () -> sender.send(ChannelType.SMS, message));
    }
}

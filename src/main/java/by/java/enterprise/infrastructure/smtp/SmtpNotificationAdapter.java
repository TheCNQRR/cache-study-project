package by.java.enterprise.infrastructure.smtp;

import by.java.enterprise.core.port.out.NotificationPort;
import by.java.enterprise.notification.ChannelType;
import by.java.enterprise.notification.NotificationMessage;
import by.java.enterprise.notification.NotificationSender;
import org.springframework.stereotype.Component;

@Component
public class SmtpNotificationAdapter implements NotificationPort {

    private final NotificationSender notificationSender;

    public SmtpNotificationAdapter(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    @Override
    public void sendConfirmationSuccess(String email) {
        String body = "Ваша почта успешно подтверждена!";
        notificationSender.send(ChannelType.EMAIL,
                new NotificationMessage(email, "Успешное подтверждение", body));
    }
}

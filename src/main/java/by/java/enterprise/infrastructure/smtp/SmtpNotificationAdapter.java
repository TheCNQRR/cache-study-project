package by.java.enterprise.infrastructure.smtp;

import by.java.enterprise.core.port.out.NotificationPort;
import by.java.enterprise.notification.ChannelType;
import by.java.enterprise.notification.NotificationMessage;
import by.java.enterprise.notification.NotificationSender;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class SmtpNotificationAdapter implements NotificationPort {

    private final NotificationSender notificationSender;

    public SmtpNotificationAdapter(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    @Override
    @CircuitBreaker(name = "emailBreaker", fallbackMethod = "fallbackSend")
    @Retry(name = "emailRetry")
    @Bulkhead(name = "emailBulkhead")
    @TimeLimiter(name = "emailLimiter")
    public CompletableFuture<Void> sendConfirmationSuccess(String email) {

        return CompletableFuture.runAsync(() -> {
            String body = "Ваша почта успешно подтверждена!";
            notificationSender.send(ChannelType.EMAIL,
                    new NotificationMessage(email, "Успешное подтверждение", body));
        });
    }
}

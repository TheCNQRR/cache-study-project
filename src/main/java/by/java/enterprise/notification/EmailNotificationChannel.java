package by.java.enterprise.notification;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class EmailNotificationChannel implements NotificationChannel {

    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationChannel.class);


    public EmailNotificationChannel(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public ChannelType type() {
        return ChannelType.EMAIL;
    }

    @Override
    @CircuitBreaker(name = "emailBreaker", fallbackMethod = "fallbackSend")
    @Retry(name = "emailRetry")
    @Bulkhead(name = "emailBulkhead")
    @TimeLimiter(name = "emailLimiter")
    public CompletableFuture<Void> send(NotificationMessage message) {
        return CompletableFuture.runAsync(() -> {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(message.getRecipient());
            mail.setSubject(message.getSubject());
            mail.setText(message.getBody());
            mailSender.send(mail);
        });
    }

    public CompletableFuture<Void> fallbackSend(NotificationMessage message, Throwable t) {
        logger.warn("Fallback: не удалось отправить email на {}. Ошибка: {}",
                message.getRecipient(), t.getMessage());
        return CompletableFuture.completedFuture(null);
    }
}

package by.java.enterprise.kafka.consumer;

import by.java.enterprise.kafka.model.UserRegisteredEvent;
import by.java.enterprise.notification.ChannelType;
import by.java.enterprise.notification.NotificationMessage;
import by.java.enterprise.notification.NotificationSender;
import by.java.enterprise.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

@Component
public class UserRegisteredConsumer {

    private final Logger logger = LoggerFactory.getLogger(UserRegisteredConsumer.class);

    private final NotificationService notificationService;
    private final NotificationSender sender;
    private final SecureRandom secureRandom = new SecureRandom();

    public UserRegisteredConsumer(
            NotificationService notificationService,
            NotificationSender sender
    ) {
        this.notificationService = notificationService;
        this.sender = sender;
    }

    @KafkaListener(topics = "user.registered", groupId = "notification-group")
    public void consume(UserRegisteredEvent event) {
        logger.info("Consumed event: eventId={}, email={}", event.userId(), event.email());

        int code = secureRandom.nextInt(100000, 1000000);
        String codeHash = sha256(String.valueOf(code));

        try {
            notificationService.saveCodeAndNotification(event.userId(), codeHash);
        } catch (Exception e) {
            logger.warn("Duplicate event, skipping DB writes. userId={}", event.userId());
        }

        try {
            String body = """
                    Здравствуйте, %s!

                    Ваш код подтверждения: %d

                    Подтвердите адрес, перейдя по ссылке:
                    http://localhost:8080/api/v1/auth/confirm-email?email=%s&code=%d

                    Код действует 15 минут.
                    """.formatted(event.username(), code, event.email(), code);

            NotificationMessage message = new NotificationMessage(
                    "ap166884@gmail.com",
                    "Код подтверждения",
                    body
            );

            sender.send(ChannelType.EMAIL, message);
            logger.info("Sent via EMAIL to {}", event.email());
        } catch (Exception e) {
            logger.error("Failed to send email to {}", event.email(), e);
            throw e;
        }
    }

    private String sha256(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digest = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}

package by.java.enterprise.kafka.consumer;

import by.java.enterprise.exception.DuplicateNotificationException;
import by.java.enterprise.kafka.model.UserRegisteredEvent;
import by.java.enterprise.model.Notification;
import by.java.enterprise.service.EmailService;
import by.java.enterprise.service.NotificationService;
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
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public UserRegisteredConsumer(NotificationService notificationService, EmailService emailService) {
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user.registered", groupId = "notification-group")
    public void consume(UserRegisteredEvent event) {
        logger.info("Consumed event: eventId={}, email={}", event.userId(), event.email());

        int code = secureRandom.nextInt(100000, 1000000);
        String codeHash = sha256(String.valueOf(code));

        try {
            notificationService.saveCodeAndNotification(event.userId(), codeHash);
        } catch (DuplicateNotificationException e) {
            logger.warn("Duplicate event, skipping. eventId={}", event.userId());
        }

        try {
            emailService.sendConfirmationCode(event.email(), event.username(), code);
            logger.info("Email sent to {}", event.email());
        } catch (Exception e) {
            logger.error("failed to send email to {}", event.email(), e);
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

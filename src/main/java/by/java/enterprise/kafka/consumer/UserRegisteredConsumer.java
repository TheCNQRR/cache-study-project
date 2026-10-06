package by.java.enterprise.kafka.consumer;

import by.java.enterprise.exception.DuplicateNotificationException;
import by.java.enterprise.kafka.model.UserRegisteredEvent;
import by.java.enterprise.model.Notification;
import by.java.enterprise.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredConsumer {

    private final Logger logger = LoggerFactory.getLogger(UserRegisteredConsumer.class);

    private final NotificationRepository notificationRepository;

    public UserRegisteredConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @KafkaListener(topics = "user.registered", groupId = "notification-group")
    public void consume(UserRegisteredEvent event) {
        try {
            Notification notification = new Notification();
            notification.setEventId(event.userId());
            notificationRepository.save(notification);
        } catch (DuplicateNotificationException e) {
            logger.warn("Duplicate event, skipping. eventId={}", event.userId());
        }
    }
}

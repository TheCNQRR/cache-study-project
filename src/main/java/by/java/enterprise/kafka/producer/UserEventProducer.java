package by.java.enterprise.kafka.producer;

import by.java.enterprise.kafka.model.UserRegisteredEvent;
import by.java.enterprise.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventProducer {

    private final Logger logger = LoggerFactory.getLogger(UserEventProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void userRegistered(User user) {
        kafkaTemplate.send(
                "user.registered",
                String.valueOf(user.getId()),
                new UserRegisteredEvent(
                        user.getId(),
                        user.getUsername()
                )
        );
        logger.info("User register, id={}, username={}", user.getId(), user.getUsername());
    }
}

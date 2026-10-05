package by.java.enterprise.kafka.model;

public record UserRegisteredEvent(
        Long userId,
        String username
) {
}

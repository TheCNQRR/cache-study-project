package by.java.enterprise.kafka.model;

public record UserRegisteredEvent(
        Long userId,
        String email,
        String username
) {
}

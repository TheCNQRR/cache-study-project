package by.java.enterprise.dto.request;

public record CreateUserRequest(
        String username,
        String displayName
) {
}

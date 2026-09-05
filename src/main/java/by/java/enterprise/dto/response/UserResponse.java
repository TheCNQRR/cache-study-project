package by.java.enterprise.dto.response;

public record UserResponse(
        long id,
        String username,
        String displayName
) {
}

package by.java.enterprise.dto.response;

public record CreateUserResponse(
        long id,
        String username,
        String displayName
) {
}

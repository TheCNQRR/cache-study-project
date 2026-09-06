package by.java.enterprise.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Username не может быть пустым")
        @Size(min = 3, max = 32, message = "Username должен быть от 3 до 32 символов")
        String username,

        @NotBlank(message = "Display name не может быть пустым")
        @Size(min = 3, max = 32, message = "Display name должен быть от 3 до 32 символов")
        String displayName
) {
}

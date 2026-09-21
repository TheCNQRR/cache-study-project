package by.java.enterprise.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserFullRequest(
        @NotBlank(message = "username не может быть пустым")
        String username,

        @NotBlank(message = "displayName не может быть пустым")
        String displayName
) {
}

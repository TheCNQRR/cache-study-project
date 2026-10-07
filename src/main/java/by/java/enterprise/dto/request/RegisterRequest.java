package by.java.enterprise.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String username,

        @NotBlank
        String password,

        String displayName
) {
}

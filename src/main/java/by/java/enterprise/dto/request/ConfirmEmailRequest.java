package by.java.enterprise.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ConfirmEmailRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String code
) {
}

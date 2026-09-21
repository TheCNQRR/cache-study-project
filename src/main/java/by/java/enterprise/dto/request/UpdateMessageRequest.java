package by.java.enterprise.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateMessageRequest(
        @NotBlank(message = "Text не может быть пустым")
        String text
) {
}

package by.java.enterprise.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateChatRequest(
        @NotBlank(message = "Title не может быть пустым")
        String title
) {
}

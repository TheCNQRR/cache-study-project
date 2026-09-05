package by.java.enterprise.dto.response;

import by.java.enterprise.model.Message;

import java.util.List;

public record ChatStoryResponse(
        List<Message> messages
) {
}

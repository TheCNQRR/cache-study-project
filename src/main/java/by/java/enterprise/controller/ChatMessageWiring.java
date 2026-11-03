package by.java.enterprise.controller;

import by.java.enterprise.model.Chat;
import by.java.enterprise.model.Message;
import by.java.enterprise.service.MessageService;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ChatMessageWiring {

    private final MessageService messageService;

    public ChatMessageWiring(MessageService messageService) {
        this.messageService = messageService;
    }

    @BatchMapping
    public Map<Chat, List<Message>> messages(List<Chat> chats) {
        List<Long> chatIds = chats.stream().map(Chat::getId).toList();

        Map<Long, List<Message>> messagesByChatId = messageService.groupByChat(chatIds);

        return chats.stream()
                .collect(Collectors.toMap(
                        chat -> chat,
                        chat -> messagesByChatId.getOrDefault(chat.getId(), List.of())
                ));
    }
}

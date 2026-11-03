package by.java.enterprise.controller;

import by.java.enterprise.model.Chat;
import by.java.enterprise.service.ChatService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ChatQueryController {

    private final ChatService chatService;

    public ChatQueryController(ChatService chatService) {
        this.chatService = chatService;
    }

    @QueryMapping
    public List<Chat> chats() {
        return chatService.findAllChats();
    }

    @QueryMapping
    public Chat chat(@Argument Long id) {
        return chatService.findChatById(id)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
    }
}

package by.java.enterprise.controller;

import by.java.enterprise.dto.websocket.SendMessage;
import by.java.enterprise.model.Message;
import by.java.enterprise.service.ChatRealtimeService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ChatMessageHandler {

    private final ChatRealtimeService chatRealtimeService;

    public ChatMessageHandler(ChatRealtimeService chatRealtimeService) {
        this.chatRealtimeService = chatRealtimeService;
    }

    @MessageMapping("/chat/send")
    public void handleSend(SendMessage msg) {
        Message saved = chatRealtimeService.saveAndGet(msg);
        chatRealtimeService.broadcastToChat(saved);
    }
}

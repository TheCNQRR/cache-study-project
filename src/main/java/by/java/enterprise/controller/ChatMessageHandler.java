package by.java.enterprise.controller;

import by.java.enterprise.dto.websocket.SendMessage;
import by.java.enterprise.model.Message;
import by.java.enterprise.service.ChatRealtimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Controller
public class ChatMessageHandler {
    private static final Logger log = LoggerFactory.getLogger(ChatMessageHandler.class);

    private final ChatRealtimeService chatRealtimeService;

    public ChatMessageHandler(ChatRealtimeService chatRealtimeService) {
        this.chatRealtimeService = chatRealtimeService;
    }

    @MessageMapping("/chat/send")
    public void handleSend(SendMessage msg) {
        long start = System.currentTimeMillis();

        Message saved = chatRealtimeService.saveAndGet(msg);
        chatRealtimeService.broadcastToChat(saved);

        long duration = System.currentTimeMillis() - start;


        log.info("Message processed",
                kv("chatId", msg.chatId()),
                kv("messageId", saved.getId()),
                kv("durationMs", duration)
        );
    }
}

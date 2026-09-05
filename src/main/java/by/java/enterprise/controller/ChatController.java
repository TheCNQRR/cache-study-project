package by.java.enterprise.controller;

import by.java.enterprise.dto.request.ChatStoryRequest;
import by.java.enterprise.dto.request.CreateChatRequest;
import by.java.enterprise.dto.request.CreateMessageRequest;
import by.java.enterprise.dto.request.ViewMessageRequest;
import by.java.enterprise.dto.response.ChatStoryResponse;
import by.java.enterprise.dto.response.CreateChatResponse;
import by.java.enterprise.dto.response.CreateMessageResponse;
import by.java.enterprise.interfaces.Content;
import by.java.enterprise.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("api/v1/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<?> createChat(@RequestBody CreateChatRequest request) {
        CreateChatResponse chat = chatService.createChat(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(chat);
    }

    @PostMapping("/{chatId}/message")
    public ResponseEntity<CreateMessageResponse> createMessage(
            @PathVariable long chatId,
            @RequestHeader("x-current-user-id") long senderId,
            @RequestBody Content content
            ) {
        CreateMessageRequest request = new CreateMessageRequest(
                chatId,
                senderId,
                content
        );

        CreateMessageResponse message = chatService.createMessage(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping("/{id}/story")
    public ResponseEntity<ChatStoryResponse> getChatStory(@PathVariable long id, @RequestBody long quantity) {
        ChatStoryRequest request = new ChatStoryRequest(id, quantity);

        ChatStoryResponse chatStory = chatService.getChatStory(request);

        return ResponseEntity.status(HttpStatus.OK).body(chatStory);
    }

    @PatchMapping("/{chatId}/messages/{messageId}")
    public ResponseEntity<?> viewMessage(@PathVariable long chatId, @PathVariable long messageId) {
        ViewMessageRequest request = new ViewMessageRequest(chatId, messageId);
        chatService.viewMessage(request);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

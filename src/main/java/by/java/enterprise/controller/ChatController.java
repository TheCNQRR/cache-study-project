package by.java.enterprise.controller;

import by.java.enterprise.dto.request.CreateChatRequest;
import by.java.enterprise.dto.request.CreateMessageRequest;
import by.java.enterprise.dto.request.UpdateChatRequest;
import by.java.enterprise.dto.response.CreateChatResponse;
import by.java.enterprise.dto.response.CreateMessageResponse;
import by.java.enterprise.dto.response.UpdateChatResponse;
import by.java.enterprise.model.Chat;
import by.java.enterprise.model.Message;
import by.java.enterprise.service.ChatService;
import by.java.enterprise.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/chats")
public class ChatController {

    private final ChatService chatService;
    private final MessageService messageService;

    public ChatController(ChatService chatService, MessageService messageService) {
        this.chatService = chatService;
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<CreateChatResponse> createChat(@RequestBody CreateChatRequest request) {
        CreateChatResponse chat = chatService.createChat(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(chat);
    }

    @GetMapping
    public ResponseEntity<List<Chat>> getChats() {
        List<Chat> chats = chatService.findAllChats();

        return ResponseEntity.status(HttpStatus.OK).body(chats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getChatById(@PathVariable long id) {
        Optional<Chat> chat = chatService.findChatById(id);

        return chat.map(value -> ResponseEntity.status(HttpStatus.OK).body(value)).orElseGet(() ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<CreateMessageResponse> sendMessage(@RequestBody CreateMessageRequest request) {
        CreateMessageResponse message = messageService.createMessage(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<Message>> getMessagesByChatId(@PathVariable long chatId) {
        List<Message> messages = messageService.findAllMessages(chatId);

        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateChatResponse> updateChat(@PathVariable long id, @Valid @RequestBody UpdateChatRequest request) {
        UpdateChatResponse chat = chatService.updateChat(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(chat);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChatById(@PathVariable long id) {
        chatService.deleteChatById(id);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

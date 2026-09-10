package by.java.enterprise.controller;

import by.java.enterprise.dto.request.ChatStoryRequest;
import by.java.enterprise.dto.request.CreateChatRequest;
import by.java.enterprise.dto.request.CreateMessageRequest;
import by.java.enterprise.dto.request.ViewMessageRequest;
import by.java.enterprise.dto.response.ChatStoryResponse;
import by.java.enterprise.dto.response.CreateChatResponse;
import by.java.enterprise.dto.response.CreateMessageResponse;
import by.java.enterprise.interfaces.Content;
import by.java.enterprise.interfaces.MessageSorter;
import by.java.enterprise.model.Message;
import by.java.enterprise.service.ChatService;
import by.java.enterprise.service.MockSingletonService;
import by.java.enterprise.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/chats")
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;
    private final MessageSorter messageSorter;
    private final MockSingletonService mockSingletonService;

    public ChatController(ChatService chatService, UserService userService, @Qualifier("byTimeAsc") MessageSorter messageSorter,
                          MockSingletonService mockSingletonService) {
        this.chatService = chatService;
        this.userService = userService;
        this.messageSorter = messageSorter;
        this.mockSingletonService = mockSingletonService;
        System.out.println(System.identityHashCode(this.userService));
        mockSingletonService.printMockServiceHash();
        mockSingletonService.printMockServiceHash();
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

    @GetMapping("/{id}/messages")
    public ResponseEntity<ChatStoryResponse> getChatHistory(
            @PathVariable long id,
            @RequestParam(defaultValue = "0") long offset,
            @RequestParam(defaultValue = "20") long limit
    ) {
        ChatStoryRequest request = new ChatStoryRequest(id, offset, limit);

        ChatStoryResponse chatStory = chatService.getChatStory(request);

        return ResponseEntity.status(HttpStatus.OK).body(chatStory);
    }

    @PatchMapping("/{chatId}/messages/{messageId}")
    public ResponseEntity<?> viewMessage(@PathVariable long chatId, @PathVariable long messageId) {
        ViewMessageRequest request = new ViewMessageRequest(chatId, messageId);
        chatService.viewMessage(request);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{id}/messages/sorted")
    public ResponseEntity<List<Message>> sortedMessages(@PathVariable long id) {
        List<Message> sorted = chatService.getMessagesByChatId(id).stream()
                .sorted(messageSorter)
                .toList();
        return ResponseEntity.ok(sorted);
    }
}

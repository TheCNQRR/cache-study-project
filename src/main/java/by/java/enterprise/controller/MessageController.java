package by.java.enterprise.controller;

import by.java.enterprise.dto.request.UpdateMessageRequest;
import by.java.enterprise.dto.response.UpdateMessageResponse;
import by.java.enterprise.model.Message;
import by.java.enterprise.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMessageById(@PathVariable long id) {
        Optional<Message> message = messageService.findMessageById(id);

        return message.map(value -> ResponseEntity.status(HttpStatus.OK).body(value)).orElseGet(() ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateMessageResponse> updateMessage(@PathVariable long id, @Valid @RequestBody UpdateMessageRequest request) {
        UpdateMessageResponse message = messageService.updateMessage(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessageById(@PathVariable long id) {
        messageService.deleteMessageById(id);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

package by.java.enterprise.service;

import by.java.enterprise.dto.request.CreateMessageRequest;
import by.java.enterprise.dto.response.CreateMessageResponse;
import by.java.enterprise.exception.ChatNotFoundException;
import by.java.enterprise.exception.UserNotFoundException;
import by.java.enterprise.model.Chat;
import by.java.enterprise.model.Message;
import by.java.enterprise.model.User;
import by.java.enterprise.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final UserService userService;
    private final ChatService chatService;
    private final MessageRepository messageRepository;

    public MessageService(UserService userService, ChatService chatService, MessageRepository messageRepository) {
        this.userService = userService;
        this.chatService = chatService;
        this.messageRepository = messageRepository;
    }

    public List<Message> findAllMessages(long chatId) {
        return messageRepository.findByChatIdOrderByCreatedAt(chatId);
    }

    public Optional<Message> findMessageById(long messageId) {
        return messageRepository.findById(messageId);
    }

    public CreateMessageResponse createMessage(CreateMessageRequest request) {
        Optional<Chat> foundChat = chatService.findChatById(request.chatId());
        if (foundChat.isEmpty()) {
            throw new ChatNotFoundException("Chat with id {" + request.chatId() + "} not found");
        }

        Optional<User> foundUser = userService.findUserById(request.senderId());
        if (foundUser.isEmpty()) {
            throw new UserNotFoundException("User with id {" + request.senderId() + "} not found");
        }

        Chat chat = foundChat.get();
        User sender = foundUser.get();

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setText(request.text());

        return new CreateMessageResponse(
                message.getId(),
                message.getChat(),
                message.getSender(),
                message.getText(),
                message.getCreatedAt()
        );
    }
}

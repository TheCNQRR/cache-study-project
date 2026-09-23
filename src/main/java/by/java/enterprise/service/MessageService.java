package by.java.enterprise.service;

import by.java.enterprise.dto.request.CreateMessageRequest;
import by.java.enterprise.dto.request.UpdateMessageRequest;
import by.java.enterprise.dto.response.CreateMessageResponse;
import by.java.enterprise.dto.response.UpdateMessageResponse;
import by.java.enterprise.exception.ChatNotFoundException;
import by.java.enterprise.exception.MessageNotFoundException;
import by.java.enterprise.exception.UserNotFoundException;
import by.java.enterprise.model.Chat;
import by.java.enterprise.model.Message;
import by.java.enterprise.model.User;
import by.java.enterprise.repository.MessageRepository;
import by.java.enterprise.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final UserRepository userRepository;
    private final ChatService chatService;
    private final MessageRepository messageRepository;

    public MessageService(UserRepository userRepository, ChatService chatService, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.chatService = chatService;
        this.messageRepository = messageRepository;
    }

    public List<Message> findAllMessages(long chatId) {
        return messageRepository.findByChatIdOrderByCreatedAt(chatId);
    }

    public Optional<Message> findMessageById(long messageId) {
        return messageRepository.findById(messageId);
    }

    public CreateMessageResponse createMessage(long chatId, CreateMessageRequest request, String senderUsername) {
        Optional<Chat> foundChat = chatService.findChatById(chatId);
        if (foundChat.isEmpty()) {
            throw new ChatNotFoundException("Chat with id {" + chatId + "} not found");
        }

        Chat chat = foundChat.get();

        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with username {" + senderUsername + "} not found"));

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setText(request.text());

        Message savedMessage = messageRepository.save(message);

        return new CreateMessageResponse(
                savedMessage.getId(),
                savedMessage.getChat(),
                savedMessage.getSender(),
                savedMessage.getText(),
                savedMessage.getCreatedAt()
        );
    }

    public UpdateMessageResponse updateMessage(long id, UpdateMessageRequest request) {
        Optional<Message> foundMessage = messageRepository.findById(id);

        if (foundMessage.isEmpty()) {
            throw new MessageNotFoundException("Message with id {" + id + "} not found" );
        }

        Message message = foundMessage.get();

        message.setText(request.text());
        messageRepository.save(message);

        return new UpdateMessageResponse(
                message.getId(),
                message.getChat(),
                message.getSender(),
                message.getCreatedAt()
        );
    }

    public void deleteMessageById(long id) {
        messageRepository.deleteById(id);
    }
}

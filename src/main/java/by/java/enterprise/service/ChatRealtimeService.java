package by.java.enterprise.service;

import by.java.enterprise.dto.websocket.SendMessage;
import by.java.enterprise.exception.ChatNotFoundException;
import by.java.enterprise.exception.UserNotFoundException;
import by.java.enterprise.model.Chat;
import by.java.enterprise.model.Message;
import by.java.enterprise.model.User;
import by.java.enterprise.repository.ChatRepository;
import by.java.enterprise.repository.MessageRepository;
import by.java.enterprise.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatRealtimeService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatRealtimeService(ChatRepository chatRepository,
                               UserRepository userRepository,
                               MessageRepository messageRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public Message saveAndGet(SendMessage msg) {
        Chat chat = chatRepository.findById(msg.chatId()).orElseThrow(() ->
                new ChatNotFoundException("Chat with id {" + msg.chatId() + "} not found"));

        User sender = userRepository.findById(msg.senderId()).orElseThrow(() ->
                new UserNotFoundException("User with id {" + msg.senderId() + "} not found"));

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setText(msg.text());

        return messageRepository.save(message);
    }

    public void broadcastToChat(Message msg) {
        String destination = "/topic/chat/" + msg.getChat().getId();
        messagingTemplate.convertAndSend(destination, msg);
    }
}

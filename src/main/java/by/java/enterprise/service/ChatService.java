package by.java.enterprise.service;

import by.java.enterprise.dto.request.CreateChatRequest;
import by.java.enterprise.dto.request.UpdateChatRequest;
import by.java.enterprise.dto.response.CreateChatResponse;
import by.java.enterprise.dto.response.UpdateChatResponse;
import by.java.enterprise.exception.ChatNotFoundException;
import by.java.enterprise.exception.UserNotFoundException;
import by.java.enterprise.model.Chat;
import by.java.enterprise.model.User;
import by.java.enterprise.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final UserService userService;
    private final ChatRepository chatRepository;

    public ChatService(UserService userService, ChatRepository chatRepository) {
        this.userService = userService;
        this.chatRepository = chatRepository;
    }

    public List<Chat> findAllChats() {
        return chatRepository.findAll();
    }

    public Optional<Chat> findChatById(long id) {
        return chatRepository.findById(id);
    }

    public CreateChatResponse createChat(CreateChatRequest request) {
        Optional<User> foundUser = userService.findUserById(request.createdBy());

        if (foundUser.isEmpty()) {
            throw new UserNotFoundException("user with id {" + request.createdBy() + "} not found");
        }

        User user = foundUser.get();

        Chat chat = chatRepository.save(
                new Chat(
                     request.title(),
                     user
                )
        );

        return new CreateChatResponse(
                chat.getId(),
                chat.getTitle(),
                chat.getCreatedBy(),
                chat.getCreatedAt()
        );
    }

    public UpdateChatResponse updateChat(long id, UpdateChatRequest request) {
        Optional<Chat> foundChat = chatRepository.findById(id);

        if (foundChat.isEmpty()) {
            throw new ChatNotFoundException("Chat with id {" + id + "} not found" );
        }

        Chat chat = foundChat.get();

        chat.setTitle(request.title());
        chatRepository.save(chat);

        return new UpdateChatResponse(
                chat.getId(),
                chat.getTitle(),
                chat.getCreatedBy(),
                chat.getCreatedAt()
        );
    }

    public void deleteChatById(long id) {
        chatRepository.deleteById(id);
    }
}

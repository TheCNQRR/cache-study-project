package by.java.enterprise.service;

import by.java.enterprise.dto.request.CreateUserRequest;
import by.java.enterprise.dto.response.CreateUserResponse;
import by.java.enterprise.dto.response.UserResponse;
import by.java.enterprise.dto.response.UsersResponse;
import by.java.enterprise.exception.DuplicateUsernameException;
import by.java.enterprise.exception.UserNotFoundException;
import by.java.enterprise.model.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public final class UserService {
    private ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();;
    private final AtomicLong idCounter = new AtomicLong(0);
    private final CommonService commonService;

    /**
     * Первый способ
     * @Autowired
     * private ChatService chatService;
     */

    /**
     *  @Autowired
     *  public void setChatService(ChatService chatService) {
     *      this.chatService = chatService;
     *  }
     */

    private ChatService chatService;

    public UserService(@Lazy ChatService chatService, CommonService commonService) {
        this.chatService = chatService;
        this.commonService = commonService;
    }

    public HashMap<Long, User> getUsers() {
        return new HashMap<>(users);
    }

    public CreateUserResponse createUser(CreateUserRequest request) {
        List<User> users = findAllUsers().users();

        Optional<User> userWithDuplicateUsername = users.stream().filter(u -> u.username().equals(request.username())).findFirst();

        if (userWithDuplicateUsername.isPresent()) {
            throw new DuplicateUsernameException("User with username {" + request.username() + "} already exists");
        }

        User user = new User(
                idCounter.getAndIncrement(),
                request.username(),
                request.displayName()
        );

        this.users.put(user.id(), user);

        return new CreateUserResponse(
                user.id(),
                user.username(),
                user.displayName()
        );
    }

    public UsersResponse findAllUsers() {
        HashMap<Long, User> users = getUsers();

        List<User> usersList = users.values().stream().toList();

        return new UsersResponse(usersList);
    }

    public UserResponse findUser(long id) {
        User user = users.getOrDefault(id, null);

        if (user == null) {
            throw new UserNotFoundException("User with id {" + id + "} not found");
        }

        return new UserResponse(
                user.id(),
                user.username(),
                user.displayName()
        );
    }

    User findUserOrThrow(long id) {
        HashMap<Long, User> snapshot = getUsers();

        return snapshot.entrySet().stream()
                .filter(entry -> entry.getKey() == id)
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User with id {" + id + "} not found"));
    }

    public Optional<UserResponse> getUserExpensive(long id) throws InterruptedException {
        return Optional.of(
                getUserFromCache(id)
                        .orElse(getUserFromDb(id))
        );
    }

    public Optional<UserResponse> getUserCheaper(long id) throws InterruptedException {
        return Optional.of(
                getUserFromCache(id)
                        .orElseGet(() -> {
                            try {
                                return getUserFromDb(id);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        })
        );
    }

    public Optional<UserResponse> getUserFromCache(long id) throws InterruptedException {
        Thread.sleep(100);

        return Optional.empty();
    }

    public UserResponse getUserFromDb(long id) throws InterruptedException {
        Thread.sleep(5000);

        return findUser(id);
    }
}

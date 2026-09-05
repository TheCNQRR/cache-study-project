package by.java.enterprise.service;

import by.java.enterprise.dto.request.CreateUserRequest;
import by.java.enterprise.dto.response.CreateUserResponse;
import by.java.enterprise.dto.response.UserResponse;
import by.java.enterprise.dto.response.UsersResponse;
import by.java.enterprise.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public final class UserService {
    private ConcurrentHashMap<Long, User> users;
    private final AtomicLong idCounter = new AtomicLong(0);

    public HashMap<Long, User> getUsers() {
        return new HashMap<>(users);
    }

    public CreateUserResponse createUser(CreateUserRequest request) {
        User user = new User(
                idCounter.getAndIncrement(),
                request.username(),
                request.displayName()
        );

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

    public Optional<UserResponse> findUser(long id) {
        User user = users.getOrDefault(id, null);

        return user == null ? Optional.empty() : Optional.of(new UserResponse(
                user.id(),
                user.username(),
                user.displayName()
        ));
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

        Optional<UserResponse> user = findUser(id);

        if (user.isEmpty()) {
            throw new RuntimeException("User with id {" + id + "} not found");
        }

        return user.get();
    }
}

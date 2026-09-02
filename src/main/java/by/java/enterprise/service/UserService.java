package by.java.enterprise.service;

import by.java.enterprise.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class UserService {
    private ConcurrentHashMap<Long, User> users;
    private final AtomicLong idCounter = new AtomicLong(0);

    public HashMap<Long, User> getUsers() {
        return new HashMap<>(users);
    }

    public User createUser(String username, String displayName) {
        return new User(
                idCounter.getAndIncrement(),
                username,
                displayName
        );
    }


    public Optional<User> findUser(long id) {
        User user = users.getOrDefault(id, null);

        return user == null ? Optional.empty() : Optional.of(user);
    }

    User findUserOrThrow(long id) {
        HashMap<Long, User> snapshot = getUsers();

        return snapshot.entrySet().stream()
                .filter(entry -> entry.getKey() == id)
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User with id {" + id + "} not found"));
    }

    public Optional<User> getUserExpensive(long id) throws InterruptedException {
        return Optional.of(
                getUserFromCache(id)
                        .orElse(getUserFromDb(id))
        );
    }

    public Optional<User> getUserCheaper(long id) throws InterruptedException {
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

    public Optional<User> getUserFromCache(long id) throws InterruptedException {
        Thread.sleep(100);

        return Optional.empty();
    }

    public User getUserFromDb(long id) throws InterruptedException {
        Thread.sleep(5000);

        Optional<User> user = findUser(id);

        if (user.isEmpty()) {
            throw new RuntimeException("User with id {" + id + "} not found");
        }

        return user.get();
    }
}

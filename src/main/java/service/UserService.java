package service;

import model.User;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class UserService {
    private ConcurrentHashMap<Long, User> users;


    public Optional<User> findUser(long id) {
        User user = users.getOrDefault(id, null);

        return user == null ? Optional.empty() : Optional.of(user);
    }
}

package by.java.enterprise.service;

import by.java.enterprise.dto.request.CreateUserRequest;
import by.java.enterprise.dto.request.UpdateUserFullRequest;
import by.java.enterprise.dto.response.CreateUserResponse;
import by.java.enterprise.dto.request.UpdateUserPartRequest;
import by.java.enterprise.dto.response.UpdateUserResponse;
import by.java.enterprise.dto.response.UserResponse;
import by.java.enterprise.exception.DuplicateUsernameException;
import by.java.enterprise.exception.UserNotFoundException;
import by.java.enterprise.model.User;
import by.java.enterprise.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Cacheable(value = "users", key = "#id")
    public UserResponse findUserById(long id) {
        log.info("User not in cache -> db");
        return userRepository.findById(id).map(u ->
                new UserResponse(
                       u.getId(),
                       u.getUsername(),
                       u.getDisplayName(),
                       u.getCreatedAt()
                )
        ).orElseThrow(() -> new UserNotFoundException("User with id {" + id + "} not found"));
    }

    @Cacheable(value = "users", key = "#username")
    public Optional<User> findUserByUsername(String username) {
        log.info("User not in cache -> db");
        return userRepository.findByUsername(username);
    }

    public CreateUserResponse createUser(CreateUserRequest request) {
        List<User> users = findAllUsers();

        Optional<User> userWithDuplicateUsername = users.stream().filter(u -> u.getUsername().equals(request.username())).findFirst();

        if (userWithDuplicateUsername.isPresent()) {
            throw new DuplicateUsernameException("User with username {" + request.username() + "} already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setDisplayName(request.displayName());

        User createdUser = userRepository.save(user);

        return new CreateUserResponse(
                createdUser.getId(),
                createdUser.getUsername(),
                createdUser.getDisplayName(),
                createdUser.getCreatedAt()
        );
    }

    @CacheEvict(value = "users", allEntries = true)
    public UpdateUserResponse updateUserFull(long id, UpdateUserFullRequest request) {
        Optional<User> foundUser = userRepository.findById(id);

        if (foundUser.isEmpty()) {
            throw new UserNotFoundException("User with id {" + id + "} not exists");
        }

        User user = foundUser.get();

        user.setUsername(request.username());
        user.setDisplayName(request.displayName());
        userRepository.save(user);

        return new UpdateUserResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getCreatedAt()
        );
    }

    @CacheEvict(value = "users", allEntries = true)
    public UpdateUserResponse updateUserPart(long id, UpdateUserPartRequest request) {
        Optional<User> foundUser = userRepository.findById(id);

        if (foundUser.isEmpty()) {
            throw new UserNotFoundException("User with id {" + id + "} not exists");
        }

        User user = foundUser.get();

        if (request.username() != null) {
            user.setUsername(request.username());
        }
        if (request.displayName() != null) {
            user.setDisplayName(request.displayName());
        }
        userRepository.save(user);

        return new UpdateUserResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getCreatedAt()
        );
    }

    public void deleteUserById(long id) {
        userRepository.deleteById(id);
    }
}

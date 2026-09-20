package by.java.enterprise.service;

import by.java.enterprise.dto.request.CreateUserRequest;
import by.java.enterprise.dto.response.CreateUserResponse;
import by.java.enterprise.dto.response.UserResponse;
import by.java.enterprise.exception.DuplicateUsernameException;
import by.java.enterprise.exception.UserNotFoundException;
import by.java.enterprise.model.User;
import by.java.enterprise.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public final class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(long id) {
        return userRepository.findById(id);
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
}

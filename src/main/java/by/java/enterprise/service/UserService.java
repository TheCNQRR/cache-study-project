package by.java.enterprise.service;

import by.java.enterprise.dto.request.CreateUserRequest;
import by.java.enterprise.dto.request.UpdateUserFullRequest;
import by.java.enterprise.dto.response.CreateUserResponse;
import by.java.enterprise.dto.request.UpdateUserPartRequest;
import by.java.enterprise.dto.response.UpdateUserResponse;
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

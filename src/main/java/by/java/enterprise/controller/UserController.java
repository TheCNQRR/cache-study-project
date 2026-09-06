package by.java.enterprise.controller;

import by.java.enterprise.dto.request.CreateUserRequest;
import by.java.enterprise.dto.response.CreateUserResponse;
import by.java.enterprise.dto.response.UserResponse;
import by.java.enterprise.dto.response.UsersResponse;
import by.java.enterprise.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
        log.info("UserService hash: {}", System.identityHashCode(this.userService));
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        CreateUserResponse user = userService.createUser(request);

        URI location = URI.create("/api/v1/users/" + user.id());
        return ResponseEntity.created(location).body(user);
    }

    @GetMapping
    public ResponseEntity<UsersResponse> getAllUsers() {
        UsersResponse users = userService.findAllUsers();

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long id) {
        UserResponse user = userService.findUser(id);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}

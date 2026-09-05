package by.java.enterprise.controller;

import by.java.enterprise.dto.request.CreateUserRequest;
import by.java.enterprise.dto.response.CreateUserResponse;
import by.java.enterprise.dto.response.UserResponse;
import by.java.enterprise.dto.response.UsersResponse;
import by.java.enterprise.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) {
        CreateUserResponse user = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping
    public ResponseEntity<UsersResponse> getAllUsers() {
        UsersResponse users = userService.findAllUsers();

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long id) {
        Optional<UserResponse> user = userService.findUser(id);

        return user.map(userResponse ->
                ResponseEntity.status(HttpStatus.OK).body(userResponse)).orElseGet(() ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}

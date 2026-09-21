package by.java.enterprise.controller;

import by.java.enterprise.dto.request.CreateUserRequest;
import by.java.enterprise.dto.request.UpdateUserFullRequest;
import by.java.enterprise.dto.request.UpdateUserPartRequest;
import by.java.enterprise.dto.response.*;
import by.java.enterprise.model.User;
import by.java.enterprise.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable long id) {
        Optional<User> user = userService.findUserById(id);

        return user.map(value -> ResponseEntity.status(HttpStatus.OK).body(value)).orElseGet(() ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateUserResponse> updateUserFull(@PathVariable long id, @Valid @RequestBody UpdateUserFullRequest request) {
        UpdateUserResponse user = userService.updateUserFull(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UpdateUserResponse> updateUserPart(@PathVariable long id, @Valid @RequestBody UpdateUserPartRequest request) {
        UpdateUserResponse user = userService.updateUserPart(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable long id) {
        userService.deleteUserById(id);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

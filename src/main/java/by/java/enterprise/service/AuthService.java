package by.java.enterprise.service;

import by.java.enterprise.dto.request.LoginRequest;
import by.java.enterprise.dto.request.RegisterRequest;
import by.java.enterprise.dto.response.AuthResponse;
import by.java.enterprise.dto.response.UserResponse;
import by.java.enterprise.exception.DuplicateUsernameException;
import by.java.enterprise.exception.InvalidCredentialsException;
import by.java.enterprise.kafka.producer.UserEventProducer;
import by.java.enterprise.model.User;
import by.java.enterprise.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserEventProducer userEventProducer;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       UserEventProducer userEventProducer
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userEventProducer = userEventProducer;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new DuplicateUsernameException("User with username {" + request.username() + "} already exists");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setDisplayName(request.displayName());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setEmailVerified(false);
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        userEventProducer.userRegistered(saved);

        return new UserResponse(saved.getId(), saved.getEmail(), saved.getUsername(), saved.getDisplayName(), saved.getCreatedAt());
    }

    public AuthResponse login(LoginRequest request) {
        User foundUser = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), foundUser.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String token = jwtService.createToken(foundUser);
        UserResponse response = new UserResponse(foundUser.getId(), foundUser.getUsername(), foundUser.getDisplayName(), foundUser.getCreatedAt());

        return new AuthResponse(token, response);
    }
}

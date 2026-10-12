package by.java.enterprise.controller;

import by.java.enterprise.dto.request.ConfirmEmailRequest;
import by.java.enterprise.dto.request.LoginRequest;
import by.java.enterprise.dto.request.RegisterRequest;
import by.java.enterprise.dto.response.AuthResponse;
import by.java.enterprise.dto.response.UserResponse;
import by.java.enterprise.exception.CodeExpiredException;
import by.java.enterprise.exception.CodeNotFoundException;
import by.java.enterprise.exception.EmailNotFoundException;
import by.java.enterprise.exception.InvalidCodeException;
import by.java.enterprise.service.AuthService;
import by.java.enterprise.service.EmailConfirmationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailConfirmationService emailConfirmationService;

    public AuthController(AuthService authService, EmailConfirmationService emailConfirmationService) {
        this.authService = authService;
        this.emailConfirmationService = emailConfirmationService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<Void> confirmEmailPost(@Valid @RequestBody ConfirmEmailRequest request) {
        emailConfirmationService.confirm(request.email(), request.code());
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/confirm-email", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> confirmEmailGet(
            @RequestParam String email,
            @RequestParam String code
    ) {
        try {
            emailConfirmationService.confirm(email, code);
            return ResponseEntity.ok("Почта " + email + " подтверждена");
        } catch (EmailNotFoundException | CodeNotFoundException
                 | InvalidCodeException | CodeExpiredException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend-confirmation-code")
    public ResponseEntity<Void> resend(@Valid @RequestBody ConfirmEmailRequest request) {
        emailConfirmationService.resend(request.email());
        return ResponseEntity.ok().build();
    }
}

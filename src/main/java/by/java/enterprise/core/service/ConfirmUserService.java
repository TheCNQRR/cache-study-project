package by.java.enterprise.core.service;

import by.java.enterprise.core.domain.User;
import by.java.enterprise.core.port.in.ConfirmUserUseCase;
import by.java.enterprise.core.port.out.NotificationPort;
import by.java.enterprise.core.port.out.UserRepositoryPort;
import by.java.enterprise.exception.CodeExpiredException;
import by.java.enterprise.exception.CodeNotFoundException;
import by.java.enterprise.exception.EmailNotFoundException;
import by.java.enterprise.exception.InvalidCodeException;
import by.java.enterprise.model.EmailCode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

public class ConfirmUserService implements ConfirmUserUseCase {
    private final UserRepositoryPort userRepository;
    private final NotificationPort notificationPort;

    public ConfirmUserService(UserRepositoryPort userRepository, NotificationPort notificationPort) {
        this.userRepository = userRepository;
        this.notificationPort = notificationPort;
    }

    @Override
    public void confirm(String email, String rawCode) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("Пользователь не найден"));

        EmailCode code = userRepository.findActiveCodeByUserId(user.getId())
                .orElseThrow(() -> new CodeNotFoundException("Код не найден"));

        String submittedHash = sha256(rawCode);
        if (!submittedHash.equals(code.getCodeHash())) {
            throw new InvalidCodeException("Неверный код");
        }

        if (LocalDateTime.now().isAfter(code.getExpiresAt())) {
            throw new CodeExpiredException("Код истёк, запросите новый");
        }

        user.setEmailVerified(true);
        code.setConfirmed(true);

        userRepository.saveUser(user);
        userRepository.saveCode(code);

        notificationPort.sendConfirmationSuccess(email);
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}

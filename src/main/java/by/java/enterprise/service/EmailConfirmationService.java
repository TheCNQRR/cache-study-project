package by.java.enterprise.service;

import by.java.enterprise.exception.CodeExpiredException;
import by.java.enterprise.exception.CodeNotFoundException;
import by.java.enterprise.exception.EmailNotFoundException;
import by.java.enterprise.exception.InvalidCodeException;
import by.java.enterprise.model.EmailCode;
import by.java.enterprise.model.User;
import by.java.enterprise.notification.ChannelType;
import by.java.enterprise.notification.NotificationMessage;
import by.java.enterprise.notification.NotificationSender;
import by.java.enterprise.repository.EmailCodeRepository;
import by.java.enterprise.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class EmailConfirmationService {

    private final UserRepository userRepository;
    private final EmailCodeRepository emailCodeRepository;
    private final NotificationSender notificationSender;
    private final SecureRandom secureRandom = new SecureRandom();

    public EmailConfirmationService(
            UserRepository userRepository,
            EmailCodeRepository emailCodeRepository,
            NotificationSender notificationSender
    ) {
        this.userRepository = userRepository;
        this.emailCodeRepository = emailCodeRepository;
        this.notificationSender = notificationSender;
    }

    @Transactional
    public void confirm(String email, String rawCode) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("Пользователь не найден"));

        EmailCode code = emailCodeRepository
                .findTopByUserIdAndConfirmedFalseOrderByExpiresAtDesc(user.getId())
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
    }

    @Transactional
    public void resend(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("Пользователь не найден"));

        if (user.getEmailVerified()) {
            throw new InvalidCodeException("Почта уже подтверждена");
        }

        int code = secureRandom.nextInt(100000, 1000000);
        String hash = sha256(String.valueOf(code));

        EmailCode newCode = new EmailCode();
        newCode.setUserId(user.getId());
        newCode.setCodeHash(hash);
        newCode.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        newCode.setConfirmed(false);
        emailCodeRepository.save(newCode);

        String body = """
            Здравствуйте, %s!

            Ваш новый код подтверждения: %d

            Подтвердите адрес, перейдя по ссылке:
            http://localhost:8080/auth/confirm-email?email=%s&code=%d

            Код действует 15 минут.
            """.formatted(user.getUsername(), code, email, code);

        notificationSender.send(ChannelType.EMAIL,
                new NotificationMessage(email, "Код подтверждения", body));
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

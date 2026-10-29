package by.java.enterprise.service;

import by.java.enterprise.event.UserConfirmedEvent;
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
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher publisher;

    private final Counter okCounter;
    private final Counter failCounter;
    private final Timer confirmTimer;

    public EmailConfirmationService(
            UserRepository userRepository,
            EmailCodeRepository emailCodeRepository,
            NotificationSender notificationSender,
            ApplicationEventPublisher publisher,
            MeterRegistry meterRegistry
    ) {
        this.userRepository = userRepository;
        this.emailCodeRepository = emailCodeRepository;
        this.notificationSender = notificationSender;
        this.publisher = publisher;

        this.okCounter = Counter.builder("confirm.ok")
                .description("Успешные подтверждения почты")
                .register(meterRegistry);

        this.failCounter = Counter.builder("confirm.fail")
                .description("Неудачные попытки подтверждения")
                .register(meterRegistry);

        this.confirmTimer = Timer.builder("confirm.time")
                .description("Время выполнения подтверждения")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
    }

    @Transactional
    public void confirm(String email, String rawCode) {
        Timer.Sample sample = Timer.start();

        try {
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

            publisher.publishEvent(new UserConfirmedEvent(user.getId()));

            okCounter.increment();
        } catch (Exception e) {
            failCounter.increment();
            throw e;
        } finally {
            sample.stop(confirmTimer);
        }
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
            http://localhost:8080/api/v1/auth/confirm-email?email=%s&code=%d

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

import by.java.enterprise.exception.CodeExpiredException;
import by.java.enterprise.exception.CodeNotFoundException;
import by.java.enterprise.exception.InvalidCodeException;
import by.java.enterprise.model.EmailCode;
import by.java.enterprise.model.User;
import by.java.enterprise.notification.NotificationSender;
import by.java.enterprise.repository.EmailCodeRepository;
import by.java.enterprise.repository.UserRepository;
import by.java.enterprise.service.EmailConfirmationService;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailConfirmationServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    EmailCodeRepository emailCodeRepository;

    @Mock
    NotificationSender notificationSender;

    @Mock
    ApplicationEventPublisher eventPublisher;

    EmailConfirmationService service;

    User user;
    EmailCode code;

    private static final String VALID_HASH = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92";

    @BeforeEach
    void setUp() {
        service = new EmailConfirmationService(
                userRepository,
                emailCodeRepository,
                notificationSender,
                eventPublisher,
                new SimpleMeterRegistry()
        );

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setEmailVerified(false);

        code = new EmailCode();
        code.setUserId(1L);
        code.setCodeHash(VALID_HASH);
        code.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        code.setConfirmed(false);
    }

    @Test
    void confirmValidCodeMarksUserVerifiedAndCodeConfirmed() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(emailCodeRepository.findTopByUserIdAndConfirmedFalseOrderByExpiresAtDesc(1L))
                .thenReturn(Optional.of(code));

        service.confirm("test@example.com", "123456");

        assertTrue(user.getEmailVerified(), "User должен стать verified");
        assertTrue(code.isConfirmed(), "EmailCode должен стать confirmed");
    }

    @Test
    void confirmInvalidCodeThrowsExceptionAndDoesNotChangeState() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(emailCodeRepository.findTopByUserIdAndConfirmedFalseOrderByExpiresAtDesc(1L))
                .thenReturn(Optional.of(code));

        assertThrows(InvalidCodeException.class,
                () -> service.confirm("test@example.com", "000000"));

        assertFalse(user.getEmailVerified(), "User не должен стать verified");
        assertFalse(code.isConfirmed(), "EmailCode не должен стать confirmed");
    }

    @Test
    void confirmExpiredCodeThrowsException() {
        code.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(emailCodeRepository.findTopByUserIdAndConfirmedFalseOrderByExpiresAtDesc(1L))
                .thenReturn(Optional.of(code));

        assertThrows(CodeExpiredException.class,
                () -> service.confirm("test@example.com", "123456"));

        assertFalse(user.getEmailVerified());
        assertFalse(code.isConfirmed());
    }

    @Test
    void confirmRepeatedCallThrowsCodeNotFound() {

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(emailCodeRepository.findTopByUserIdAndConfirmedFalseOrderByExpiresAtDesc(1L))
                .thenReturn(Optional.empty());

        assertThrows(CodeNotFoundException.class,
                () -> service.confirm("test@example.com", "123456"));
    }
}

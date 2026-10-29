package by.java.enterprise.event;

import by.java.enterprise.exception.UserNotFoundException;
import by.java.enterprise.model.User;
import by.java.enterprise.notification.ChannelType;
import by.java.enterprise.notification.NotificationMessage;
import by.java.enterprise.notification.NotificationSender;
import by.java.enterprise.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@Component
public class UserConfirmedListener {

    private final UserRepository userRepository;
    private final NotificationSender notificationSender;

    public UserConfirmedListener(UserRepository userRepository, NotificationSender notificationSender) {
        this.userRepository = userRepository;
        this.notificationSender = notificationSender;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onConfirmed(UserConfirmedEvent event) {
        Optional<User> userResult = userRepository.findById(event.userId());

        if (userResult.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        User user = userResult.get();

        NotificationMessage msg = new NotificationMessage(
                user.getEmail(),
                "Почта подтверждена",
                "Ваша почта успешно подтверждена!"
        );
        notificationSender.send(ChannelType.EMAIL, msg);
    }
}

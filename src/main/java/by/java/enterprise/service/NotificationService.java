package by.java.enterprise.service;

import by.java.enterprise.model.EmailCode;
import by.java.enterprise.model.Notification;
import by.java.enterprise.repository.EmailCodeRepository;
import by.java.enterprise.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailCodeRepository emailCodeRepository;

    public NotificationService(NotificationRepository notificationRepository, EmailCodeRepository emailCodeRepository) {
        this.notificationRepository = notificationRepository;
        this.emailCodeRepository = emailCodeRepository;
    }

    @Transactional
    public void saveCodeAndNotification(Long userId, String codeHash) {
        Notification notification = new Notification();
        notification.setEventId(userId);
        notificationRepository.save(notification);

        EmailCode emailCode = new EmailCode();
        emailCode.setUserId(userId);
        emailCode.setCodeHash(codeHash);
        emailCode.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        emailCode.setConfirmed(false);
        emailCodeRepository.save(emailCode);
    }
}

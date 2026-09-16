package by.java.enterprise.service;

import by.java.enterprise.interfaces.NotificationService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class SmsNotificationService implements NotificationService {
}

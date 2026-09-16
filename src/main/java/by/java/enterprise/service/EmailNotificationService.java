package by.java.enterprise.service;

import by.java.enterprise.interfaces.NotificationService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class EmailNotificationService implements NotificationService {
}

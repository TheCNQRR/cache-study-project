package by.java.enterprise.repository;

import by.java.enterprise.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    boolean existsByEventId(Long eventId);
}

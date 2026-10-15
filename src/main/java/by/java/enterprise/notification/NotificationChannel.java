package by.java.enterprise.notification;

public interface NotificationChannel {
    ChannelType type();
    void send(NotificationMessage message);
}

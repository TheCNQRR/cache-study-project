package by.java.enterprise.notification;

import java.util.concurrent.CompletableFuture;

public interface NotificationChannel {
    ChannelType type();
    CompletableFuture<Void> send(NotificationMessage message);
}

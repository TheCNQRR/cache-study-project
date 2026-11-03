package by.java.enterprise.core.port.out;

import java.util.concurrent.CompletableFuture;

public interface NotificationPort {
    CompletableFuture<Void> sendConfirmationSuccess(String email);
}

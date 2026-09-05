package by.java.enterprise.exception;

public class MessageAlreadyViewed extends RuntimeException {
    public MessageAlreadyViewed(String message) {
        super(message);
    }
}

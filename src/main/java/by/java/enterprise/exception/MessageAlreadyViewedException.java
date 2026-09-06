package by.java.enterprise.exception;

public class MessageAlreadyViewedException extends RuntimeException {
    public MessageAlreadyViewedException(String message) {
        super(message);
    }
}

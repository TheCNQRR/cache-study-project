package by.java.enterprise.core.port.in;

public interface ConfirmUserUseCase {
    void confirm(String email, String code);
}

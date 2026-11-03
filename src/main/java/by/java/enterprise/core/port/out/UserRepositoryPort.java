package by.java.enterprise.core.port.out;

import by.java.enterprise.core.domain.User;
import by.java.enterprise.model.EmailCode;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByEmail(String email);
    Optional<EmailCode> findActiveCodeByUserId(Long userId);
    void saveUser(User user);
    void saveCode(EmailCode code);
}

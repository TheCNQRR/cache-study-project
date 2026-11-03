package by.java.enterprise.infrastructure.persistence;

import by.java.enterprise.core.domain.User;
import by.java.enterprise.core.port.out.UserRepositoryPort;
import by.java.enterprise.model.EmailCode;
import by.java.enterprise.repository.EmailCodeRepository;
import by.java.enterprise.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userJpaRepository;
    private final EmailCodeRepository emailCodeJpaRepository;

    public UserRepositoryAdapter(UserRepository userJpaRepository, EmailCodeRepository emailCodeJpaRepository) {
        this.userJpaRepository = userJpaRepository;
        this.emailCodeJpaRepository = emailCodeJpaRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(this::toDomainUser);
    }

    @Override
    public Optional<EmailCode> findActiveCodeByUserId(Long userId) {
        return emailCodeJpaRepository.findTopByUserIdAndConfirmedFalseOrderByExpiresAtDesc(userId)
                .map(this::toDomainCode);
    }

    @Override
    public void saveUser(User user) {
        by.java.enterprise.model.User entity = userJpaRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found for save"));

        entity.setEmailVerified(user.getEmailVerified());
        entity.setEmail(user.getEmail());
        entity.setUsername(user.getUsername());
        entity.setDisplayName(user.getDisplayName());

        userJpaRepository.save(entity);
    }

    @Override
    public void saveCode(EmailCode code) {
        by.java.enterprise.model.EmailCode entity = emailCodeJpaRepository.findById(code.getId())
                .orElseThrow(() -> new RuntimeException("Code not found for save"));

        entity.setConfirmed(code.isConfirmed());
        emailCodeJpaRepository.save(entity);
    }

    private User toDomainUser(by.java.enterprise.model.User entity) {
        User domain = new User();
        domain.setId(entity.getId());
        domain.setEmail(entity.getEmail());
        domain.setUsername(entity.getUsername());
        domain.setDisplayName(entity.getDisplayName());
        domain.setPasswordHash(entity.getPasswordHash());
        domain.setEmailVerified(entity.getEmailVerified());
        domain.setCreatedAt(entity.getCreatedAt());
        return domain;
    }

    private EmailCode toDomainCode(by.java.enterprise.model.EmailCode entity) {
        EmailCode domain = new EmailCode();
        domain.setId(entity.getId());
        domain.setUserId(entity.getUserId());
        domain.setCodeHash(entity.getCodeHash());
        domain.setExpiresAt(entity.getExpiresAt());
        domain.setConfirmed(entity.isConfirmed());
        return domain;
    }
}

package by.java.enterprise.repository;

import by.java.enterprise.model.EmailCode;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailCodeRepository extends JpaRepository<EmailCode, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EmailCode> findTopByUserIdAndConfirmedFalseOrderByExpiresAtDesc(Long userId);
}

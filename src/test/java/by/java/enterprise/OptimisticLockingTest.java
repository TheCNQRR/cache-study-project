package by.java.enterprise;

import by.java.enterprise.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class OptimisticLockingTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testOptimisticLockingException() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPasswordHash("dummy_hash");
        em.persist(user);
        em.flush();
        em.clear();

        User detachedUser = em.find(User.class, user.getId());
        em.clear();

        jdbcTemplate.update("UPDATE users SET version = version + 1 WHERE id = ?", user.getId());

        detachedUser.setDisplayName("New Name");

        assertThrows(OptimisticLockException.class, () -> {
            em.merge(detachedUser);
            em.flush();
        });
    }
}
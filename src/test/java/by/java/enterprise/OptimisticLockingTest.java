package by.java.enterprise;

import by.java.enterprise.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class OptimisticLockingTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testOptimisticLockingException() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPasswordHash("dummy_hash");
        entityManager.persist(user);
        entityManager.flush();

        entityManager.clear();

        User user1 = entityManager.find(User.class, user.getId());
        entityManager.clear();

        User user2 = entityManager.find(User.class, user.getId());

        user1.setDisplayName("New Name 1");
        entityManager.merge(user1);
        entityManager.flush();

        user2.setDisplayName("New Name 2");

        assertThrows(OptimisticLockException.class, () -> {
            entityManager.merge(user2);
            entityManager.flush();
        });
    }
}
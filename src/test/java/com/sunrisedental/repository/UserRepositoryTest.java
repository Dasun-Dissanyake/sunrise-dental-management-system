package com.sunrisedental.repository;

import com.sunrisedental.model.Role;
import com.sunrisedental.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findByUsername returns user when username exists")
    void testFindByUsername_Success() {
        User user = new User("admin_test", "$2a$10$hashedpassword", "Test Admin", Role.ADMIN, true);
        entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findByUsername("admin_test");

        assertTrue(found.isPresent());
        assertEquals("admin_test", found.get().getUsername());
        assertEquals(Role.ADMIN, found.get().getRole());
    }

    @Test
    @DisplayName("findByUsername returns empty optional when username does not exist")
    void testFindByUsername_NotFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("existsByUsername returns true when user exists and false when not")
    void testExistsByUsername() {
        User user = new User("reception_test", "$2a$10$hashedpassword", "Reception Staff", Role.RECEPTIONIST, true);
        entityManager.persistAndFlush(user);

        assertTrue(userRepository.existsByUsername("reception_test"));
        assertFalse(userRepository.existsByUsername("unknown_user"));
    }
}

package br.com.infnet.edur.usermanagement.repository;

import br.com.infnet.edur.usermanagement.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+1234567890")
                .build();
    }

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUserSuccessfully() {
        User savedUser = userRepository.save(testUser);

        assertNotNull(savedUser.getId());
        assertEquals(testUser.getFirstName(), savedUser.getFirstName());
        assertEquals(testUser.getLastName(), savedUser.getLastName());
        assertEquals(testUser.getEmail(), savedUser.getEmail());
        assertEquals(testUser.getPhoneNumber(), savedUser.getPhoneNumber());
    }

    @Test
    @DisplayName("Should find user by id")
    void shouldFindUserById() {
        User savedUser = entityManager.persistAndFlush(testUser);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals(savedUser.getEmail(), foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty when user not found by id")
    void shouldReturnEmptyWhenUserNotFoundById() {
        Optional<User> foundUser = userRepository.findById(999L);

        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should find all users")
    void shouldFindAllUsers() {
        User user1 = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phoneNumber("+1111111111")
                .build();
                
        User user2 = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .phoneNumber("+2222222222")
                .build();

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());
    }

    @Test
    @DisplayName("Should return true when user exists by email")
    void shouldReturnTrueWhenUserExistsByEmail() {
        entityManager.persistAndFlush(testUser);

        boolean exists = userRepository.existsByEmail(testUser.getEmail());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when user does not exist by email")
    void shouldReturnFalseWhenUserDoesNotExistByEmail() {
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should return true when user exists by phone number")
    void shouldReturnTrueWhenUserExistsByPhoneNumber() {
        entityManager.persistAndFlush(testUser);

        boolean exists = userRepository.existsByPhoneNumber(testUser.getPhoneNumber());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when user does not exist by phone number")
    void shouldReturnFalseWhenUserDoesNotExistByPhoneNumber() {
        boolean exists = userRepository.existsByPhoneNumber("+9999999999");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        User savedUser = entityManager.persistAndFlush(testUser);
        Long userId = savedUser.getId();

        userRepository.delete(savedUser);
        entityManager.flush();

        Optional<User> deletedUser = userRepository.findById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        User savedUser = entityManager.persistAndFlush(testUser);
        
        User updatedUser = User.builder()
                .id(savedUser.getId())
                .firstName("Updated")
                .lastName("Name")
                .email("updated@example.com")
                .phoneNumber("+9999999999")
                .build();

        User result = userRepository.save(updatedUser);

        assertEquals("Updated", result.getFirstName());
        assertEquals("Name", result.getLastName());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("+9999999999", result.getPhoneNumber());
    }

    @Test
    @DisplayName("Should handle case sensitivity in email existence check")
    void shouldHandleCaseSensitivityInEmailExistenceCheck() {
        entityManager.persistAndFlush(testUser);

        boolean existsLowerCase = userRepository.existsByEmail(testUser.getEmail().toLowerCase());
        boolean existsUpperCase = userRepository.existsByEmail(testUser.getEmail().toUpperCase());

        assertTrue(existsLowerCase);
        assertFalse(existsUpperCase);
    }
}
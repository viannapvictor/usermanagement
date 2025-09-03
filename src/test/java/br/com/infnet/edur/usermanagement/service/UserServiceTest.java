package br.com.infnet.edur.usermanagement.service;

import br.com.infnet.edur.usermanagement.model.User;
import br.com.infnet.edur.usermanagement.repository.UserRepository;
import br.com.infnet.edur.usermanagement.utils.exceptions.UserAlreadyExistsException;
import br.com.infnet.edur.usermanagement.utils.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+1234567890")
                .build();
    }

    @Test
    @DisplayName("Should get all users successfully")
    void shouldGetAllUsersSuccessfully() {
        User user1 = User.builder().id(1L).firstName("John").lastName("Doe").email("john@example.com").phoneNumber("+1111111111").build();
        User user2 = User.builder().id(2L).firstName("Jane").lastName("Smith").email("jane@example.com").phoneNumber("+2222222222").build();
        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should get user by id successfully")
    void shouldGetUserByIdSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(1L);

        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found by id")
    void shouldThrowUserNotFoundExceptionWhenUserNotFoundById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        User newUser = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phoneNumber("+9876543210")
                .build();

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User result = userService.createUser(newUser);

        assertEquals(newUser.getEmail(), result.getEmail());
        verify(userRepository).existsByEmail(newUser.getEmail());
        verify(userRepository).existsByPhoneNumber(newUser.getPhoneNumber());
        verify(userRepository).save(newUser);
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email exists")
    void shouldThrowUserAlreadyExistsExceptionWhenEmailExists() {
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(testUser));
        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when phone number exists")
    void shouldThrowUserAlreadyExistsExceptionWhenPhoneNumberExists() {
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(testUser.getPhoneNumber())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(testUser));
        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository).existsByPhoneNumber(testUser.getPhoneNumber());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        User existingUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("old@example.com")
                .phoneNumber("+1111111111")
                .build();

        User updatedUserData = User.builder()
                .firstName("John Updated")
                .lastName("Doe Updated")
                .email("new@example.com")
                .phoneNumber("+9999999999")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByPhoneNumber("+9999999999")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUserData);

        User result = userService.updateUser(1L, updatedUserData);

        assertEquals("new@example.com", result.getEmail());
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when updating with existing email")
    void shouldThrowUserAlreadyExistsExceptionWhenUpdatingWithExistingEmail() {
        User existingUser = User.builder()
                .id(1L)
                .email("original@example.com")
                .phoneNumber("+1111111111")
                .build();

        User updatedUserData = User.builder()
                .email("existing@example.com")
                .phoneNumber("+1111111111")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(1L, updatedUserData));
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(testUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when deleting non-existent user")
    void shouldThrowUserNotFoundExceptionWhenDeletingNonExistentUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(999L));
        verify(userRepository).findById(999L);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("Should return true when user exists by email")
    void shouldReturnTrueWhenUserExistsByEmail() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = userService.existsByEmail("test@example.com");

        assertTrue(result);
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should return false when user does not exist by email")
    void shouldReturnFalseWhenUserDoesNotExistByEmail() {
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        boolean result = userService.existsByEmail("nonexistent@example.com");

        assertFalse(result);
        verify(userRepository).existsByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Should return true when user exists by phone number")
    void shouldReturnTrueWhenUserExistsByPhoneNumber() {
        when(userRepository.existsByPhoneNumber("+1234567890")).thenReturn(true);

        boolean result = userService.existsByPhoneNumber("+1234567890");

        assertTrue(result);
        verify(userRepository).existsByPhoneNumber("+1234567890");
    }

    @Test
    @DisplayName("Should return false when user does not exist by phone number")
    void shouldReturnFalseWhenUserDoesNotExistByPhoneNumber() {
        when(userRepository.existsByPhoneNumber("+9999999999")).thenReturn(false);

        boolean result = userService.existsByPhoneNumber("+9999999999");

        assertFalse(result);
        verify(userRepository).existsByPhoneNumber("+9999999999");
    }

    @Test
    @DisplayName("Should allow updating user with same email")
    void shouldAllowUpdatingUserWithSameEmail() {
        User existingUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("same@example.com")
                .phoneNumber("+1111111111")
                .build();

        User updatedUserData = User.builder()
                .firstName("John Updated")
                .lastName("Doe Updated")
                .email("same@example.com")
                .phoneNumber("+1111111111")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUserData);

        User result = userService.updateUser(1L, updatedUserData);

        assertEquals("John Updated", result.getFirstName());
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
        verify(userRepository, never()).existsByEmail(anyString());
    }
}
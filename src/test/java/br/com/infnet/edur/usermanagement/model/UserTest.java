package br.com.infnet.edur.usermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Model Tests")
class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create user successfully with valid data")
    void shouldCreateUserSuccessfullyWithValidData() {
        User user = new User("John", "Doe", "john.doe@example.com", "+1234567890");

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("+1234567890", user.getPhoneNumber());
    }

    @Test
    @DisplayName("Should create user using builder pattern")
    void shouldCreateUserUsingBuilderPattern() {
        User user = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phoneNumber("+9876543210")
                .build();

        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("jane.smith@example.com", user.getEmail());
        assertEquals("+9876543210", user.getPhoneNumber());
    }

    @Test
    @DisplayName("Should fail validation when first name is blank")
    void shouldFailValidationWhenFirstNameIsBlank() {
        User user = User.builder()
                .firstName("")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+1234567890")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("First name is required")));
    }

    @Test
    @DisplayName("Should fail validation when last name is blank")
    void shouldFailValidationWhenLastNameIsBlank() {
        User user = User.builder()
                .firstName("John")
                .lastName("")
                .email("john.doe@example.com")
                .phoneNumber("+1234567890")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Last name is required")));
    }

    @Test
    @DisplayName("Should fail validation when email is blank")
    void shouldFailValidationWhenEmailIsBlank() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("")
                .phoneNumber("+1234567890")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
    }

    @Test
    @DisplayName("Should fail validation when email is invalid")
    void shouldFailValidationWhenEmailIsInvalid() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("invalid-email")
                .phoneNumber("+1234567890")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email should be valid")));
    }

    @Test
    @DisplayName("Should fail validation when phone number is blank")
    void shouldFailValidationWhenPhoneNumberIsBlank() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Phone number is required")));
    }

    @Test
    @DisplayName("Should fail validation when phone number is invalid")
    void shouldFailValidationWhenPhoneNumberIsInvalid() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("invalid-phone")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Phone number should be valid")));
    }

    @Test
    @DisplayName("Should pass validation with valid phone number formats")
    void shouldPassValidationWithValidPhoneNumberFormats() {
        String[] validPhones = {"+1234567890", "1234567890", "+123456789012345"};
        
        for (String phone : validPhones) {
            User user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.doe@example.com")
                    .phoneNumber(phone)
                    .build();

            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertTrue(violations.isEmpty(), "Phone number " + phone + " should be valid");
        }
    }
}
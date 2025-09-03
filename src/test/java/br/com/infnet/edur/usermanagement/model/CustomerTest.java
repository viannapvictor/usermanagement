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

@DisplayName("Customer Model Tests")
class CustomerTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create customer successfully with valid data")
    void shouldCreateCustomerSuccessfullyWithValidData() {
        Customer customer = new Customer("John Smith", "john.smith@example.com", "+1234567890");

        assertEquals("John Smith", customer.getName());
        assertEquals("john.smith@example.com", customer.getEmail());
        assertEquals("+1234567890", customer.getPhoneNumber());
    }

    @Test
    @DisplayName("Should create customer using builder pattern")
    void shouldCreateCustomerUsingBuilderPattern() {
        Customer customer = Customer.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .phoneNumber("+9876543210")
                .build();

        assertEquals("Jane Doe", customer.getName());
        assertEquals("jane.doe@example.com", customer.getEmail());
        assertEquals("+9876543210", customer.getPhoneNumber());
    }

    @Test
    @DisplayName("Should fail validation when name is blank")
    void shouldFailValidationWhenNameIsBlank() {
        Customer customer = Customer.builder()
                .name("")
                .email("test@example.com")
                .phoneNumber("+1234567890")
                .build();

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name is required")));
    }

    @Test
    @DisplayName("Should fail validation when email is blank")
    void shouldFailValidationWhenEmailIsBlank() {
        Customer customer = Customer.builder()
                .name("John Smith")
                .email("")
                .phoneNumber("+1234567890")
                .build();

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
    }

    @Test
    @DisplayName("Should fail validation when email is invalid")
    void shouldFailValidationWhenEmailIsInvalid() {
        Customer customer = Customer.builder()
                .name("John Smith")
                .email("invalid-email")
                .phoneNumber("+1234567890")
                .build();

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email should be valid")));
    }

    @Test
    @DisplayName("Should fail validation when phone number is blank")
    void shouldFailValidationWhenPhoneNumberIsBlank() {
        Customer customer = Customer.builder()
                .name("John Smith")
                .email("john.smith@example.com")
                .phoneNumber("")
                .build();

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Phone number is required")));
    }

    @Test
    @DisplayName("Should fail validation when phone number is invalid")
    void shouldFailValidationWhenPhoneNumberIsInvalid() {
        Customer customer = Customer.builder()
                .name("John Smith")
                .email("john.smith@example.com")
                .phoneNumber("invalid-phone")
                .build();

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Phone number should be valid")));
    }

    @Test
    @DisplayName("Should pass validation with valid phone number formats")
    void shouldPassValidationWithValidPhoneNumberFormats() {
        String[] validPhones = {"+1234567890", "1234567890", "+123456789012345"};
        
        for (String phone : validPhones) {
            Customer customer = Customer.builder()
                    .name("John Smith")
                    .email("john.smith@example.com")
                    .phoneNumber(phone)
                    .build();

            Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
            assertTrue(violations.isEmpty(), "Phone number " + phone + " should be valid");
        }
    }

    @Test
    @DisplayName("Should pass validation with valid email formats")
    void shouldPassValidationWithValidEmailFormats() {
        String[] validEmails = {
            "test@example.com", 
            "user.name@domain.co.uk", 
            "user+tag@example.org",
            "test123@test-domain.com"
        };
        
        for (String email : validEmails) {
            Customer customer = Customer.builder()
                    .name("John Smith")
                    .email(email)
                    .phoneNumber("+1234567890")
                    .build();

            Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
            assertTrue(violations.isEmpty(), "Email " + email + " should be valid");
        }
    }
}
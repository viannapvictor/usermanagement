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

@DisplayName("Supplier Model Tests")
class SupplierTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create supplier successfully with valid data")
    void shouldCreateSupplierSuccessfullyWithValidData() {
        Supplier supplier = new Supplier("Tech Corp", "contact@techcorp.com", "+1234567890");

        assertEquals("Tech Corp", supplier.getName());
        assertEquals("contact@techcorp.com", supplier.getEmail());
        assertEquals("+1234567890", supplier.getPhoneNumber());
    }

    @Test
    @DisplayName("Should create supplier using builder pattern")
    void shouldCreateSupplierUsingBuilderPattern() {
        Supplier supplier = Supplier.builder()
                .name("Supply Solutions")
                .email("info@supplysolutions.com")
                .phoneNumber("+9876543210")
                .build();

        assertEquals("Supply Solutions", supplier.getName());
        assertEquals("info@supplysolutions.com", supplier.getEmail());
        assertEquals("+9876543210", supplier.getPhoneNumber());
    }

    @Test
    @DisplayName("Should fail validation when name is blank")
    void shouldFailValidationWhenNameIsBlank() {
        Supplier supplier = Supplier.builder()
                .name("")
                .email("test@example.com")
                .phoneNumber("+1234567890")
                .build();

        Set<ConstraintViolation<Supplier>> violations = validator.validate(supplier);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name is required")));
    }

    @Test
    @DisplayName("Should fail validation when email is blank")
    void shouldFailValidationWhenEmailIsBlank() {
        Supplier supplier = Supplier.builder()
                .name("Tech Corp")
                .email("")
                .phoneNumber("+1234567890")
                .build();

        Set<ConstraintViolation<Supplier>> violations = validator.validate(supplier);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
    }

    @Test
    @DisplayName("Should fail validation when email is invalid")
    void shouldFailValidationWhenEmailIsInvalid() {
        Supplier supplier = Supplier.builder()
                .name("Tech Corp")
                .email("invalid-email")
                .phoneNumber("+1234567890")
                .build();

        Set<ConstraintViolation<Supplier>> violations = validator.validate(supplier);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email should be valid")));
    }

    @Test
    @DisplayName("Should fail validation when phone number is blank")
    void shouldFailValidationWhenPhoneNumberIsBlank() {
        Supplier supplier = Supplier.builder()
                .name("Tech Corp")
                .email("contact@techcorp.com")
                .phoneNumber("")
                .build();

        Set<ConstraintViolation<Supplier>> violations = validator.validate(supplier);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Phone number is required")));
    }

    @Test
    @DisplayName("Should fail validation when phone number is invalid")
    void shouldFailValidationWhenPhoneNumberIsInvalid() {
        Supplier supplier = Supplier.builder()
                .name("Tech Corp")
                .email("contact@techcorp.com")
                .phoneNumber("invalid-phone")
                .build();

        Set<ConstraintViolation<Supplier>> violations = validator.validate(supplier);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Phone number should be valid")));
    }

    @Test
    @DisplayName("Should pass validation with valid phone number formats")
    void shouldPassValidationWithValidPhoneNumberFormats() {
        String[] validPhones = {"+1234567890", "1234567890", "+123456789012345"};
        
        for (String phone : validPhones) {
            Supplier supplier = Supplier.builder()
                    .name("Tech Corp")
                    .email("contact@techcorp.com")
                    .phoneNumber(phone)
                    .build();

            Set<ConstraintViolation<Supplier>> violations = validator.validate(supplier);
            assertTrue(violations.isEmpty(), "Phone number " + phone + " should be valid");
        }
    }

    @Test
    @DisplayName("Should pass validation with valid email formats")
    void shouldPassValidationWithValidEmailFormats() {
        String[] validEmails = {
            "contact@techcorp.com", 
            "info@supplier-solutions.co.uk", 
            "support+orders@example.org",
            "sales123@test-domain.com"
        };
        
        for (String email : validEmails) {
            Supplier supplier = Supplier.builder()
                    .name("Tech Corp")
                    .email(email)
                    .phoneNumber("+1234567890")
                    .build();

            Set<ConstraintViolation<Supplier>> violations = validator.validate(supplier);
            assertTrue(violations.isEmpty(), "Email " + email + " should be valid");
        }
    }
}
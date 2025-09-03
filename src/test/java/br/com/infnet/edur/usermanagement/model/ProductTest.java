package br.com.infnet.edur.usermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Model Tests")
class ProductTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create product successfully with valid data")
    void shouldCreateProductSuccessfullyWithValidData() {
        BigDecimal price = new BigDecimal("19.99");
        Product product = new Product("Test Product", price);

        assertEquals("Test Product", product.getName());
        assertEquals(price, product.getUnitPrice());
    }

    @Test
    @DisplayName("Should create product using builder pattern")
    void shouldCreateProductUsingBuilderPattern() {
        BigDecimal price = new BigDecimal("29.99");
        Product product = Product.builder()
                .name("Builder Product")
                .unitPrice(price)
                .build();

        assertEquals("Builder Product", product.getName());
        assertEquals(price, product.getUnitPrice());
    }

    @Test
    @DisplayName("Should fail validation when name is blank")
    void shouldFailValidationWhenNameIsBlank() {
        Product product = Product.builder()
                .name("")
                .unitPrice(new BigDecimal("19.99"))
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Product name is required")));
    }

    @Test
    @DisplayName("Should fail validation when unit price is null")
    void shouldFailValidationWhenUnitPriceIsNull() {
        Product product = Product.builder()
                .name("Test Product")
                .unitPrice(null)
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Unit price is required")));
    }

    @Test
    @DisplayName("Should fail validation when unit price is zero")
    void shouldFailValidationWhenUnitPriceIsZero() {
        Product product = Product.builder()
                .name("Test Product")
                .unitPrice(BigDecimal.ZERO)
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Unit price must be greater than 0")));
    }

    @Test
    @DisplayName("Should fail validation when unit price is negative")
    void shouldFailValidationWhenUnitPriceIsNegative() {
        Product product = Product.builder()
                .name("Test Product")
                .unitPrice(new BigDecimal("-10.00"))
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Unit price must be greater than 0")));
    }

    @Test
    @DisplayName("Should pass validation with valid positive price")
    void shouldPassValidationWithValidPositivePrice() {
        Product product = Product.builder()
                .name("Valid Product")
                .unitPrice(new BigDecimal("0.01"))
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should pass validation with large price values")
    void shouldPassValidationWithLargePriceValues() {
        Product product = Product.builder()
                .name("Expensive Product")
                .unitPrice(new BigDecimal("9999999999.99"))
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertTrue(violations.isEmpty());
    }
}
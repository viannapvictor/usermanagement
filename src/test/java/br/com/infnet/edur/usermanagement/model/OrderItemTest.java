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

@DisplayName("OrderItem Model Tests")
class OrderItemTest {

    private Validator validator;
    private Product testProduct;
    private Customer testCustomer;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .unitPrice(new BigDecimal("10.00"))
                .build();
                
        testCustomer = Customer.builder()
                .id(1L)
                .name("Test Customer")
                .email("customer@test.com")
                .phoneNumber("+1234567890")
                .build();
                
        testOrder = Order.builder()
                .id(1L)
                .customer(testCustomer)
                .build();
    }

    @Test
    @DisplayName("Should create order item using builder pattern")
    void shouldCreateOrderItemUsingBuilderPattern() {
        BigDecimal unitPrice = new BigDecimal("15.00");
        OrderItem orderItem = OrderItem.builder()
                .product(testProduct)
                .quantity(3)
                .unitPrice(unitPrice)
                .order(testOrder)
                .build();

        assertEquals(testProduct, orderItem.getProduct());
        assertEquals(3, orderItem.getQuantity());
        assertEquals(unitPrice, orderItem.getUnitPrice());
        assertEquals(testOrder, orderItem.getOrder());
    }

    @Test
    @DisplayName("Should fail validation when product is null")
    void shouldFailValidationWhenProductIsNull() {
        OrderItem orderItem = OrderItem.builder()
                .product(null)
                .quantity(1)
                .unitPrice(new BigDecimal("10.00"))
                .order(testOrder)
                .build();

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Product is required")));
    }

    @Test
    @DisplayName("Should fail validation when quantity is null")
    void shouldFailValidationWhenQuantityIsNull() {
        OrderItem orderItem = OrderItem.builder()
                .product(testProduct)
                .quantity(null)
                .unitPrice(new BigDecimal("10.00"))
                .order(testOrder)
                .build();

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Quantity is required")));
    }

    @Test
    @DisplayName("Should fail validation when quantity is zero")
    void shouldFailValidationWhenQuantityIsZero() {
        OrderItem orderItem = OrderItem.builder()
                .product(testProduct)
                .quantity(0)
                .unitPrice(new BigDecimal("10.00"))
                .order(testOrder)
                .build();

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Quantity must be at least 1")));
    }

    @Test
    @DisplayName("Should fail validation when quantity is negative")
    void shouldFailValidationWhenQuantityIsNegative() {
        OrderItem orderItem = OrderItem.builder()
                .product(testProduct)
                .quantity(-1)
                .unitPrice(new BigDecimal("10.00"))
                .order(testOrder)
                .build();

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Quantity must be at least 1")));
    }

    @Test
    @DisplayName("Should fail validation when unit price is null")
    void shouldFailValidationWhenUnitPriceIsNull() {
        OrderItem orderItem = OrderItem.builder()
                .product(testProduct)
                .quantity(1)
                .unitPrice(null)
                .order(testOrder)
                .build();

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Unit price is required")));
    }

    @Test
    @DisplayName("Should calculate total price correctly")
    void shouldCalculateTotalPriceCorrectly() {
        OrderItem orderItem = OrderItem.builder()
                .product(testProduct)
                .quantity(3)
                .unitPrice(new BigDecimal("10.50"))
                .order(testOrder)
                .build();

        BigDecimal totalPrice = orderItem.getTotalPrice();
        assertEquals(new BigDecimal("31.50"), totalPrice);
    }

    @Test
    @DisplayName("Should calculate total price correctly with decimal quantity")
    void shouldCalculateTotalPriceCorrectlyWithSingleQuantity() {
        OrderItem orderItem = OrderItem.builder()
                .product(testProduct)
                .quantity(1)
                .unitPrice(new BigDecimal("19.99"))
                .order(testOrder)
                .build();

        BigDecimal totalPrice = orderItem.getTotalPrice();
        assertEquals(new BigDecimal("19.99"), totalPrice);
    }

    @Test
    @DisplayName("Should calculate total price correctly with large quantities")
    void shouldCalculateTotalPriceCorrectlyWithLargeQuantities() {
        OrderItem orderItem = OrderItem.builder()
                .product(testProduct)
                .quantity(100)
                .unitPrice(new BigDecimal("1.25"))
                .order(testOrder)
                .build();

        BigDecimal totalPrice = orderItem.getTotalPrice();
        assertEquals(new BigDecimal("125.00"), totalPrice);
    }

    @Test
    @DisplayName("Should handle zero unit price in calculation")
    void shouldHandleZeroUnitPriceInCalculation() {
        OrderItem orderItem = OrderItem.builder()
                .product(testProduct)
                .quantity(5)
                .unitPrice(BigDecimal.ZERO)
                .order(testOrder)
                .build();

        BigDecimal totalPrice = orderItem.getTotalPrice();
        assertEquals(BigDecimal.ZERO, totalPrice);
    }

    @Test
    @DisplayName("Should pass validation with minimum valid values")
    void shouldPassValidationWithMinimumValidValues() {
        OrderItem orderItem = OrderItem.builder()
                .product(testProduct)
                .quantity(1)
                .unitPrice(new BigDecimal("0.01"))
                .order(testOrder)
                .build();

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);
        assertTrue(violations.isEmpty());
        assertEquals(new BigDecimal("0.01"), orderItem.getTotalPrice());
    }

    @Test
    @DisplayName("Should handle setter methods correctly")
    void shouldHandleSetterMethodsCorrectly() {
        OrderItem orderItem = new OrderItem();
        
        orderItem.setProduct(testProduct);
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(new BigDecimal("15.00"));
        orderItem.setOrder(testOrder);

        assertEquals(testProduct, orderItem.getProduct());
        assertEquals(2, orderItem.getQuantity());
        assertEquals(new BigDecimal("15.00"), orderItem.getUnitPrice());
        assertEquals(testOrder, orderItem.getOrder());
        assertEquals(new BigDecimal("30.00"), orderItem.getTotalPrice());
    }
}
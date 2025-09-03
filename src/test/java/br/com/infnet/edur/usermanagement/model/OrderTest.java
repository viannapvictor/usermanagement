package br.com.infnet.edur.usermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Model Tests")
class OrderTest {

    private Validator validator;
    private Customer testCustomer;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        testCustomer = Customer.builder()
                .id(1L)
                .name("Test Customer")
                .email("customer@test.com")
                .phoneNumber("+1234567890")
                .build();
                
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .unitPrice(new BigDecimal("10.00"))
                .build();
    }

    @Test
    @DisplayName("Should create order successfully with valid customer")
    void shouldCreateOrderSuccessfullyWithValidCustomer() {
        Order order = new Order(testCustomer);

        assertEquals(testCustomer, order.getCustomer());
        assertNotNull(order.getOrderDate());
        assertNotNull(order.getOrderItems());
        assertTrue(order.getOrderItems().isEmpty());
    }

    @Test
    @DisplayName("Should create order using builder pattern")
    void shouldCreateOrderUsingBuilderPattern() {
        LocalDateTime orderDate = LocalDateTime.now();
        Order order = Order.builder()
                .customer(testCustomer)
                .orderDate(orderDate)
                .build();

        assertEquals(testCustomer, order.getCustomer());
        assertEquals(orderDate, order.getOrderDate());
        assertNotNull(order.getOrderItems());
    }

    @Test
    @DisplayName("Should fail validation when customer is null")
    void shouldFailValidationWhenCustomerIsNull() {
        Order order = Order.builder()
                .customer(null)
                .orderDate(LocalDateTime.now())
                .build();

        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Customer is required")));
    }

    @Test
    @DisplayName("Should calculate total amount correctly with no items")
    void shouldCalculateTotalAmountCorrectlyWithNoItems() {
        Order order = new Order(testCustomer);

        BigDecimal totalAmount = order.getTotalAmount();
        assertEquals(BigDecimal.ZERO, totalAmount);
    }

    @Test
    @DisplayName("Should calculate total amount correctly with single item")
    void shouldCalculateTotalAmountCorrectlyWithSingleItem() {
        Order order = new Order(testCustomer);
        OrderItem orderItem = OrderItem.builder()
                .product(testProduct)
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .build();

        order.addOrderItem(orderItem);

        BigDecimal totalAmount = order.getTotalAmount();
        assertEquals(new BigDecimal("20.00"), totalAmount);
    }

    @Test
    @DisplayName("Should calculate total amount correctly with multiple items")
    void shouldCalculateTotalAmountCorrectlyWithMultipleItems() {
        Order order = new Order(testCustomer);
        
        OrderItem orderItem1 = OrderItem.builder()
                .product(testProduct)
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .build();
                
        OrderItem orderItem2 = OrderItem.builder()
                .product(testProduct)
                .quantity(1)
                .unitPrice(new BigDecimal("15.50"))
                .build();

        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);

        BigDecimal totalAmount = order.getTotalAmount();
        assertEquals(new BigDecimal("35.50"), totalAmount);
    }

    @Test
    @DisplayName("Should add order item correctly")
    void shouldAddOrderItemCorrectly() {
        Order order = new Order(testCustomer);
        OrderItem orderItem = OrderItem.builder()
                .product(testProduct)
                .quantity(1)
                .unitPrice(new BigDecimal("10.00"))
                .build();

        order.addOrderItem(orderItem);

        assertEquals(1, order.getOrderItems().size());
        assertTrue(order.getOrderItems().contains(orderItem));
        assertEquals(order, orderItem.getOrder());
    }

    @Test
    @DisplayName("Should remove order item correctly")
    void shouldRemoveOrderItemCorrectly() {
        Order order = new Order(testCustomer);
        OrderItem orderItem = OrderItem.builder()
                .product(testProduct)
                .quantity(1)
                .unitPrice(new BigDecimal("10.00"))
                .build();

        order.addOrderItem(orderItem);
        assertEquals(1, order.getOrderItems().size());

        order.removeOrderItem(orderItem);
        assertEquals(0, order.getOrderItems().size());
        assertFalse(order.getOrderItems().contains(orderItem));
        assertNull(orderItem.getOrder());
    }

    @Test
    @DisplayName("Should maintain bidirectional relationship when adding item")
    void shouldMaintainBidirectionalRelationshipWhenAddingItem() {
        Order order = new Order(testCustomer);
        OrderItem orderItem = OrderItem.builder()
                .product(testProduct)
                .quantity(1)
                .unitPrice(new BigDecimal("10.00"))
                .build();

        order.addOrderItem(orderItem);

        assertEquals(order, orderItem.getOrder());
        assertTrue(order.getOrderItems().contains(orderItem));
    }

    @Test
    @DisplayName("Should maintain bidirectional relationship when removing item")
    void shouldMaintainBidirectionalRelationshipWhenRemovingItem() {
        Order order = new Order(testCustomer);
        OrderItem orderItem = OrderItem.builder()
                .product(testProduct)
                .quantity(1)
                .unitPrice(new BigDecimal("10.00"))
                .build();

        order.addOrderItem(orderItem);
        order.removeOrderItem(orderItem);

        assertNull(orderItem.getOrder());
        assertFalse(order.getOrderItems().contains(orderItem));
    }
}
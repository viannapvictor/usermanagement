package br.com.infnet.edur.usermanagement.repository;

import br.com.infnet.edur.usermanagement.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("OrderItemRepository Tests")
class OrderItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private Customer testCustomer;
    private Product testProduct;
    private Order testOrder;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .name("Test Customer")
                .email("customer@example.com")
                .phoneNumber("+1234567890")
                .build();
                
        testProduct = Product.builder()
                .name("Test Product")
                .unitPrice(new BigDecimal("10.00"))
                .build();
                
        testOrder = Order.builder()
                .customer(testCustomer)
                .orderDate(LocalDateTime.now())
                .build();
                
        testOrderItem = OrderItem.builder()
                .product(testProduct)
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .order(testOrder)
                .build();
    }

    @Test
    @DisplayName("Should save order item successfully")
    void shouldSaveOrderItemSuccessfully() {
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);
        Product savedProduct = entityManager.persistAndFlush(testProduct);
        
        Order savedOrder = Order.builder()
                .customer(savedCustomer)
                .orderDate(LocalDateTime.now())
                .build();
        savedOrder = entityManager.persistAndFlush(savedOrder);
        
        OrderItem orderItem = OrderItem.builder()
                .product(savedProduct)
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .order(savedOrder)
                .build();

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        assertNotNull(savedOrderItem.getId());
        assertEquals(savedProduct.getId(), savedOrderItem.getProduct().getId());
        assertEquals(2, savedOrderItem.getQuantity());
        assertEquals(new BigDecimal("10.00"), savedOrderItem.getUnitPrice());
        assertEquals(savedOrder.getId(), savedOrderItem.getOrder().getId());
    }

    @Test
    @DisplayName("Should find order item by id")
    void shouldFindOrderItemById() {
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);
        Product savedProduct = entityManager.persistAndFlush(testProduct);
        Order savedOrder = Order.builder()
                .customer(savedCustomer)
                .orderDate(LocalDateTime.now())
                .build();
        savedOrder = entityManager.persistAndFlush(savedOrder);
        
        OrderItem orderItem = OrderItem.builder()
                .product(savedProduct)
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .order(savedOrder)
                .build();
        OrderItem savedOrderItem = entityManager.persistAndFlush(orderItem);

        Optional<OrderItem> foundOrderItem = orderItemRepository.findById(savedOrderItem.getId());

        assertTrue(foundOrderItem.isPresent());
        assertEquals(savedOrderItem.getId(), foundOrderItem.get().getId());
        assertEquals(savedProduct.getId(), foundOrderItem.get().getProduct().getId());
        assertEquals(2, foundOrderItem.get().getQuantity());
    }

    @Test
    @DisplayName("Should return empty when order item not found by id")
    void shouldReturnEmptyWhenOrderItemNotFoundById() {
        Optional<OrderItem> foundOrderItem = orderItemRepository.findById(999L);

        assertFalse(foundOrderItem.isPresent());
    }

    @Test
    @DisplayName("Should find all order items")
    void shouldFindAllOrderItems() {
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);
        
        Product product1 = Product.builder()
                .name("Product 1")
                .unitPrice(new BigDecimal("10.00"))
                .build();
        Product product2 = Product.builder()
                .name("Product 2")
                .unitPrice(new BigDecimal("20.00"))
                .build();
                
        Product savedProduct1 = entityManager.persist(product1);
        Product savedProduct2 = entityManager.persist(product2);
        
        Order savedOrder = Order.builder()
                .customer(savedCustomer)
                .orderDate(LocalDateTime.now())
                .build();
        savedOrder = entityManager.persistAndFlush(savedOrder);

        OrderItem orderItem1 = OrderItem.builder()
                .product(savedProduct1)
                .quantity(1)
                .unitPrice(new BigDecimal("10.00"))
                .order(savedOrder)
                .build();
                
        OrderItem orderItem2 = OrderItem.builder()
                .product(savedProduct2)
                .quantity(2)
                .unitPrice(new BigDecimal("20.00"))
                .order(savedOrder)
                .build();

        entityManager.persist(orderItem1);
        entityManager.persist(orderItem2);
        entityManager.flush();

        List<OrderItem> orderItems = orderItemRepository.findAll();

        assertEquals(2, orderItems.size());
    }

    @Test
    @DisplayName("Should delete order item successfully")
    void shouldDeleteOrderItemSuccessfully() {
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);
        Product savedProduct = entityManager.persistAndFlush(testProduct);
        Order savedOrder = Order.builder()
                .customer(savedCustomer)
                .orderDate(LocalDateTime.now())
                .build();
        savedOrder = entityManager.persistAndFlush(savedOrder);
        
        OrderItem orderItem = OrderItem.builder()
                .product(savedProduct)
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .order(savedOrder)
                .build();
        OrderItem savedOrderItem = entityManager.persistAndFlush(orderItem);
        Long orderItemId = savedOrderItem.getId();

        orderItemRepository.delete(savedOrderItem);
        entityManager.flush();

        Optional<OrderItem> deletedOrderItem = orderItemRepository.findById(orderItemId);
        assertFalse(deletedOrderItem.isPresent());
    }

    @Test
    @DisplayName("Should update order item successfully")
    void shouldUpdateOrderItemSuccessfully() {
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);
        Product savedProduct = entityManager.persistAndFlush(testProduct);
        Order savedOrder = Order.builder()
                .customer(savedCustomer)
                .orderDate(LocalDateTime.now())
                .build();
        savedOrder = entityManager.persistAndFlush(savedOrder);
        
        OrderItem orderItem = OrderItem.builder()
                .product(savedProduct)
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .order(savedOrder)
                .build();
        OrderItem savedOrderItem = entityManager.persistAndFlush(orderItem);
        
        savedOrderItem.setQuantity(5);
        savedOrderItem.setUnitPrice(new BigDecimal("15.00"));

        OrderItem updatedOrderItem = orderItemRepository.save(savedOrderItem);

        assertEquals(5, updatedOrderItem.getQuantity());
        assertEquals(new BigDecimal("15.00"), updatedOrderItem.getUnitPrice());
        assertEquals(new BigDecimal("75.00"), updatedOrderItem.getTotalPrice());
    }

    @Test
    @DisplayName("Should handle multiple order items for same product")
    void shouldHandleMultipleOrderItemsForSameProduct() {
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);
        Product savedProduct = entityManager.persistAndFlush(testProduct);
        
        Order order1 = Order.builder()
                .customer(savedCustomer)
                .orderDate(LocalDateTime.now())
                .build();
        Order order2 = Order.builder()
                .customer(savedCustomer)
                .orderDate(LocalDateTime.now().minusDays(1))
                .build();
                
        Order savedOrder1 = entityManager.persist(order1);
        Order savedOrder2 = entityManager.persist(order2);

        OrderItem orderItem1 = OrderItem.builder()
                .product(savedProduct)
                .quantity(1)
                .unitPrice(new BigDecimal("10.00"))
                .order(savedOrder1)
                .build();
                
        OrderItem orderItem2 = OrderItem.builder()
                .product(savedProduct)
                .quantity(3)
                .unitPrice(new BigDecimal("12.00"))
                .order(savedOrder2)
                .build();

        entityManager.persist(orderItem1);
        entityManager.persist(orderItem2);
        entityManager.flush();

        List<OrderItem> orderItems = orderItemRepository.findAll();
        assertEquals(2, orderItems.size());
        
        assertTrue(orderItems.stream().allMatch(item -> 
            item.getProduct().getId().equals(savedProduct.getId())));
    }

    @Test
    @DisplayName("Should calculate total price correctly in saved order item")
    void shouldCalculateTotalPriceCorrectlyInSavedOrderItem() {
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);
        Product savedProduct = entityManager.persistAndFlush(testProduct);
        Order savedOrder = Order.builder()
                .customer(savedCustomer)
                .orderDate(LocalDateTime.now())
                .build();
        savedOrder = entityManager.persistAndFlush(savedOrder);
        
        OrderItem orderItem = OrderItem.builder()
                .product(savedProduct)
                .quantity(3)
                .unitPrice(new BigDecimal("15.50"))
                .order(savedOrder)
                .build();

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        
        assertEquals(new BigDecimal("46.50"), savedOrderItem.getTotalPrice());
    }
}
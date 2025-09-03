package br.com.infnet.edur.usermanagement.repository;

import br.com.infnet.edur.usermanagement.model.Customer;
import br.com.infnet.edur.usermanagement.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("OrderRepository Tests")
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private Customer testCustomer;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .name("Test Customer")
                .email("customer@example.com")
                .phoneNumber("+1234567890")
                .build();
                
        testOrder = Order.builder()
                .customer(testCustomer)
                .orderDate(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should save order successfully")
    void shouldSaveOrderSuccessfully() {
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);
        testOrder = Order.builder()
                .customer(savedCustomer)
                .orderDate(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(testOrder);

        assertNotNull(savedOrder.getId());
        assertEquals(savedCustomer.getId(), savedOrder.getCustomer().getId());
        assertNotNull(savedOrder.getOrderDate());
    }

    @Test
    @DisplayName("Should find order by id")
    void shouldFindOrderById() {
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);
        testOrder = Order.builder()
                .customer(savedCustomer)
                .orderDate(LocalDateTime.now())
                .build();
        Order savedOrder = entityManager.persistAndFlush(testOrder);

        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());

        assertTrue(foundOrder.isPresent());
        assertEquals(savedOrder.getId(), foundOrder.get().getId());
        assertEquals(savedCustomer.getId(), foundOrder.get().getCustomer().getId());
    }

    @Test
    @DisplayName("Should return empty when order not found by id")
    void shouldReturnEmptyWhenOrderNotFoundById() {
        Optional<Order> foundOrder = orderRepository.findById(999L);

        assertFalse(foundOrder.isPresent());
    }

    @Test
    @DisplayName("Should find all orders")
    void shouldFindAllOrders() {
        Customer customer1 = Customer.builder()
                .name("Customer 1")
                .email("customer1@example.com")
                .phoneNumber("+1111111111")
                .build();
                
        Customer customer2 = Customer.builder()
                .name("Customer 2")
                .email("customer2@example.com")
                .phoneNumber("+2222222222")
                .build();

        Customer savedCustomer1 = entityManager.persist(customer1);
        Customer savedCustomer2 = entityManager.persist(customer2);

        Order order1 = Order.builder()
                .customer(savedCustomer1)
                .orderDate(LocalDateTime.now())
                .build();
                
        Order order2 = Order.builder()
                .customer(savedCustomer2)
                .orderDate(LocalDateTime.now())
                .build();

        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.flush();

        List<Order> orders = orderRepository.findAll();

        assertEquals(2, orders.size());
    }

    @Test
    @DisplayName("Should find orders by customer")
    void shouldFindOrdersByCustomer() {
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);
        
        Order order1 = Order.builder()
                .customer(savedCustomer)
                .orderDate(LocalDateTime.now())
                .build();
                
        Order order2 = Order.builder()
                .customer(savedCustomer)
                .orderDate(LocalDateTime.now().minusDays(1))
                .build();

        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.flush();

        List<Order> orders = orderRepository.findByCustomer(savedCustomer);

        assertEquals(2, orders.size());
        assertTrue(orders.stream().allMatch(order -> order.getCustomer().getId().equals(savedCustomer.getId())));
    }

    @Test
    @DisplayName("Should find orders by customer id")
    void shouldFindOrdersByCustomerId() {
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);
        
        Order order1 = Order.builder()
                .customer(savedCustomer)
                .orderDate(LocalDateTime.now())
                .build();
                
        Order order2 = Order.builder()
                .customer(savedCustomer)
                .orderDate(LocalDateTime.now().minusDays(1))
                .build();

        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.flush();

        List<Order> orders = orderRepository.findByCustomerId(savedCustomer.getId());

        assertEquals(2, orders.size());
        assertTrue(orders.stream().allMatch(order -> order.getCustomer().getId().equals(savedCustomer.getId())));
    }

    @Test
    @DisplayName("Should return empty list when no orders found for customer")
    void shouldReturnEmptyListWhenNoOrdersFoundForCustomer() {
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);

        List<Order> orders = orderRepository.findByCustomer(savedCustomer);

        assertTrue(orders.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no orders found for customer id")
    void shouldReturnEmptyListWhenNoOrdersFoundForCustomerId() {
        List<Order> orders = orderRepository.findByCustomerId(999L);

        assertTrue(orders.isEmpty());
    }

    @Test
    @DisplayName("Should delete order successfully")
    void shouldDeleteOrderSuccessfully() {
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);
        testOrder = Order.builder()
                .customer(savedCustomer)
                .orderDate(LocalDateTime.now())
                .build();
        Order savedOrder = entityManager.persistAndFlush(testOrder);
        Long orderId = savedOrder.getId();

        orderRepository.delete(savedOrder);
        entityManager.flush();

        Optional<Order> deletedOrder = orderRepository.findById(orderId);
        assertFalse(deletedOrder.isPresent());
    }

    @Test
    @DisplayName("Should handle multiple customers with different orders")
    void shouldHandleMultipleCustomersWithDifferentOrders() {
        Customer customer1 = Customer.builder()
                .name("Customer 1")
                .email("customer1@example.com")
                .phoneNumber("+1111111111")
                .build();
                
        Customer customer2 = Customer.builder()
                .name("Customer 2")
                .email("customer2@example.com")
                .phoneNumber("+2222222222")
                .build();

        Customer savedCustomer1 = entityManager.persist(customer1);
        Customer savedCustomer2 = entityManager.persist(customer2);

        Order order1 = Order.builder()
                .customer(savedCustomer1)
                .orderDate(LocalDateTime.now())
                .build();
                
        Order order2 = Order.builder()
                .customer(savedCustomer1)
                .orderDate(LocalDateTime.now().minusDays(1))
                .build();
                
        Order order3 = Order.builder()
                .customer(savedCustomer2)
                .orderDate(LocalDateTime.now())
                .build();

        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.persist(order3);
        entityManager.flush();

        List<Order> customer1Orders = orderRepository.findByCustomer(savedCustomer1);
        List<Order> customer2Orders = orderRepository.findByCustomer(savedCustomer2);

        assertEquals(2, customer1Orders.size());
        assertEquals(1, customer2Orders.size());
        assertEquals(3, orderRepository.findAll().size());
    }
}
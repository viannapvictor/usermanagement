package br.com.infnet.edur.usermanagement.service;

import br.com.infnet.edur.usermanagement.dto.request.OrderInputDTO;
import br.com.infnet.edur.usermanagement.dto.request.OrderItemInputDTO;
import br.com.infnet.edur.usermanagement.model.Customer;
import br.com.infnet.edur.usermanagement.model.Order;
import br.com.infnet.edur.usermanagement.model.OrderItem;
import br.com.infnet.edur.usermanagement.repository.OrderRepository;
import br.com.infnet.edur.usermanagement.repository.OrderItemRepository;
import br.com.infnet.edur.usermanagement.utils.exceptions.OrderNotFoundException;
import br.com.infnet.edur.usermanagement.utils.exceptions.OrderValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private OrderItemService orderItemService;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private Customer testCustomer;
    private OrderInputDTO testOrderInputDTO;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .id(1L)
                .name("Test Customer")
                .email("customer@example.com")
                .phoneNumber("+1234567890")
                .build();

        testOrder = Order.builder()
                .id(1L)
                .customer(testCustomer)
                .orderDate(LocalDateTime.now())
                .build();

        OrderItemInputDTO orderItemInputDTO = new OrderItemInputDTO(1L, 2);

        testOrderInputDTO = new OrderInputDTO(1L, Arrays.asList(orderItemInputDTO));
    }

    @Test
    @DisplayName("Should get all orders successfully")
    void shouldGetAllOrdersSuccessfully() {
        Order order1 = Order.builder().id(1L).customer(testCustomer).build();
        Order order2 = Order.builder().id(2L).customer(testCustomer).build();
        List<Order> orders = Arrays.asList(order1, order2);

        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertEquals(2, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    @DisplayName("Should get order by id successfully")
    void shouldGetOrderByIdSuccessfully() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        Order result = orderService.getOrderById(1L);

        assertEquals(testOrder.getId(), result.getId());
        assertEquals(testOrder.getCustomer().getId(), result.getCustomer().getId());
        verify(orderRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order not found by id")
    void shouldThrowOrderNotFoundExceptionWhenOrderNotFoundById() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(999L));
        verify(orderRepository).findById(999L);
    }

    @Test
    @DisplayName("Should get orders by customer id successfully")
    void shouldGetOrdersByCustomerIdSuccessfully() {
        List<Order> orders = Arrays.asList(testOrder);

        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);
        when(orderRepository.findByCustomerId(1L)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByCustomerId(1L);

        assertEquals(1, result.size());
        assertEquals(testOrder.getId(), result.get(0).getId());
        verify(customerService).getCustomerById(1L);
        verify(orderRepository).findByCustomerId(1L);
    }

    @Test
    @DisplayName("Should create order successfully")
    void shouldCreateOrderSuccessfully() {
        OrderItem testOrderItem = OrderItem.builder().id(1L).build();
        List<OrderItem> orderItems = Arrays.asList(testOrderItem);

        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemService.createOrderItemsForOrder(any(Order.class), anyList())).thenReturn(orderItems);

        Order result = orderService.createOrder(testOrderInputDTO);

        assertNotNull(result);
        assertEquals(testCustomer.getId(), result.getCustomer().getId());
        verify(customerService).getCustomerById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(orderItemService).createOrderItemsForOrder(any(Order.class), anyList());
    }

    @Test
    @DisplayName("Should throw OrderValidationException when creating order without items")
    void shouldThrowOrderValidationExceptionWhenCreatingOrderWithoutItems() {
        OrderInputDTO emptyOrderInputDTO = new OrderInputDTO(1L, Collections.emptyList());

        assertThrows(OrderValidationException.class, () -> orderService.createOrder(emptyOrderInputDTO));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should update order successfully")
    void shouldUpdateOrderSuccessfully() {
        OrderItem existingOrderItem = OrderItem.builder().id(1L).build();
        testOrder.getOrderItems().add(existingOrderItem);

        OrderItem newOrderItem = OrderItem.builder().id(2L).build();
        List<OrderItem> newOrderItems = Arrays.asList(newOrderItem);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);
        when(orderItemService.createOrderItemsForOrder(any(Order.class), anyList())).thenReturn(newOrderItems);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.updateOrder(1L, testOrderInputDTO);

        assertNotNull(result);
        verify(orderRepository).findById(1L);
        verify(customerService).getCustomerById(1L);
        verify(orderItemRepository).deleteAll(anyList());
        verify(orderItemService).createOrderItemsForOrder(any(Order.class), anyList());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw OrderValidationException when updating order without items")
    void shouldThrowOrderValidationExceptionWhenUpdatingOrderWithoutItems() {
        OrderInputDTO emptyOrderInputDTO = new OrderInputDTO(1L, Collections.emptyList());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        assertThrows(OrderValidationException.class, () -> orderService.updateOrder(1L, emptyOrderInputDTO));
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should delete order successfully")
    void shouldDeleteOrderSuccessfully() {
        OrderItem orderItem = OrderItem.builder().id(1L).build();
        testOrder.getOrderItems().add(orderItem);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        orderService.deleteOrder(1L);

        verify(orderRepository).findById(1L);
        verify(orderItemRepository).deleteAll(testOrder.getOrderItems());
        verify(orderRepository).delete(testOrder);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when deleting non-existent order")
    void shouldThrowOrderNotFoundExceptionWhenDeletingNonExistentOrder() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(999L));
        verify(orderRepository).findById(999L);
        verify(orderRepository, never()).delete(any(Order.class));
    }

    @Test
    @DisplayName("Should validate customer existence when getting orders by customer id")
    void shouldValidateCustomerExistenceWhenGettingOrdersByCustomerId() {
        when(customerService.getCustomerById(999L)).thenThrow(new RuntimeException("Customer not found"));

        assertThrows(RuntimeException.class, () -> orderService.getOrdersByCustomerId(999L));
        verify(customerService).getCustomerById(999L);
        verify(orderRepository, never()).findByCustomerId(anyLong());
    }
}
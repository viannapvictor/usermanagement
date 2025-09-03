package br.com.infnet.edur.usermanagement.service;

import br.com.infnet.edur.usermanagement.dto.request.OrderItemInputDTO;
import br.com.infnet.edur.usermanagement.model.Order;
import br.com.infnet.edur.usermanagement.model.OrderItem;
import br.com.infnet.edur.usermanagement.model.Product;
import br.com.infnet.edur.usermanagement.repository.OrderItemRepository;
import br.com.infnet.edur.usermanagement.utils.exceptions.OrderValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderItemService Tests")
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderItemService orderItemService;

    private OrderItem testOrderItem;
    private Product testProduct;
    private Order testOrder;
    private OrderItemInputDTO testOrderItemInputDTO;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .unitPrice(new BigDecimal("10.00"))
                .build();

        testOrder = Order.builder()
                .id(1L)
                .build();

        testOrderItem = OrderItem.builder()
                .id(1L)
                .product(testProduct)
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .order(testOrder)
                .build();

        testOrderItemInputDTO = new OrderItemInputDTO(1L, 2);
    }

    @Test
    @DisplayName("Should get all order items successfully")
    void shouldGetAllOrderItemsSuccessfully() {
        OrderItem orderItem1 = OrderItem.builder().id(1L).build();
        OrderItem orderItem2 = OrderItem.builder().id(2L).build();
        List<OrderItem> orderItems = Arrays.asList(orderItem1, orderItem2);

        when(orderItemRepository.findAll()).thenReturn(orderItems);

        List<OrderItem> result = orderItemService.getAllOrderItems();

        assertEquals(2, result.size());
        verify(orderItemRepository).findAll();
    }

    @Test
    @DisplayName("Should get order item by id successfully")
    void shouldGetOrderItemByIdSuccessfully() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));

        OrderItem result = orderItemService.getOrderItemById(1L);

        assertEquals(testOrderItem.getId(), result.getId());
        assertEquals(testOrderItem.getProduct().getId(), result.getProduct().getId());
        assertEquals(testOrderItem.getQuantity(), result.getQuantity());
        verify(orderItemRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw OrderValidationException when order item not found by id")
    void shouldThrowOrderValidationExceptionWhenOrderItemNotFoundById() {
        when(orderItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderValidationException.class, () -> orderItemService.getOrderItemById(999L));
        verify(orderItemRepository).findById(999L);
    }

    @Test
    @DisplayName("Should create order item successfully")
    void shouldCreateOrderItemSuccessfully() {
        when(productService.getProductById(1L)).thenReturn(testProduct);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        OrderItem result = orderItemService.createOrderItem(testOrder, testOrderItemInputDTO);

        assertEquals(testProduct.getId(), result.getProduct().getId());
        assertEquals(2, result.getQuantity());
        assertEquals(testProduct.getUnitPrice(), result.getUnitPrice());
        assertEquals(testOrder.getId(), result.getOrder().getId());
        verify(productService).getProductById(1L);
        verify(orderItemRepository).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Should update order item successfully")
    void shouldUpdateOrderItemSuccessfully() {
        Product newProduct = Product.builder()
                .id(2L)
                .name("New Product")
                .unitPrice(new BigDecimal("15.00"))
                .build();

        OrderItemInputDTO updateDTO = new OrderItemInputDTO(2L, 3);

        OrderItem updatedOrderItem = OrderItem.builder()
                .id(1L)
                .product(newProduct)
                .quantity(3)
                .unitPrice(new BigDecimal("15.00"))
                .order(testOrder)
                .build();

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));
        when(productService.getProductById(2L)).thenReturn(newProduct);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(updatedOrderItem);

        OrderItem result = orderItemService.updateOrderItem(1L, updateDTO);

        assertEquals(newProduct.getId(), result.getProduct().getId());
        assertEquals(3, result.getQuantity());
        assertEquals(new BigDecimal("15.00"), result.getUnitPrice());
        verify(orderItemRepository).findById(1L);
        verify(productService).getProductById(2L);
        verify(orderItemRepository).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Should delete order item successfully")
    void shouldDeleteOrderItemSuccessfully() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));

        orderItemService.deleteOrderItem(1L);

        verify(orderItemRepository).findById(1L);
        verify(orderItemRepository).delete(testOrderItem);
    }

    @Test
    @DisplayName("Should throw OrderValidationException when deleting non-existent order item")
    void shouldThrowOrderValidationExceptionWhenDeletingNonExistentOrderItem() {
        when(orderItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderValidationException.class, () -> orderItemService.deleteOrderItem(999L));
        verify(orderItemRepository).findById(999L);
        verify(orderItemRepository, never()).delete(any(OrderItem.class));
    }

    @Test
    @DisplayName("Should create multiple order items for order successfully")
    void shouldCreateMultipleOrderItemsForOrderSuccessfully() {
        OrderItemInputDTO item1DTO = new OrderItemInputDTO(1L, 2);

        OrderItemInputDTO item2DTO = new OrderItemInputDTO(2L, 3);

        List<OrderItemInputDTO> itemDTOs = Arrays.asList(item1DTO, item2DTO);

        Product product2 = Product.builder()
                .id(2L)
                .name("Product 2")
                .unitPrice(new BigDecimal("20.00"))
                .build();

        OrderItem orderItem1 = OrderItem.builder()
                .id(1L)
                .product(testProduct)
                .quantity(2)
                .unitPrice(testProduct.getUnitPrice())
                .order(testOrder)
                .build();

        OrderItem orderItem2 = OrderItem.builder()
                .id(2L)
                .product(product2)
                .quantity(3)
                .unitPrice(product2.getUnitPrice())
                .order(testOrder)
                .build();

        when(productService.getProductById(1L)).thenReturn(testProduct);
        when(productService.getProductById(2L)).thenReturn(product2);
        when(orderItemRepository.save(any(OrderItem.class)))
                .thenReturn(orderItem1)
                .thenReturn(orderItem2);

        List<OrderItem> result = orderItemService.createOrderItemsForOrder(testOrder, itemDTOs);

        assertEquals(2, result.size());
        verify(productService).getProductById(1L);
        verify(productService).getProductById(2L);
        verify(orderItemRepository, times(2)).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Should add item to order successfully")
    void shouldAddItemToOrderSuccessfully() {
        when(productService.getProductById(1L)).thenReturn(testProduct);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        OrderItem result = orderItemService.addItemToOrder(1L, testOrderItemInputDTO);

        assertEquals(testProduct.getId(), result.getProduct().getId());
        assertEquals(2, result.getQuantity());
        assertEquals(testProduct.getUnitPrice(), result.getUnitPrice());
        verify(productService).getProductById(1L);
        verify(orderItemRepository).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Should use product unit price when creating order item")
    void shouldUseProductUnitPriceWhenCreatingOrderItem() {
        BigDecimal productPrice = new BigDecimal("25.99");
        Product expensiveProduct = Product.builder()
                .id(1L)
                .name("Expensive Product")
                .unitPrice(productPrice)
                .build();

        OrderItem expectedOrderItem = OrderItem.builder()
                .id(1L)
                .product(expensiveProduct)
                .quantity(2)
                .unitPrice(productPrice)
                .order(testOrder)
                .build();

        when(productService.getProductById(1L)).thenReturn(expensiveProduct);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(expectedOrderItem);

        OrderItem result = orderItemService.createOrderItem(testOrder, testOrderItemInputDTO);

        assertEquals(productPrice, result.getUnitPrice());
        verify(productService).getProductById(1L);
        verify(orderItemRepository).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Should preserve existing order when updating order item")
    void shouldPreserveExistingOrderWhenUpdatingOrderItem() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));
        when(productService.getProductById(1L)).thenReturn(testProduct);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        OrderItem result = orderItemService.updateOrderItem(1L, testOrderItemInputDTO);

        assertEquals(testOrder.getId(), result.getOrder().getId());
        verify(orderItemRepository).findById(1L);
        verify(productService).getProductById(1L);
        verify(orderItemRepository).save(any(OrderItem.class));
    }
}
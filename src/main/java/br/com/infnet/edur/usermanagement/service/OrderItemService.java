package br.com.infnet.edur.usermanagement.service;

import br.com.infnet.edur.usermanagement.dto.request.OrderItemInputDTO;
import br.com.infnet.edur.usermanagement.model.Order;
import br.com.infnet.edur.usermanagement.model.OrderItem;
import br.com.infnet.edur.usermanagement.model.Product;
import br.com.infnet.edur.usermanagement.repository.OrderItemRepository;
import br.com.infnet.edur.usermanagement.utils.exceptions.OrderValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private ProductService productService;
    
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }
    
    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new OrderValidationException("OrderItem not found with ID: " + id));
    }
    
    @Transactional
    public OrderItem createOrderItem(Order order, OrderItemInputDTO orderItemInputDTO) {
        Product product = productService.getProductById(orderItemInputDTO.getProductId());
        
        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(orderItemInputDTO.getQuantity())
                .unitPrice(product.getUnitPrice())
                .order(order)
                .build();
        
        return orderItemRepository.save(orderItem);
    }
    
    @Transactional
    public OrderItem updateOrderItem(Long id, OrderItemInputDTO orderItemInputDTO) {
        OrderItem existingOrderItem = getOrderItemById(id);
        Product product = productService.getProductById(orderItemInputDTO.getProductId());
        
        OrderItem updatedOrderItem = OrderItem.builder()
                .id(id)
                .product(product)
                .quantity(orderItemInputDTO.getQuantity())
                .unitPrice(product.getUnitPrice())
                .order(existingOrderItem.getOrder())
                .build();
        
        return orderItemRepository.save(updatedOrderItem);
    }
    
    @Transactional
    public void deleteOrderItem(Long id) {
        OrderItem orderItem = getOrderItemById(id);
        orderItemRepository.delete(orderItem);
    }
    
    public List<OrderItem> createOrderItemsForOrder(Order order, List<OrderItemInputDTO> orderItemInputDTOs) {
        return orderItemInputDTOs.stream()
                .map(itemDTO -> createOrderItem(order, itemDTO))
                .toList();
    }
    
    @Transactional
    public OrderItem addItemToOrder(Long orderId, OrderItemInputDTO orderItemInputDTO) {
        Product product = productService.getProductById(orderItemInputDTO.getProductId());
        
        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(orderItemInputDTO.getQuantity())
                .unitPrice(product.getUnitPrice())
                .order(Order.builder().id(orderId).build())
                .build();
        
        return orderItemRepository.save(orderItem);
    }
}
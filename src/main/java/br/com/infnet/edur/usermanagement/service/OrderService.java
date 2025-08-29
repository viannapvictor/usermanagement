package br.com.infnet.edur.usermanagement.service;

import br.com.infnet.edur.usermanagement.dto.request.OrderInputDTO;
import br.com.infnet.edur.usermanagement.dto.request.OrderItemInputDTO;
import br.com.infnet.edur.usermanagement.model.Order;
import br.com.infnet.edur.usermanagement.model.OrderItem;
import br.com.infnet.edur.usermanagement.model.Product;
import br.com.infnet.edur.usermanagement.model.Customer;
import br.com.infnet.edur.usermanagement.repository.OrderRepository;
import br.com.infnet.edur.usermanagement.repository.OrderItemRepository;
import br.com.infnet.edur.usermanagement.utils.exceptions.OrderNotFoundException;
import br.com.infnet.edur.usermanagement.utils.exceptions.OrderValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private OrderItemService orderItemService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
    
    public List<Order> getOrdersByCustomerId(Long customerId) {
        customerService.getCustomerById(customerId);
        return orderRepository.findByCustomerId(customerId);
    }

    @Transactional
    public Order createOrder(OrderInputDTO orderInputDTO) {
        Customer customer = customerService.getCustomerById(orderInputDTO.getCustomerId());
        
        if (orderInputDTO.getOrderItems().isEmpty()) {
            throw new OrderValidationException("Order must have at least one item");
        }
        
        Order order = Order.builder()
                .customer(customer)
                .orderDate(LocalDateTime.now())
                .orderItems(new ArrayList<>())
                .build();
        
        Order savedOrder = orderRepository.save(order);
        
        List<OrderItem> orderItems = orderItemService.createOrderItemsForOrder(savedOrder, orderInputDTO.getOrderItems());
        savedOrder.getOrderItems().addAll(orderItems);
        
        return savedOrder;
    }

    @Transactional
    public Order updateOrder(Long id, OrderInputDTO orderInputDTO) {
        Order existingOrder = getOrderById(id);
        Customer customer = customerService.getCustomerById(orderInputDTO.getCustomerId());
        
        if (orderInputDTO.getOrderItems().isEmpty()) {
            throw new OrderValidationException("Order must have at least one item");
        }
        
        orderItemRepository.deleteAll(existingOrder.getOrderItems());
        existingOrder.getOrderItems().clear();
        
        List<OrderItem> orderItems = orderItemService.createOrderItemsForOrder(existingOrder, orderInputDTO.getOrderItems());
        existingOrder.getOrderItems().addAll(orderItems);
        
        Order updatedOrder = Order.builder()
                .id(id)
                .customer(customer)
                .orderDate(existingOrder.getOrderDate())
                .orderItems(orderItems)
                .build();
        
        return orderRepository.save(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderItemRepository.deleteAll(order.getOrderItems());
        orderRepository.delete(order);
    }
}
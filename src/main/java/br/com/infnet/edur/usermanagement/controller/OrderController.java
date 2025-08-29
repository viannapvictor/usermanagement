package br.com.infnet.edur.usermanagement.controller;

import br.com.infnet.edur.usermanagement.dto.reponse.APIResponse;
import br.com.infnet.edur.usermanagement.dto.request.OrderInputDTO;
import br.com.infnet.edur.usermanagement.service.OrderService;
import br.com.infnet.edur.usermanagement.model.Order;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    public ResponseEntity<APIResponse<List<Order>>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        APIResponse<List<Order>> response = APIResponse.success(orders);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Order>> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        APIResponse<Order> response = APIResponse.success(order);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<APIResponse<List<Order>>> getOrdersByCustomerId(@PathVariable Long customerId) {
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);
        APIResponse<List<Order>> response = APIResponse.success(orders);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<APIResponse<Order>> createOrder(@Valid @RequestBody OrderInputDTO orderInputDTO) {
        Order createdOrder = orderService.createOrder(orderInputDTO);
        APIResponse<Order> response = APIResponse.success(createdOrder, HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<Order>> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderInputDTO orderInputDTO) {
        Order updatedOrder = orderService.updateOrder(id, orderInputDTO);
        APIResponse<Order> response = APIResponse.success(updatedOrder);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        APIResponse<Void> response = APIResponse.success(null, HttpStatus.NO_CONTENT.value());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
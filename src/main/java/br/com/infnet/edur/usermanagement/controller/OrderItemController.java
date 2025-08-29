package br.com.infnet.edur.usermanagement.controller;

import br.com.infnet.edur.usermanagement.dto.reponse.APIResponse;
import br.com.infnet.edur.usermanagement.dto.request.OrderItemInputDTO;
import br.com.infnet.edur.usermanagement.dto.request.OrderItemUpdateDTO;
import br.com.infnet.edur.usermanagement.service.OrderItemService;
import br.com.infnet.edur.usermanagement.service.OrderService;
import br.com.infnet.edur.usermanagement.model.OrderItem;
import br.com.infnet.edur.usermanagement.model.Order;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {
    
    @Autowired
    private OrderItemService orderItemService;
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    public ResponseEntity<APIResponse<List<OrderItem>>> getAllOrderItems() {
        List<OrderItem> orderItems = orderItemService.getAllOrderItems();
        APIResponse<List<OrderItem>> response = APIResponse.success(orderItems);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<OrderItem>> getOrderItemById(@PathVariable Long id) {
        OrderItem orderItem = orderItemService.getOrderItemById(id);
        APIResponse<OrderItem> response = APIResponse.success(orderItem);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/order/{orderId}")
    public ResponseEntity<APIResponse<OrderItem>> addItemToOrder(
            @PathVariable Long orderId, 
            @Valid @RequestBody OrderItemInputDTO orderItemInputDTO) {
        
        Order order = orderService.getOrderById(orderId);
        OrderItem createdOrderItem = orderItemService.createOrderItem(order, orderItemInputDTO);
        APIResponse<OrderItem> response = APIResponse.success(createdOrderItem, HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<OrderItem>> updateOrderItem(
            @PathVariable Long id, 
            @Valid @RequestBody OrderItemUpdateDTO orderItemUpdateDTO) {
        
        OrderItemInputDTO inputDTO = new OrderItemInputDTO(
            orderItemUpdateDTO.getProductId(), 
            orderItemUpdateDTO.getQuantity()
        );
        OrderItem updatedOrderItem = orderItemService.updateOrderItem(id, inputDTO);
        APIResponse<OrderItem> response = APIResponse.success(updatedOrderItem);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        APIResponse<Void> response = APIResponse.success(null, HttpStatus.NO_CONTENT.value());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
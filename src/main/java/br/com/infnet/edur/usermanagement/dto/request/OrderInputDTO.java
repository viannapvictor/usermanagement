package br.com.infnet.edur.usermanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderInputDTO {
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotNull(message = "Order items are required")
    @Size(min = 1, message = "Order must have at least one item")
    @Valid
    private List<OrderItemInputDTO> orderItems;
}
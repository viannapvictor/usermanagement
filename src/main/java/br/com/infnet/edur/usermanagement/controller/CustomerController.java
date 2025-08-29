package br.com.infnet.edur.usermanagement.controller;

import br.com.infnet.edur.usermanagement.dto.reponse.APIResponse;
import br.com.infnet.edur.usermanagement.dto.request.CustomerInputDTO;
import br.com.infnet.edur.usermanagement.service.CustomerService;
import br.com.infnet.edur.usermanagement.model.Customer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @GetMapping
    public ResponseEntity<APIResponse<List<Customer>>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        APIResponse<List<Customer>> response = APIResponse.success(customers);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Customer>> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        APIResponse<Customer> response = APIResponse.success(customer);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<APIResponse<Customer>> createCustomer(@Valid @RequestBody CustomerInputDTO customerInputDTO) {
        Customer customer = new Customer(customerInputDTO.getName(), customerInputDTO.getEmail(), 
                                        customerInputDTO.getPhoneNumber());
        Customer createdCustomer = customerService.createCustomer(customer);
        APIResponse<Customer> response = APIResponse.success(createdCustomer, HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<Customer>> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerInputDTO customerInputDTO) {
        Customer customer = new Customer(customerInputDTO.getName(), customerInputDTO.getEmail(), 
                                        customerInputDTO.getPhoneNumber());
        Customer updatedCustomer = customerService.updateCustomer(id, customer);
        APIResponse<Customer> response = APIResponse.success(updatedCustomer);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        APIResponse<Void> response = APIResponse.success(null, HttpStatus.NO_CONTENT.value());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
package br.com.infnet.edur.usermanagement.service;

import br.com.infnet.edur.usermanagement.model.Customer;
import br.com.infnet.edur.usermanagement.repository.CustomerRepository;
import br.com.infnet.edur.usermanagement.utils.exceptions.CustomerAlreadyExistsException;
import br.com.infnet.edur.usermanagement.utils.exceptions.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public Customer createCustomer(Customer customer) {
        
        if (existsByEmail(customer.getEmail())) {
            throw new CustomerAlreadyExistsException("email", customer.getEmail());
        }
        
        if (existsByPhoneNumber(customer.getPhoneNumber())) {
            throw new CustomerAlreadyExistsException("phone number", customer.getPhoneNumber());
        }
        
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer customer) {
        Customer existingCustomer = getCustomerById(id);
        
        if (!existingCustomer.getName().equals(customer.getName()) && existsByName(customer.getName())) {
            throw new CustomerAlreadyExistsException("name", customer.getName());
        }
        
        if (!existingCustomer.getEmail().equals(customer.getEmail()) && existsByEmail(customer.getEmail())) {
            throw new CustomerAlreadyExistsException("email", customer.getEmail());
        }
        
        if (!existingCustomer.getPhoneNumber().equals(customer.getPhoneNumber()) && existsByPhoneNumber(customer.getPhoneNumber())) {
            throw new CustomerAlreadyExistsException("phone number", customer.getPhoneNumber());
        }
        
        Customer updatedCustomer = Customer.builder()
                .id(id)
                .name(customer.getName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .build();
        
        return customerRepository.save(updatedCustomer);
    }

    public void deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
    }

    public boolean existsByName(String name) {
        return customerRepository.existsByName(name);
    }

    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return customerRepository.existsByPhoneNumber(phoneNumber);
    }
}
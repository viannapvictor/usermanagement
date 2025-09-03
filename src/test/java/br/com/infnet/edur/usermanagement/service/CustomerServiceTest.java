package br.com.infnet.edur.usermanagement.service;

import br.com.infnet.edur.usermanagement.model.Customer;
import br.com.infnet.edur.usermanagement.repository.CustomerRepository;
import br.com.infnet.edur.usermanagement.utils.exceptions.CustomerAlreadyExistsException;
import br.com.infnet.edur.usermanagement.utils.exceptions.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .id(1L)
                .name("John Smith")
                .email("john.smith@example.com")
                .phoneNumber("+1234567890")
                .build();
    }

    @Test
    @DisplayName("Should get all customers successfully")
    void shouldGetAllCustomersSuccessfully() {
        Customer customer1 = Customer.builder().id(1L).name("Customer 1").email("customer1@example.com").phoneNumber("+1111111111").build();
        Customer customer2 = Customer.builder().id(2L).name("Customer 2").email("customer2@example.com").phoneNumber("+2222222222").build();
        List<Customer> customers = Arrays.asList(customer1, customer2);

        when(customerRepository.findAll()).thenReturn(customers);

        List<Customer> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Should get customer by id successfully")
    void shouldGetCustomerByIdSuccessfully() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        Customer result = customerService.getCustomerById(1L);

        assertEquals(testCustomer.getId(), result.getId());
        assertEquals(testCustomer.getName(), result.getName());
        assertEquals(testCustomer.getEmail(), result.getEmail());
        verify(customerRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when customer not found by id")
    void shouldThrowCustomerNotFoundExceptionWhenCustomerNotFoundById() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(999L));
        verify(customerRepository).findById(999L);
    }

    @Test
    @DisplayName("Should create customer successfully")
    void shouldCreateCustomerSuccessfully() {
        Customer newCustomer = Customer.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .phoneNumber("+9876543210")
                .build();

        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

        Customer result = customerService.createCustomer(newCustomer);

        assertEquals(newCustomer.getName(), result.getName());
        assertEquals(newCustomer.getEmail(), result.getEmail());
        verify(customerRepository).existsByEmail(newCustomer.getEmail());
        verify(customerRepository).existsByPhoneNumber(newCustomer.getPhoneNumber());
        verify(customerRepository).save(newCustomer);
    }

    @Test
    @DisplayName("Should throw CustomerAlreadyExistsException when email exists")
    void shouldThrowCustomerAlreadyExistsExceptionWhenEmailExists() {
        when(customerRepository.existsByEmail(testCustomer.getEmail())).thenReturn(true);

        assertThrows(CustomerAlreadyExistsException.class, () -> customerService.createCustomer(testCustomer));
        verify(customerRepository).existsByEmail(testCustomer.getEmail());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw CustomerAlreadyExistsException when phone number exists")
    void shouldThrowCustomerAlreadyExistsExceptionWhenPhoneNumberExists() {
        when(customerRepository.existsByEmail(testCustomer.getEmail())).thenReturn(false);
        when(customerRepository.existsByPhoneNumber(testCustomer.getPhoneNumber())).thenReturn(true);

        assertThrows(CustomerAlreadyExistsException.class, () -> customerService.createCustomer(testCustomer));
        verify(customerRepository).existsByEmail(testCustomer.getEmail());
        verify(customerRepository).existsByPhoneNumber(testCustomer.getPhoneNumber());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should update customer successfully")
    void shouldUpdateCustomerSuccessfully() {
        Customer existingCustomer = Customer.builder()
                .id(1L)
                .name("Old Name")
                .email("old@example.com")
                .phoneNumber("+1111111111")
                .build();

        Customer updatedCustomerData = Customer.builder()
                .name("New Name")
                .email("new@example.com")
                .phoneNumber("+9999999999")
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByName("New Name")).thenReturn(false);
        when(customerRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(customerRepository.existsByPhoneNumber("+9999999999")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomerData);

        Customer result = customerService.updateCustomer(1L, updatedCustomerData);

        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw CustomerAlreadyExistsException when updating with existing name")
    void shouldThrowCustomerAlreadyExistsExceptionWhenUpdatingWithExistingName() {
        Customer existingCustomer = Customer.builder()
                .id(1L)
                .name("Original Name")
                .email("original@example.com")
                .phoneNumber("+1111111111")
                .build();

        Customer updatedCustomerData = Customer.builder()
                .name("Existing Name")
                .email("original@example.com")
                .phoneNumber("+1111111111")
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByName("Existing Name")).thenReturn(true);

        assertThrows(CustomerAlreadyExistsException.class, () -> customerService.updateCustomer(1L, updatedCustomerData));
        verify(customerRepository).findById(1L);
        verify(customerRepository).existsByName("Existing Name");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void shouldDeleteCustomerSuccessfully() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        customerService.deleteCustomer(1L);

        verify(customerRepository).findById(1L);
        verify(customerRepository).delete(testCustomer);
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when deleting non-existent customer")
    void shouldThrowCustomerNotFoundExceptionWhenDeletingNonExistentCustomer() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.deleteCustomer(999L));
        verify(customerRepository).findById(999L);
        verify(customerRepository, never()).delete(any(Customer.class));
    }

    @Test
    @DisplayName("Should return true when customer exists by name")
    void shouldReturnTrueWhenCustomerExistsByName() {
        when(customerRepository.existsByName("John Smith")).thenReturn(true);

        boolean result = customerService.existsByName("John Smith");

        assertTrue(result);
        verify(customerRepository).existsByName("John Smith");
    }

    @Test
    @DisplayName("Should return true when customer exists by email")
    void shouldReturnTrueWhenCustomerExistsByEmail() {
        when(customerRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = customerService.existsByEmail("test@example.com");

        assertTrue(result);
        verify(customerRepository).existsByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should return true when customer exists by phone number")
    void shouldReturnTrueWhenCustomerExistsByPhoneNumber() {
        when(customerRepository.existsByPhoneNumber("+1234567890")).thenReturn(true);

        boolean result = customerService.existsByPhoneNumber("+1234567890");

        assertTrue(result);
        verify(customerRepository).existsByPhoneNumber("+1234567890");
    }

    @Test
    @DisplayName("Should allow updating customer with same values")
    void shouldAllowUpdatingCustomerWithSameValues() {
        Customer existingCustomer = Customer.builder()
                .id(1L)
                .name("Same Name")
                .email("same@example.com")
                .phoneNumber("+1111111111")
                .build();

        Customer updatedCustomerData = Customer.builder()
                .name("Same Name")
                .email("same@example.com")
                .phoneNumber("+1111111111")
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomerData);

        Customer result = customerService.updateCustomer(1L, updatedCustomerData);

        assertEquals("Same Name", result.getName());
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(Customer.class));
        verify(customerRepository, never()).existsByName(anyString());
        verify(customerRepository, never()).existsByEmail(anyString());
        verify(customerRepository, never()).existsByPhoneNumber(anyString());
    }
}
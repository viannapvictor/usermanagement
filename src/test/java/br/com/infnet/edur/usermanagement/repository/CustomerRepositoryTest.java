package br.com.infnet.edur.usermanagement.repository;

import br.com.infnet.edur.usermanagement.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("CustomerRepository Tests")
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .name("John Smith")
                .email("john.smith@example.com")
                .phoneNumber("+1234567890")
                .build();
    }

    @Test
    @DisplayName("Should save customer successfully")
    void shouldSaveCustomerSuccessfully() {
        Customer savedCustomer = customerRepository.save(testCustomer);

        assertNotNull(savedCustomer.getId());
        assertEquals(testCustomer.getName(), savedCustomer.getName());
        assertEquals(testCustomer.getEmail(), savedCustomer.getEmail());
        assertEquals(testCustomer.getPhoneNumber(), savedCustomer.getPhoneNumber());
    }

    @Test
    @DisplayName("Should find customer by id")
    void shouldFindCustomerById() {
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);

        Optional<Customer> foundCustomer = customerRepository.findById(savedCustomer.getId());

        assertTrue(foundCustomer.isPresent());
        assertEquals(savedCustomer.getId(), foundCustomer.get().getId());
        assertEquals(savedCustomer.getName(), foundCustomer.get().getName());
    }

    @Test
    @DisplayName("Should return empty when customer not found by id")
    void shouldReturnEmptyWhenCustomerNotFoundById() {
        Optional<Customer> foundCustomer = customerRepository.findById(999L);

        assertFalse(foundCustomer.isPresent());
    }

    @Test
    @DisplayName("Should find all customers")
    void shouldFindAllCustomers() {
        Customer customer1 = Customer.builder()
                .name("Customer 1")
                .email("customer1@example.com")
                .phoneNumber("+1111111111")
                .build();
                
        Customer customer2 = Customer.builder()
                .name("Customer 2")
                .email("customer2@example.com")
                .phoneNumber("+2222222222")
                .build();

        entityManager.persist(customer1);
        entityManager.persist(customer2);
        entityManager.flush();

        List<Customer> customers = customerRepository.findAll();

        assertEquals(2, customers.size());
    }

    @Test
    @DisplayName("Should return true when customer exists by name")
    void shouldReturnTrueWhenCustomerExistsByName() {
        entityManager.persistAndFlush(testCustomer);

        boolean exists = customerRepository.existsByName(testCustomer.getName());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when customer does not exist by name")
    void shouldReturnFalseWhenCustomerDoesNotExistByName() {
        boolean exists = customerRepository.existsByName("Nonexistent Customer");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should return true when customer exists by email")
    void shouldReturnTrueWhenCustomerExistsByEmail() {
        entityManager.persistAndFlush(testCustomer);

        boolean exists = customerRepository.existsByEmail(testCustomer.getEmail());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when customer does not exist by email")
    void shouldReturnFalseWhenCustomerDoesNotExistByEmail() {
        boolean exists = customerRepository.existsByEmail("nonexistent@example.com");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should return true when customer exists by phone number")
    void shouldReturnTrueWhenCustomerExistsByPhoneNumber() {
        entityManager.persistAndFlush(testCustomer);

        boolean exists = customerRepository.existsByPhoneNumber(testCustomer.getPhoneNumber());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when customer does not exist by phone number")
    void shouldReturnFalseWhenCustomerDoesNotExistByPhoneNumber() {
        boolean exists = customerRepository.existsByPhoneNumber("+9999999999");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void shouldDeleteCustomerSuccessfully() {
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);
        Long customerId = savedCustomer.getId();

        customerRepository.delete(savedCustomer);
        entityManager.flush();

        Optional<Customer> deletedCustomer = customerRepository.findById(customerId);
        assertFalse(deletedCustomer.isPresent());
    }

    @Test
    @DisplayName("Should update customer successfully")
    void shouldUpdateCustomerSuccessfully() {
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);
        
        Customer updatedCustomer = Customer.builder()
                .id(savedCustomer.getId())
                .name("Updated Customer")
                .email("updated@example.com")
                .phoneNumber("+9999999999")
                .build();

        Customer result = customerRepository.save(updatedCustomer);

        assertEquals("Updated Customer", result.getName());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("+9999999999", result.getPhoneNumber());
    }

    @Test
    @DisplayName("Should handle case sensitivity in existence checks")
    void shouldHandleCaseSensitivityInExistenceChecks() {
        entityManager.persistAndFlush(testCustomer);

        boolean existsByNameLower = customerRepository.existsByName(testCustomer.getName().toLowerCase());
        boolean existsByEmailLower = customerRepository.existsByEmail(testCustomer.getEmail().toLowerCase());

        assertFalse(existsByNameLower);
        assertTrue(existsByEmailLower);
    }

    @Test
    @DisplayName("Should handle customers with same name but different emails")
    void shouldHandleCustomersWithSameNameButDifferentEmails() {
        Customer customer1 = Customer.builder()
                .name("John Doe")
                .email("john1@example.com")
                .phoneNumber("+1111111111")
                .build();
                
        Customer customer2 = Customer.builder()
                .name("John Doe")
                .email("john2@example.com")
                .phoneNumber("+2222222222")
                .build();

        entityManager.persist(customer1);
        entityManager.persist(customer2);
        entityManager.flush();

        List<Customer> customers = customerRepository.findAll();
        assertEquals(2, customers.size());
        
        assertTrue(customerRepository.existsByName("John Doe"));
        assertTrue(customerRepository.existsByEmail("john1@example.com"));
        assertTrue(customerRepository.existsByEmail("john2@example.com"));
    }
}
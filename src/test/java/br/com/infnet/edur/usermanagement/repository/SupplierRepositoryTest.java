package br.com.infnet.edur.usermanagement.repository;

import br.com.infnet.edur.usermanagement.model.Supplier;
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
@DisplayName("SupplierRepository Tests")
class SupplierRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SupplierRepository supplierRepository;

    private Supplier testSupplier;

    @BeforeEach
    void setUp() {
        testSupplier = Supplier.builder()
                .name("Tech Corp")
                .email("contact@techcorp.com")
                .phoneNumber("+1234567890")
                .build();
    }

    @Test
    @DisplayName("Should save supplier successfully")
    void shouldSaveSupplierSuccessfully() {
        Supplier savedSupplier = supplierRepository.save(testSupplier);

        assertNotNull(savedSupplier.getId());
        assertEquals(testSupplier.getName(), savedSupplier.getName());
        assertEquals(testSupplier.getEmail(), savedSupplier.getEmail());
        assertEquals(testSupplier.getPhoneNumber(), savedSupplier.getPhoneNumber());
    }

    @Test
    @DisplayName("Should find supplier by id")
    void shouldFindSupplierById() {
        Supplier savedSupplier = entityManager.persistAndFlush(testSupplier);

        Optional<Supplier> foundSupplier = supplierRepository.findById(savedSupplier.getId());

        assertTrue(foundSupplier.isPresent());
        assertEquals(savedSupplier.getId(), foundSupplier.get().getId());
        assertEquals(savedSupplier.getName(), foundSupplier.get().getName());
    }

    @Test
    @DisplayName("Should return empty when supplier not found by id")
    void shouldReturnEmptyWhenSupplierNotFoundById() {
        Optional<Supplier> foundSupplier = supplierRepository.findById(999L);

        assertFalse(foundSupplier.isPresent());
    }

    @Test
    @DisplayName("Should find all suppliers")
    void shouldFindAllSuppliers() {
        Supplier supplier1 = Supplier.builder()
                .name("Supplier 1")
                .email("supplier1@example.com")
                .phoneNumber("+1111111111")
                .build();
                
        Supplier supplier2 = Supplier.builder()
                .name("Supplier 2")
                .email("supplier2@example.com")
                .phoneNumber("+2222222222")
                .build();

        entityManager.persist(supplier1);
        entityManager.persist(supplier2);
        entityManager.flush();

        List<Supplier> suppliers = supplierRepository.findAll();

        assertEquals(2, suppliers.size());
    }

    @Test
    @DisplayName("Should return true when supplier exists by name")
    void shouldReturnTrueWhenSupplierExistsByName() {
        entityManager.persistAndFlush(testSupplier);

        boolean exists = supplierRepository.existsByName(testSupplier.getName());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when supplier does not exist by name")
    void shouldReturnFalseWhenSupplierDoesNotExistByName() {
        boolean exists = supplierRepository.existsByName("Nonexistent Supplier");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should return true when supplier exists by email")
    void shouldReturnTrueWhenSupplierExistsByEmail() {
        entityManager.persistAndFlush(testSupplier);

        boolean exists = supplierRepository.existsByEmail(testSupplier.getEmail());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when supplier does not exist by email")
    void shouldReturnFalseWhenSupplierDoesNotExistByEmail() {
        boolean exists = supplierRepository.existsByEmail("nonexistent@example.com");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should return true when supplier exists by phone number")
    void shouldReturnTrueWhenSupplierExistsByPhoneNumber() {
        entityManager.persistAndFlush(testSupplier);

        boolean exists = supplierRepository.existsByPhoneNumber(testSupplier.getPhoneNumber());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when supplier does not exist by phone number")
    void shouldReturnFalseWhenSupplierDoesNotExistByPhoneNumber() {
        boolean exists = supplierRepository.existsByPhoneNumber("+9999999999");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should delete supplier successfully")
    void shouldDeleteSupplierSuccessfully() {
        Supplier savedSupplier = entityManager.persistAndFlush(testSupplier);
        Long supplierId = savedSupplier.getId();

        supplierRepository.delete(savedSupplier);
        entityManager.flush();

        Optional<Supplier> deletedSupplier = supplierRepository.findById(supplierId);
        assertFalse(deletedSupplier.isPresent());
    }

    @Test
    @DisplayName("Should update supplier successfully")
    void shouldUpdateSupplierSuccessfully() {
        Supplier savedSupplier = entityManager.persistAndFlush(testSupplier);
        
        Supplier updatedSupplier = Supplier.builder()
                .id(savedSupplier.getId())
                .name("Updated Supplier")
                .email("updated@example.com")
                .phoneNumber("+9999999999")
                .build();

        Supplier result = supplierRepository.save(updatedSupplier);

        assertEquals("Updated Supplier", result.getName());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("+9999999999", result.getPhoneNumber());
    }

    @Test
    @DisplayName("Should handle case sensitivity in existence checks")
    void shouldHandleCaseSensitivityInExistenceChecks() {
        entityManager.persistAndFlush(testSupplier);

        boolean existsByNameLower = supplierRepository.existsByName(testSupplier.getName().toLowerCase());
        boolean existsByEmailLower = supplierRepository.existsByEmail(testSupplier.getEmail().toLowerCase());

        assertFalse(existsByNameLower);
        assertTrue(existsByEmailLower);
    }

    @Test
    @DisplayName("Should handle suppliers with same name but different contacts")
    void shouldHandleSuppliersWithSameNameButDifferentContacts() {
        Supplier supplier1 = Supplier.builder()
                .name("ABC Corp")
                .email("contact1@abccorp.com")
                .phoneNumber("+1111111111")
                .build();
                
        Supplier supplier2 = Supplier.builder()
                .name("ABC Corp")
                .email("contact2@abccorp.com")
                .phoneNumber("+2222222222")
                .build();

        entityManager.persist(supplier1);
        entityManager.persist(supplier2);
        entityManager.flush();

        List<Supplier> suppliers = supplierRepository.findAll();
        assertEquals(2, suppliers.size());
        
        assertTrue(supplierRepository.existsByName("ABC Corp"));
        assertTrue(supplierRepository.existsByEmail("contact1@abccorp.com"));
        assertTrue(supplierRepository.existsByEmail("contact2@abccorp.com"));
        assertTrue(supplierRepository.existsByPhoneNumber("+1111111111"));
        assertTrue(supplierRepository.existsByPhoneNumber("+2222222222"));
    }
}
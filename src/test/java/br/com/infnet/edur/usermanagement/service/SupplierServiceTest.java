package br.com.infnet.edur.usermanagement.service;

import br.com.infnet.edur.usermanagement.model.Supplier;
import br.com.infnet.edur.usermanagement.repository.SupplierRepository;
import br.com.infnet.edur.usermanagement.utils.exceptions.SupplierAlreadyExistsException;
import br.com.infnet.edur.usermanagement.utils.exceptions.SupplierNotFoundException;
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
@DisplayName("SupplierService Tests")
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier testSupplier;

    @BeforeEach
    void setUp() {
        testSupplier = Supplier.builder()
                .id(1L)
                .name("Tech Corp")
                .email("contact@techcorp.com")
                .phoneNumber("+1234567890")
                .build();
    }

    @Test
    @DisplayName("Should get all suppliers successfully")
    void shouldGetAllSuppliersSuccessfully() {
        Supplier supplier1 = Supplier.builder().id(1L).name("Supplier 1").email("supplier1@example.com").phoneNumber("+1111111111").build();
        Supplier supplier2 = Supplier.builder().id(2L).name("Supplier 2").email("supplier2@example.com").phoneNumber("+2222222222").build();
        List<Supplier> suppliers = Arrays.asList(supplier1, supplier2);

        when(supplierRepository.findAll()).thenReturn(suppliers);

        List<Supplier> result = supplierService.getAllSuppliers();

        assertEquals(2, result.size());
        verify(supplierRepository).findAll();
    }

    @Test
    @DisplayName("Should get supplier by id successfully")
    void shouldGetSupplierByIdSuccessfully() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));

        Supplier result = supplierService.getSupplierById(1L);

        assertEquals(testSupplier.getId(), result.getId());
        assertEquals(testSupplier.getName(), result.getName());
        assertEquals(testSupplier.getEmail(), result.getEmail());
        verify(supplierRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw SupplierNotFoundException when supplier not found by id")
    void shouldThrowSupplierNotFoundExceptionWhenSupplierNotFoundById() {
        when(supplierRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(SupplierNotFoundException.class, () -> supplierService.getSupplierById(999L));
        verify(supplierRepository).findById(999L);
    }

    @Test
    @DisplayName("Should create supplier successfully")
    void shouldCreateSupplierSuccessfully() {
        Supplier newSupplier = Supplier.builder()
                .name("New Supplier")
                .email("new@example.com")
                .phoneNumber("+9876543210")
                .build();

        when(supplierRepository.existsByEmail(anyString())).thenReturn(false);
        when(supplierRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(newSupplier);

        Supplier result = supplierService.createSupplier(newSupplier);

        assertEquals(newSupplier.getName(), result.getName());
        assertEquals(newSupplier.getEmail(), result.getEmail());
        verify(supplierRepository).existsByEmail(newSupplier.getEmail());
        verify(supplierRepository).existsByPhoneNumber(newSupplier.getPhoneNumber());
        verify(supplierRepository).save(newSupplier);
    }

    @Test
    @DisplayName("Should throw SupplierAlreadyExistsException when email exists")
    void shouldThrowSupplierAlreadyExistsExceptionWhenEmailExists() {
        when(supplierRepository.existsByEmail(testSupplier.getEmail())).thenReturn(true);

        assertThrows(SupplierAlreadyExistsException.class, () -> supplierService.createSupplier(testSupplier));
        verify(supplierRepository).existsByEmail(testSupplier.getEmail());
        verify(supplierRepository, never()).save(any(Supplier.class));
    }

    @Test
    @DisplayName("Should throw SupplierAlreadyExistsException when phone number exists")
    void shouldThrowSupplierAlreadyExistsExceptionWhenPhoneNumberExists() {
        when(supplierRepository.existsByEmail(testSupplier.getEmail())).thenReturn(false);
        when(supplierRepository.existsByPhoneNumber(testSupplier.getPhoneNumber())).thenReturn(true);

        assertThrows(SupplierAlreadyExistsException.class, () -> supplierService.createSupplier(testSupplier));
        verify(supplierRepository).existsByEmail(testSupplier.getEmail());
        verify(supplierRepository).existsByPhoneNumber(testSupplier.getPhoneNumber());
        verify(supplierRepository, never()).save(any(Supplier.class));
    }

    @Test
    @DisplayName("Should update supplier successfully")
    void shouldUpdateSupplierSuccessfully() {
        Supplier existingSupplier = Supplier.builder()
                .id(1L)
                .name("Old Supplier")
                .email("old@example.com")
                .phoneNumber("+1111111111")
                .build();

        Supplier updatedSupplierData = Supplier.builder()
                .name("Updated Supplier")
                .email("updated@example.com")
                .phoneNumber("+9999999999")
                .build();

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(existingSupplier));
        when(supplierRepository.existsByName("Updated Supplier")).thenReturn(false);
        when(supplierRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(supplierRepository.existsByPhoneNumber("+9999999999")).thenReturn(false);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(updatedSupplierData);

        Supplier result = supplierService.updateSupplier(1L, updatedSupplierData);

        assertEquals("Updated Supplier", result.getName());
        assertEquals("updated@example.com", result.getEmail());
        verify(supplierRepository).findById(1L);
        verify(supplierRepository).save(any(Supplier.class));
    }

    @Test
    @DisplayName("Should throw SupplierAlreadyExistsException when updating with existing name")
    void shouldThrowSupplierAlreadyExistsExceptionWhenUpdatingWithExistingName() {
        Supplier existingSupplier = Supplier.builder()
                .id(1L)
                .name("Original Supplier")
                .email("original@example.com")
                .phoneNumber("+1111111111")
                .build();

        Supplier updatedSupplierData = Supplier.builder()
                .name("Existing Supplier")
                .email("original@example.com")
                .phoneNumber("+1111111111")
                .build();

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(existingSupplier));
        when(supplierRepository.existsByName("Existing Supplier")).thenReturn(true);

        assertThrows(SupplierAlreadyExistsException.class, () -> supplierService.updateSupplier(1L, updatedSupplierData));
        verify(supplierRepository).findById(1L);
        verify(supplierRepository).existsByName("Existing Supplier");
        verify(supplierRepository, never()).save(any(Supplier.class));
    }

    @Test
    @DisplayName("Should delete supplier successfully")
    void shouldDeleteSupplierSuccessfully() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));

        supplierService.deleteSupplier(1L);

        verify(supplierRepository).findById(1L);
        verify(supplierRepository).delete(testSupplier);
    }

    @Test
    @DisplayName("Should throw SupplierNotFoundException when deleting non-existent supplier")
    void shouldThrowSupplierNotFoundExceptionWhenDeletingNonExistentSupplier() {
        when(supplierRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(SupplierNotFoundException.class, () -> supplierService.deleteSupplier(999L));
        verify(supplierRepository).findById(999L);
        verify(supplierRepository, never()).delete(any(Supplier.class));
    }

    @Test
    @DisplayName("Should return true when supplier exists by name")
    void shouldReturnTrueWhenSupplierExistsByName() {
        when(supplierRepository.existsByName("Tech Corp")).thenReturn(true);

        boolean result = supplierService.existsByName("Tech Corp");

        assertTrue(result);
        verify(supplierRepository).existsByName("Tech Corp");
    }

    @Test
    @DisplayName("Should return true when supplier exists by email")
    void shouldReturnTrueWhenSupplierExistsByEmail() {
        when(supplierRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = supplierService.existsByEmail("test@example.com");

        assertTrue(result);
        verify(supplierRepository).existsByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should return true when supplier exists by phone number")
    void shouldReturnTrueWhenSupplierExistsByPhoneNumber() {
        when(supplierRepository.existsByPhoneNumber("+1234567890")).thenReturn(true);

        boolean result = supplierService.existsByPhoneNumber("+1234567890");

        assertTrue(result);
        verify(supplierRepository).existsByPhoneNumber("+1234567890");
    }

    @Test
    @DisplayName("Should allow updating supplier with same values")
    void shouldAllowUpdatingSupplierWithSameValues() {
        Supplier existingSupplier = Supplier.builder()
                .id(1L)
                .name("Same Supplier")
                .email("same@example.com")
                .phoneNumber("+1111111111")
                .build();

        Supplier updatedSupplierData = Supplier.builder()
                .name("Same Supplier")
                .email("same@example.com")
                .phoneNumber("+1111111111")
                .build();

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(existingSupplier));
        when(supplierRepository.save(any(Supplier.class))).thenReturn(updatedSupplierData);

        Supplier result = supplierService.updateSupplier(1L, updatedSupplierData);

        assertEquals("Same Supplier", result.getName());
        verify(supplierRepository).findById(1L);
        verify(supplierRepository).save(any(Supplier.class));
        verify(supplierRepository, never()).existsByName(anyString());
        verify(supplierRepository, never()).existsByEmail(anyString());
        verify(supplierRepository, never()).existsByPhoneNumber(anyString());
    }
}
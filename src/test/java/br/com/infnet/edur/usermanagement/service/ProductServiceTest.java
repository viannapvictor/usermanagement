package br.com.infnet.edur.usermanagement.service;

import br.com.infnet.edur.usermanagement.model.Product;
import br.com.infnet.edur.usermanagement.repository.ProductRepository;
import br.com.infnet.edur.usermanagement.utils.exceptions.ProductAlreadyExistsException;
import br.com.infnet.edur.usermanagement.utils.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .unitPrice(new BigDecimal("19.99"))
                .build();
    }

    @Test
    @DisplayName("Should get all products successfully")
    void shouldGetAllProductsSuccessfully() {
        Product product1 = Product.builder().id(1L).name("Product 1").unitPrice(new BigDecimal("10.00")).build();
        Product product2 = Product.builder().id(2L).name("Product 2").unitPrice(new BigDecimal("20.00")).build();
        List<Product> products = Arrays.asList(product1, product2);

        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertEquals(2, result.size());
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should get product by id successfully")
    void shouldGetProductByIdSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Product result = productService.getProductById(1L);

        assertEquals(testProduct.getId(), result.getId());
        assertEquals(testProduct.getName(), result.getName());
        assertEquals(testProduct.getUnitPrice(), result.getUnitPrice());
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product not found by id")
    void shouldThrowProductNotFoundExceptionWhenProductNotFoundById() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(999L));
        verify(productRepository).findById(999L);
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() {
        Product newProduct = Product.builder()
                .name("New Product")
                .unitPrice(new BigDecimal("29.99"))
                .build();

        when(productRepository.existsByName(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(newProduct);

        Product result = productService.createProduct(newProduct);

        assertEquals(newProduct.getName(), result.getName());
        assertEquals(newProduct.getUnitPrice(), result.getUnitPrice());
        verify(productRepository).existsByName(newProduct.getName());
        verify(productRepository).save(newProduct);
    }

    @Test
    @DisplayName("Should throw ProductAlreadyExistsException when name exists")
    void shouldThrowProductAlreadyExistsExceptionWhenNameExists() {
        when(productRepository.existsByName(testProduct.getName())).thenReturn(true);

        assertThrows(ProductAlreadyExistsException.class, () -> productService.createProduct(testProduct));
        verify(productRepository).existsByName(testProduct.getName());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() {
        Product existingProduct = Product.builder()
                .id(1L)
                .name("Old Product")
                .unitPrice(new BigDecimal("10.00"))
                .build();

        Product updatedProductData = Product.builder()
                .name("Updated Product")
                .unitPrice(new BigDecimal("25.00"))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByName("Updated Product")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProductData);

        Product result = productService.updateProduct(1L, updatedProductData);

        assertEquals("Updated Product", result.getName());
        assertEquals(new BigDecimal("25.00"), result.getUnitPrice());
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ProductAlreadyExistsException when updating with existing name")
    void shouldThrowProductAlreadyExistsExceptionWhenUpdatingWithExistingName() {
        Product existingProduct = Product.builder()
                .id(1L)
                .name("Original Product")
                .unitPrice(new BigDecimal("10.00"))
                .build();

        Product updatedProductData = Product.builder()
                .name("Existing Product")
                .unitPrice(new BigDecimal("20.00"))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByName("Existing Product")).thenReturn(true);

        assertThrows(ProductAlreadyExistsException.class, () -> productService.updateProduct(1L, updatedProductData));
        verify(productRepository).findById(1L);
        verify(productRepository).existsByName("Existing Product");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should allow updating product with same name")
    void shouldAllowUpdatingProductWithSameName() {
        Product existingProduct = Product.builder()
                .id(1L)
                .name("Same Product")
                .unitPrice(new BigDecimal("10.00"))
                .build();

        Product updatedProductData = Product.builder()
                .name("Same Product")
                .unitPrice(new BigDecimal("15.00"))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProductData);

        Product result = productService.updateProduct(1L, updatedProductData);

        assertEquals("Same Product", result.getName());
        assertEquals(new BigDecimal("15.00"), result.getUnitPrice());
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
        verify(productRepository, never()).existsByName(anyString());
    }

    @Test
    @DisplayName("Should delete product successfully")
    void shouldDeleteProductSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        productService.deleteProduct(1L);

        verify(productRepository).findById(1L);
        verify(productRepository).delete(testProduct);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when deleting non-existent product")
    void shouldThrowProductNotFoundExceptionWhenDeletingNonExistentProduct() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(999L));
        verify(productRepository).findById(999L);
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    @DisplayName("Should return true when product exists by name")
    void shouldReturnTrueWhenProductExistsByName() {
        when(productRepository.existsByName("Test Product")).thenReturn(true);

        boolean result = productService.existsByName("Test Product");

        assertTrue(result);
        verify(productRepository).existsByName("Test Product");
    }

    @Test
    @DisplayName("Should return false when product does not exist by name")
    void shouldReturnFalseWhenProductDoesNotExistByName() {
        when(productRepository.existsByName("Nonexistent Product")).thenReturn(false);

        boolean result = productService.existsByName("Nonexistent Product");

        assertFalse(result);
        verify(productRepository).existsByName("Nonexistent Product");
    }
}
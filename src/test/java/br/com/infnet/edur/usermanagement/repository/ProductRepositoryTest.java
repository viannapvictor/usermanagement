package br.com.infnet.edur.usermanagement.repository;

import br.com.infnet.edur.usermanagement.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("ProductRepository Tests")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .name("Test Product")
                .unitPrice(new BigDecimal("19.99"))
                .build();
    }

    @Test
    @DisplayName("Should save product successfully")
    void shouldSaveProductSuccessfully() {
        Product savedProduct = productRepository.save(testProduct);

        assertNotNull(savedProduct.getId());
        assertEquals(testProduct.getName(), savedProduct.getName());
        assertEquals(testProduct.getUnitPrice(), savedProduct.getUnitPrice());
    }

    @Test
    @DisplayName("Should find product by id")
    void shouldFindProductById() {
        Product savedProduct = entityManager.persistAndFlush(testProduct);

        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        assertTrue(foundProduct.isPresent());
        assertEquals(savedProduct.getId(), foundProduct.get().getId());
        assertEquals(savedProduct.getName(), foundProduct.get().getName());
        assertEquals(savedProduct.getUnitPrice(), foundProduct.get().getUnitPrice());
    }

    @Test
    @DisplayName("Should return empty when product not found by id")
    void shouldReturnEmptyWhenProductNotFoundById() {
        Optional<Product> foundProduct = productRepository.findById(999L);

        assertFalse(foundProduct.isPresent());
    }

    @Test
    @DisplayName("Should find all products")
    void shouldFindAllProducts() {
        Product product1 = Product.builder()
                .name("Product 1")
                .unitPrice(new BigDecimal("10.00"))
                .build();
                
        Product product2 = Product.builder()
                .name("Product 2")
                .unitPrice(new BigDecimal("20.00"))
                .build();

        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.flush();

        List<Product> products = productRepository.findAll();

        assertEquals(2, products.size());
    }

    @Test
    @DisplayName("Should return true when product exists by name")
    void shouldReturnTrueWhenProductExistsByName() {
        entityManager.persistAndFlush(testProduct);

        boolean exists = productRepository.existsByName(testProduct.getName());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when product does not exist by name")
    void shouldReturnFalseWhenProductDoesNotExistByName() {
        boolean exists = productRepository.existsByName("Nonexistent Product");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should delete product successfully")
    void shouldDeleteProductSuccessfully() {
        Product savedProduct = entityManager.persistAndFlush(testProduct);
        Long productId = savedProduct.getId();

        productRepository.delete(savedProduct);
        entityManager.flush();

        Optional<Product> deletedProduct = productRepository.findById(productId);
        assertFalse(deletedProduct.isPresent());
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() {
        Product savedProduct = entityManager.persistAndFlush(testProduct);
        
        Product updatedProduct = Product.builder()
                .id(savedProduct.getId())
                .name("Updated Product")
                .unitPrice(new BigDecimal("29.99"))
                .build();

        Product result = productRepository.save(updatedProduct);

        assertEquals("Updated Product", result.getName());
        assertEquals(new BigDecimal("29.99"), result.getUnitPrice());
    }

    @Test
    @DisplayName("Should handle case sensitivity in name existence check")
    void shouldHandleCaseSensitivityInNameExistenceCheck() {
        entityManager.persistAndFlush(testProduct);

        boolean existsLowerCase = productRepository.existsByName(testProduct.getName().toLowerCase());
        boolean existsUpperCase = productRepository.existsByName(testProduct.getName().toUpperCase());

        assertFalse(existsLowerCase);
        assertFalse(existsUpperCase);
    }

    @Test
    @DisplayName("Should handle products with same price but different names")
    void shouldHandleProductsWithSamePriceButDifferentNames() {
        BigDecimal samePrice = new BigDecimal("15.99");
        
        Product product1 = Product.builder()
                .name("Product A")
                .unitPrice(samePrice)
                .build();
                
        Product product2 = Product.builder()
                .name("Product B")
                .unitPrice(samePrice)
                .build();

        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.flush();

        List<Product> products = productRepository.findAll();
        assertEquals(2, products.size());
        
        assertTrue(productRepository.existsByName("Product A"));
        assertTrue(productRepository.existsByName("Product B"));
        assertFalse(productRepository.existsByName("Product C"));
    }

    @Test
    @DisplayName("Should preserve decimal precision in unit price")
    void shouldPreserveDecimalPrecisionInUnitPrice() {
        BigDecimal precisePrice = new BigDecimal("123.456789");
        Product product = Product.builder()
                .name("Precise Product")
                .unitPrice(precisePrice)
                .build();

        Product savedProduct = productRepository.save(product);
        entityManager.flush();

        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        assertTrue(foundProduct.isPresent());
        assertEquals(0, precisePrice.compareTo(foundProduct.get().getUnitPrice()));
    }
}
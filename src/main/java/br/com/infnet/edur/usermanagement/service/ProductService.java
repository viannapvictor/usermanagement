package br.com.infnet.edur.usermanagement.service;

import br.com.infnet.edur.usermanagement.model.Product;
import br.com.infnet.edur.usermanagement.repository.ProductRepository;
import br.com.infnet.edur.usermanagement.utils.exceptions.ProductAlreadyExistsException;
import br.com.infnet.edur.usermanagement.utils.exceptions.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Product createProduct(Product product) {
        if (existsByName(product.getName())) {
            throw new ProductAlreadyExistsException("name", product.getName());
        }
        
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product product) {
        Product existingProduct = getProductById(id);
        
        if (!existingProduct.getName().equals(product.getName()) && existsByName(product.getName())) {
            throw new ProductAlreadyExistsException("name", product.getName());
        }
        
        Product updatedProduct = Product.builder()
                .id(id)
                .name(product.getName())
                .unitPrice(product.getUnitPrice())
                .build();
        
        return productRepository.save(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }
}
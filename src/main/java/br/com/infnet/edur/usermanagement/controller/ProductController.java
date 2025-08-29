package br.com.infnet.edur.usermanagement.controller;

import br.com.infnet.edur.usermanagement.dto.reponse.APIResponse;
import br.com.infnet.edur.usermanagement.dto.request.ProductInputDTO;
import br.com.infnet.edur.usermanagement.service.ProductService;
import br.com.infnet.edur.usermanagement.model.Product;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public ResponseEntity<APIResponse<List<Product>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        APIResponse<List<Product>> response = APIResponse.success(products);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Product>> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        APIResponse<Product> response = APIResponse.success(product);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<APIResponse<Product>> createProduct(@Valid @RequestBody ProductInputDTO productInputDTO) {
        Product product = new Product(productInputDTO.getName(), productInputDTO.getUnitPrice());
        Product createdProduct = productService.createProduct(product);
        APIResponse<Product> response = APIResponse.success(createdProduct, HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<Product>> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductInputDTO productInputDTO) {
        Product product = new Product(productInputDTO.getName(), productInputDTO.getUnitPrice());
        Product updatedProduct = productService.updateProduct(id, product);
        APIResponse<Product> response = APIResponse.success(updatedProduct);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        APIResponse<Void> response = APIResponse.success(null, HttpStatus.NO_CONTENT.value());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
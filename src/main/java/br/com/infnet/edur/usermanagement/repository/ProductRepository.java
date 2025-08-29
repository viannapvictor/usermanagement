package br.com.infnet.edur.usermanagement.repository;

import br.com.infnet.edur.usermanagement.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    boolean existsByName(String name);
}
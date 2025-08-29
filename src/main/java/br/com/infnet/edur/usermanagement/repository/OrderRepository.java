package br.com.infnet.edur.usermanagement.repository;

import br.com.infnet.edur.usermanagement.model.Order;
import br.com.infnet.edur.usermanagement.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByCustomer(Customer customer);
    
    List<Order> findByCustomerId(Long customerId);
}
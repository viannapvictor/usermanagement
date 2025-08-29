package br.com.infnet.edur.usermanagement.repository;

import br.com.infnet.edur.usermanagement.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
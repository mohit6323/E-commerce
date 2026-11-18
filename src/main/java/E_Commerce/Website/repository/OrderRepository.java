package E_Commerce.Website.repository;

import E_Commerce.Website.entity.Order;
import E_Commerce.Website.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    // For admin: get all orders (already present via JpaRepository)
    Optional<Order> findById(Long id);
}
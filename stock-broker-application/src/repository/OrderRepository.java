package repository;

import entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(String orderId);
    List<Order> findByUserId(String userId);
}

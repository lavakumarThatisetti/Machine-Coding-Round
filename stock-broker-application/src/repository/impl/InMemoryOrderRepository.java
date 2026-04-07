package repository.impl;

import entity.Order;
import repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class InMemoryOrderRepository implements OrderRepository {
    private final ConcurrentMap<String, Order> orders = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, List<Order>> userOrdersMap = new ConcurrentHashMap<>();

    @Override
    public void save(Order order) {
        orders.put(order.getOrderId(), order);
        userOrdersMap
                .computeIfAbsent(order.getUserId(), key -> new CopyOnWriteArrayList<>())
                .add(order);
    }

    @Override
    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public List<Order> findByUserId(String userId) {
        return List.copyOf(userOrdersMap.getOrDefault(userId, List.of()));
    }
}

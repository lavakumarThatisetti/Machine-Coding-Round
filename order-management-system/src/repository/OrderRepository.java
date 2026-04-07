package repository;

import model.Order;

import java.util.concurrent.ConcurrentHashMap;

public class OrderRepository {
    ConcurrentHashMap<String, Order> orderMap;

    public OrderRepository() {
        this.orderMap = new ConcurrentHashMap<>();
    }


    public void save(Order order) {
        orderMap.put(order.getOrderId(), order);
    }


    public Order get(String orderId) {
        return orderMap.get(orderId);
    }
}

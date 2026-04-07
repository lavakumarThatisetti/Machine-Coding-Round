package model;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;

public class PriceLevel {
    private final BigDecimal price;
    private final Deque<Order> orders;

    public PriceLevel(BigDecimal price) {
        this.price = price;
        this.orders = new ArrayDeque<>();
    }

    public BigDecimal getPrice() { return price; }

    public void add(Order order) {
        orders.addLast(order);
    }

    public Order peek() {
        return orders.peekFirst();
    }

    public Order poll() {
        return orders.pollFirst();
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }

    public int size() {
        return orders.size();
    }
}

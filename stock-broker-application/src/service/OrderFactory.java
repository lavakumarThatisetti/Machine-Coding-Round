package service;

import model.Order;
import model.OrderSide;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OrderFactory {
    private final AtomicLong sequenceGenerator = new AtomicLong(0);

    public Order createOrder(String userId,
                             String stockId,
                             OrderSide side,
                             BigDecimal price,
                             int quantity) {
        return new Order(
                UUID.randomUUID().toString(),
                userId,
                stockId,
                side,
                price,
                quantity,
                sequenceGenerator.incrementAndGet(),
                Instant.now()
        );
    }
}
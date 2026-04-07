package model;

import java.math.BigDecimal;
import java.time.Instant;

public class Order {
    private final String orderId;
    private final String userId;
    private final String stockId;
    private final OrderSide side;
    private final BigDecimal price;
    private final int originalQuantity;
    private int remainingQuantity;
    private final long sequenceNumber; // to maintain FCFS
    private final Instant createdAt;
    private OrderStatus status;

    public Order(
            String orderId,
            String userId,
            String stockId,
            OrderSide side,
            BigDecimal price,
            int quantity,
            long sequenceNumber,
            Instant createdAt) {

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        this.orderId = orderId;
        this.userId = userId;
        this.stockId = stockId;
        this.side = side;
        this.price = price;
        this.originalQuantity = quantity;
        this.remainingQuantity = quantity;
        this.sequenceNumber = sequenceNumber;
        this.createdAt = createdAt;
        this.status = OrderStatus.OPEN;
    }

    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public String getStockId() { return stockId; }
    public OrderSide getSide() { return side; }
    public BigDecimal getPrice() { return price; }
    public int getOriginalQuantity() { return originalQuantity; }
    public int getRemainingQuantity() { return remainingQuantity; }
    public long getSequenceNumber() { return sequenceNumber; }
    public Instant getCreatedAt() { return createdAt; }
    public OrderStatus getStatus() { return status; }

    public boolean isOpen() {
        return status == OrderStatus.OPEN || status == OrderStatus.PARTIALLY_FILLED;
    }

    public void fill(int qty) {
        if (qty <= 0) throw new IllegalArgumentException("qty must be positive");
        if (!isOpen()) throw new IllegalStateException("Order is not open");
        if (remainingQuantity < qty) throw new IllegalStateException("Insufficient remaining quantity");

        remainingQuantity -= qty;

        if (remainingQuantity == 0) {
            status = OrderStatus.FILLED;
        } else {
            status = OrderStatus.PARTIALLY_FILLED;
        }
    }

    public void cancel() {
        if (!isOpen()) {
            throw new IllegalStateException("Only open orders can be cancelled");
        }
        status = OrderStatus.CANCELLED;
    }
}

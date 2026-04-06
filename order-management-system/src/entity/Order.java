package entity;

public class Order {
    private final String orderId;
    private final String productId;
    private final int quantity;
    private OrderStatus status;
    private String reservationId;

    public Order(String orderId, String productId, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = OrderStatus.INITIATED;
    }

    public void markReserved(String reservationId) {
        this.reservationId = reservationId;
        this.status = OrderStatus.RESERVED;
    }

    public void markConfirmed() {
        this.status = OrderStatus.CONFIRMED;
    }

    public void markCancelled() {
        this.status = OrderStatus.CANCELLED;
    }

    public void markExpired() {
        this.status = OrderStatus.EXPIRED;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getReservationId() {
        return reservationId;
    }

    @Override
    public String toString() {
        return "OrderId "+this.orderId;
    }
}

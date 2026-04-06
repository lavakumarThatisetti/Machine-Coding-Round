package entity;

public class Reservation {
    private final String reservationId;
    private final String orderId;
    private final String productId;
    private final int quantity;
    private final long createdAt;
    private final long expiresAt;

    private ReservationStatus status;

    public Reservation(String reservationId,
                       String orderId,
                       String productId,
                       int quantity,
                       long createdAt,
                       long expiresAt) {
        this.reservationId = reservationId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.status = ReservationStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == ReservationStatus.ACTIVE;
    }

    public void markConfirmed() {
        if (status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Reservation is not active");
        }
        status = ReservationStatus.CONFIRMED;
    }

    public void markCancelled() {
        if (status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Reservation is not active");
        }
        status = ReservationStatus.CANCELLED;
    }

    public void markExpired() {
        if (status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Reservation is not active");
        }
        status = ReservationStatus.EXPIRED;
    }

    public String getReservationId() {
        return reservationId;
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

    public ReservationStatus getStatus() {
        return status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }
}

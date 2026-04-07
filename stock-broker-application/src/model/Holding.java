package model;

public class Holding {
    private final String stockId;
    private int availableQuantity;
    private int reservedQuantity;

    public Holding(String stockId, int availableQuantity, int reservedQuantity) {
        this.stockId = stockId;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
    }

    public String getStockId() { return stockId; }
    public int getAvailableQuantity() { return availableQuantity; }
    public int getReservedQuantity() { return reservedQuantity; }

    public void reserve(int qty) {
        if (qty <= 0) throw new IllegalArgumentException("qty must be positive");
        if (availableQuantity < qty) throw new IllegalStateException("Insufficient holdings");
        availableQuantity -= qty;
        reservedQuantity += qty;
    }

    public void release(int qty) {
        if (qty <= 0) throw new IllegalArgumentException("qty must be positive");
        if (reservedQuantity < qty) throw new IllegalStateException("Insufficient reserved holdings");
        reservedQuantity -= qty;
        availableQuantity += qty;
    }

    public void consumeReserved(int qty) {
        if (qty <= 0) throw new IllegalArgumentException("qty must be positive");
        if (reservedQuantity < qty) throw new IllegalStateException("Insufficient reserved holdings");
        reservedQuantity -= qty;
    }

    public void addAvailable(int qty) {
        if (qty <= 0) throw new IllegalArgumentException("qty must be positive");
        availableQuantity += qty;
    }
}

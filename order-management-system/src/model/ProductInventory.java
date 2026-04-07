package model;

import exception.InsufficientInventoryException;

public class ProductInventory {
    private final String productId;
    private final int totalStock;
    private int reservedStock;
    private int soldStock;

    public ProductInventory(String productId, int totalStock) {
        this.productId = productId;
        this.totalStock = totalStock;
    }

    public int getAvailableStock() {
        return totalStock - reservedStock - soldStock;
    }

    public void reserve(int qty) {
        if (getAvailableStock() < qty) {
            throw new InsufficientInventoryException(productId);
        }
        reservedStock += qty;
    }

    public void confirm(int qty) {
        if (reservedStock < qty) {
            throw new IllegalStateException("Reserved stock less than confirm qty");
        }
        reservedStock -= qty;
        soldStock += qty;
    }

    public void release(int qty) {
        if (reservedStock < qty) {
            throw new IllegalStateException("Reserved stock less than release qty");
        }
        reservedStock -= qty;
    }

    public String getProductId() {
        return productId;
    }
}
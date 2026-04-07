package dto;

import model.OrderSide;

import java.math.BigDecimal;

public class PlaceOrderRequest {
    private final String userId;
    private final String stockId;
    private final OrderSide side;
    private final BigDecimal price;
    private final int quantity;

    public PlaceOrderRequest(String userId, String stockId, OrderSide side, BigDecimal price, int quantity) {
        this.userId = userId;
        this.stockId = stockId;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
    }

    public String getUserId() {
        return userId;
    }

    public String getStockId() {
        return stockId;
    }

    public OrderSide getSide() {
        return side;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}

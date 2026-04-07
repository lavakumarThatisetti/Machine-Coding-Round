package dto;

import entity.OrderStatus;
import entity.Trade;

import java.util.List;

public class PlaceOrderResult {
    private final String orderId;
    private final OrderStatus orderStatus;
    private final int remainingQuantity;
    private final List<Trade> executedTrades;

    public PlaceOrderResult(String orderId, OrderStatus orderStatus, int remainingQuantity, List<Trade> executedTrades) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.remainingQuantity = remainingQuantity;
        this.executedTrades = executedTrades;
    }

    public String getOrderId() { return orderId; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public int getRemainingQuantity() { return remainingQuantity; }
    public List<Trade> getExecutedTrades() { return executedTrades; }
}
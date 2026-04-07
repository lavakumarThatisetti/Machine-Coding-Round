package model;

import java.math.BigDecimal;
import java.time.Instant;

public class Trade {
    private final String tradeId;
    private final String buyOrderId;
    private final String sellOrderId;
    private final String buyerUserId;
    private final String sellerUserId;
    private final String stockId;
    private final BigDecimal executionPrice;
    private final int quantity;
    private final Instant executedAt;

    public Trade(
            String tradeId,
            String buyOrderId,
            String sellOrderId,
            String buyerUserId,
            String sellerUserId,
            String stockId,
            BigDecimal executionPrice,
            int quantity,
            Instant executedAt) {
        this.tradeId = tradeId;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.buyerUserId = buyerUserId;
        this.sellerUserId = sellerUserId;
        this.stockId = stockId;
        this.executionPrice = executionPrice;
        this.quantity = quantity;
        this.executedAt = executedAt;
    }

    public String getTradeId() { return tradeId; }
    public String getBuyOrderId() { return buyOrderId; }
    public String getSellOrderId() { return sellOrderId; }
    public String getBuyerUserId() { return buyerUserId; }
    public String getSellerUserId() { return sellerUserId; }
    public String getStockId() { return stockId; }
    public BigDecimal getExecutionPrice() { return executionPrice; }
    public int getQuantity() { return quantity; }
    public Instant getExecutedAt() { return executedAt; }
}

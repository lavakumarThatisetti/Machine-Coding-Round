package repository;

import entity.Trade;

import java.util.List;

public interface TradeRepository {
    void save(Trade trade);
    List<Trade> findByUserId(String userId);
    List<Trade> findByStockId(String stockId);
}

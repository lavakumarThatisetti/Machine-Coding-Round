package repository;

import entity.OrderBook;

import java.util.Optional;

public interface OrderBookRepository {
    OrderBook getOrCreate(String stockId);
    Optional<OrderBook> findByStockId(String stockId);
}

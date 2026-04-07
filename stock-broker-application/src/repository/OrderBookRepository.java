package repository;

import model.OrderBook;

import java.util.Optional;

public interface OrderBookRepository {
    OrderBook getOrCreate(String stockId);
    Optional<OrderBook> findByStockId(String stockId);
}

package repository;

import entity.Stock;

import java.util.Optional;

public interface StockRepository {
    void save(Stock stock);
    Optional<Stock> findById(String stockId);
    boolean existsById(String stockId);
}

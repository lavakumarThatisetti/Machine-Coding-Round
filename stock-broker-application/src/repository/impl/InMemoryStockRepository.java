package repository.impl;

import entity.Stock;
import repository.StockRepository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryStockRepository implements StockRepository {
    private final ConcurrentMap<String, Stock> stocks = new ConcurrentHashMap<>();

    @Override
    public void save(Stock stock) {
        stocks.put(stock.getStockId(), stock);
    }

    @Override
    public Optional<Stock> findById(String stockId) {
        return Optional.of(stocks.get(stockId));
    }

    @Override
    public boolean existsById(String stockId) {
        return stocks.containsKey(stockId);
    }
}

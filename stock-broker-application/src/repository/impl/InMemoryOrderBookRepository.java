package repository.impl;

import model.OrderBook;
import repository.OrderBookRepository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryOrderBookRepository implements OrderBookRepository {
    private final ConcurrentMap<String, OrderBook> orderBook = new ConcurrentHashMap<>();

    @Override
    public OrderBook getOrCreate(String stockId) {
        return orderBook.computeIfAbsent(stockId, k-> new OrderBook(stockId));
    }

    @Override
    public Optional<OrderBook> findByStockId(String stockId) {
        return Optional.of(orderBook.get(stockId));
    }
}

package service;

import model.Stock;
import repository.OrderBookRepository;
import repository.StockRepository;

public class StockService {
    private final StockRepository stockRepository;
    private final OrderBookRepository orderBookRepository;

    public StockService(StockRepository stockRepository, OrderBookRepository orderBookRepository) {
        this.stockRepository = stockRepository;
        this.orderBookRepository = orderBookRepository;
    }

    public void addStock(String stockId, String symbol, String name) {
        if (stockRepository.existsById(stockId)) {
            throw new IllegalStateException("Stock already exists");
        }
        stockRepository.save(new Stock(stockId, symbol, name));
        orderBookRepository.getOrCreate(stockId);
    }
}

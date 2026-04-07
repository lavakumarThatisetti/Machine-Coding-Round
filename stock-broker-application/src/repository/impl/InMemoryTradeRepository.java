package repository.impl;

import entity.Trade;
import repository.TradeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryTradeRepository implements TradeRepository {
    private final ConcurrentMap<String, Trade> trades = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, List<Trade>> userTradesMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, List<Trade>> stockTradeMap = new ConcurrentHashMap<>();


    @Override
    public void save(Trade trade) {
        trades.put(trade.getTradeId(), trade);
        userTradesMap
                .computeIfAbsent(trade.getBuyerUserId(), key -> new CopyOnWriteArrayList<>())
                .add(trade);
        userTradesMap
                .computeIfAbsent(trade.getSellerUserId(), key -> new CopyOnWriteArrayList<>())
                .add(trade);
        stockTradeMap
                .computeIfAbsent(trade.getStockId(), key -> new CopyOnWriteArrayList<>())
                .add(trade);
    }

    @Override
    public List<Trade> findByUserId(String userId) {
        return new ArrayList<>(userTradesMap.getOrDefault(userId, List.of()));
    }

    @Override
    public List<Trade> findByStockId(String stockId) {
        return new ArrayList<>(stockTradeMap.getOrDefault(stockId, List.of()));
    }
}

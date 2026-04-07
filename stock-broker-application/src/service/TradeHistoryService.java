package service;

import entity.Trade;
import repository.TradeRepository;

import java.util.List;

public class TradeHistoryService {
    private final TradeRepository tradeRepository;

    public TradeHistoryService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    public List<Trade> getUserTradeHistory(String userId) {
        return tradeRepository.findByUserId(userId);
    }
}

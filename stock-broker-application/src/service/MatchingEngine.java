package service;

import entity.Order;
import entity.OrderBook;
import entity.Trade;
import repository.TradeRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MatchingEngine {
    private final SettlementService settlementService;
    private final TradeRepository tradeRepository;

    public MatchingEngine(SettlementService settlementService, TradeRepository tradeRepository) {
        this.settlementService = settlementService;
        this.tradeRepository = tradeRepository;
    }

    public List<Trade> match(OrderBook orderBook) {
        List<Trade> executedTrades = new ArrayList<>();

        while (true) {
            orderBook.removeBestBuyIfFilledOrInactive();
            orderBook.removeBestSellIfFilledOrInactive();

            Order bestBuy = orderBook.peekBestBuy();
            Order bestSell = orderBook.peekBestSell();

            if (bestBuy == null || bestSell == null) {
                break;
            }

            if (bestBuy.getPrice().compareTo(bestSell.getPrice()) < 0) {
                break;
            }

            int tradeQty = Math.min(bestBuy.getRemainingQuantity(), bestSell.getRemainingQuantity());
            BigDecimal executionPrice = bestSell.getPrice();

            settlementService.settle(bestBuy, bestSell, tradeQty, executionPrice);

            bestBuy.fill(tradeQty);
            bestSell.fill(tradeQty);

            Trade trade = new Trade(
                    UUID.randomUUID().toString(),
                    bestBuy.getOrderId(),
                    bestSell.getOrderId(),
                    bestBuy.getUserId(),
                    bestSell.getUserId(),
                    bestBuy.getStockId(),
                    executionPrice,
                    tradeQty,
                    Instant.now()
            );

            tradeRepository.save(trade);
            executedTrades.add(trade);
        }

        return executedTrades;
    }
}

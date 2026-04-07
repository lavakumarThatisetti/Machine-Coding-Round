package entity;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

public class OrderBook {
    private final String stockId;
    private final NavigableMap<BigDecimal, PriceLevel> buyLevels;
    private final NavigableMap<BigDecimal, PriceLevel> sellLevels;
    private final ReentrantLock lock;

    public OrderBook(String stockId) {
        this.stockId = stockId;
        this.buyLevels = new TreeMap<>(Comparator.reverseOrder());
        this.sellLevels = new TreeMap<>();
        this.lock = new ReentrantLock();
    }

    public String getStockId() { return stockId; }
    public ReentrantLock getLock() { return lock; }

    public void addOrder(Order order) {
        NavigableMap<BigDecimal, PriceLevel> sideMap = order.getSide() == OrderSide.BUY ? buyLevels : sellLevels;
        sideMap.computeIfAbsent(order.getPrice(), PriceLevel::new).add(order);
    }

    public Order peekBestBuy() {
        Map.Entry<BigDecimal, PriceLevel> entry = buyLevels.firstEntry();
        return entry == null ? null : entry.getValue().peek();
    }

    public Order peekBestSell() {
        Map.Entry<BigDecimal, PriceLevel> entry = sellLevels.firstEntry();
        return entry == null ? null : entry.getValue().peek();
    }

    public void removeBestBuyIfFilledOrInactive() {
        cleanupTop(buyLevels);
    }

    public void removeBestSellIfFilledOrInactive() {
        cleanupTop(sellLevels);
    }

    private void cleanupTop(NavigableMap<BigDecimal, PriceLevel> levels) {
        while (!levels.isEmpty()) {
            Map.Entry<BigDecimal, PriceLevel> entry = levels.firstEntry();
            PriceLevel priceLevel = entry.getValue();
            Order head = priceLevel.peek();

            if (head == null) {
                levels.remove(entry.getKey());
                continue;
            }

            if (head.getStatus() == OrderStatus.FILLED || head.getStatus() == OrderStatus.CANCELLED) {
                priceLevel.poll();
                if (priceLevel.isEmpty()) {
                    levels.remove(entry.getKey());
                }
                continue;
            }

            break;
        }
    }
}

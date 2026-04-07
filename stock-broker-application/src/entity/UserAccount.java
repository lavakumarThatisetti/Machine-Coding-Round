package entity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserAccount {
    private final String userId;
    private final String name;

    private BigDecimal availableCash;
    private BigDecimal reservedCash;

    private final Map<String, Holding> holdings;

    public UserAccount(String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.availableCash = BigDecimal.ZERO;
        this.reservedCash = BigDecimal.ZERO;
        this.holdings = new HashMap<>();
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public BigDecimal getAvailableCash() { return availableCash; }
    public BigDecimal getReservedCash() { return reservedCash; }

    public Map<String, Holding> getHoldingsView() {
        return Collections.unmodifiableMap(holdings);
    }

    public void addCash(BigDecimal amount) {
        validateAmount(amount);
        availableCash = availableCash.add(amount);
    }

    public void reserveCash(BigDecimal amount) {
        validateAmount(amount);
        if (availableCash.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        availableCash = availableCash.subtract(amount);
        reservedCash = reservedCash.add(amount);
    }

    public void releaseCash(BigDecimal amount) {
        validateAmount(amount);
        if (reservedCash.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient reserved cash");
        }
        reservedCash = reservedCash.subtract(amount);
        availableCash = availableCash.add(amount);
    }

    public void consumeReservedCash(BigDecimal amount) {
        validateAmount(amount);
        if (reservedCash.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient reserved cash");
        }
        reservedCash = reservedCash.subtract(amount);
    }

    public void addHolding(String stockId, int qty) {
        if (qty <= 0) throw new IllegalArgumentException("qty must be positive");
        holdings.computeIfAbsent(stockId, id -> new Holding(id, 0, 0)).addAvailable(qty);
    }

    public void reserveHolding(String stockId, int qty) {
        Holding holding = holdings.get(stockId);
        if (holding == null) {
            throw new IllegalStateException("No holding found");
        }
        holding.reserve(qty);
    }

    public void releaseHolding(String stockId, int qty) {
        Holding holding = holdings.get(stockId);
        if (holding == null) {
            throw new IllegalStateException("No holding found");
        }
        holding.release(qty);
    }

    public void consumeReservedHolding(String stockId, int qty) {
        Holding holding = holdings.get(stockId);
        if (holding == null) {
            throw new IllegalStateException("No holding found");
        }
        holding.consumeReserved(qty);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}

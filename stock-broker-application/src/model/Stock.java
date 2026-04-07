package model;

public class Stock {
    private final String stockId;
    private final String symbol;
    private final String name;

    public Stock(String stockId, String symbol, String name) {
        this.stockId = stockId;
        this.symbol = symbol;
        this.name = name;
    }

    public String getStockId() { return stockId; }
    public String getSymbol() { return symbol; }
    public String getName() { return name; }
}

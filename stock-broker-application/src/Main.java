import dto.PlaceOrderRequest;
import dto.PlaceOrderResult;
import model.OrderSide;
import model.Trade;
import lock.LockManager;
import repository.OrderBookRepository;
import repository.OrderRepository;
import repository.StockRepository;
import repository.TradeRepository;
import repository.UserRepository;
import repository.impl.InMemoryOrderBookRepository;
import repository.impl.InMemoryOrderRepository;
import repository.impl.InMemoryStockRepository;
import repository.impl.InMemoryTradeRepository;
import repository.impl.InMemoryUserRepository;
import service.MatchingEngine;
import service.OrderFactory;
import service.OrderService;
import service.ReservationService;
import service.SettlementService;
import service.StockService;
import service.TradeHistoryService;
import service.UserService;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Main {

    private final UserRepository userRepository = new InMemoryUserRepository();
    private final StockRepository stockRepository = new InMemoryStockRepository();
    private final OrderRepository orderRepository = new InMemoryOrderRepository();
    private final TradeRepository tradeRepository = new InMemoryTradeRepository();
    private final OrderBookRepository orderBookRepository = new InMemoryOrderBookRepository();

    private final LockManager lockManager = new LockManager();
    private final ReservationService reservationService = new ReservationService(userRepository, lockManager);
    private final SettlementService settlementService = new SettlementService(userRepository, lockManager);
    private final MatchingEngine matchingEngine = new MatchingEngine(settlementService, tradeRepository);
    private final OrderFactory orderFactory = new OrderFactory();

    private final UserService userService = new UserService(userRepository, lockManager);
    private final StockService stockService = new StockService(stockRepository, orderBookRepository);
    private final OrderService orderService = new OrderService(
            userRepository,
            stockRepository,
            orderRepository,
            orderBookRepository,
            matchingEngine,
            reservationService,
            orderFactory
    );
    private final TradeHistoryService tradeHistoryService = new TradeHistoryService(tradeRepository);

    public static void main(String[] args) throws Exception {
        Main app = new Main();
        app.runAll();
    }

    private void runAll() throws Exception {
        seedBaseData();

        testHappyFlow();
        testPartialFill();
        testPriceTimePriority();
        testInsufficientBalance();
        testInsufficientHoldings();
        testConcurrentBuyOverspendProtection();
        testConcurrentSellOversellProtection();
        testConcurrentMatchingSameStock();

        System.out.println("\n======================================");
        System.out.println("ALL SCENARIOS EXECUTED");
        System.out.println("======================================");
    }

    private void seedBaseData() {
        System.out.println("\n======================================");
        System.out.println("SETUP");
        System.out.println("======================================");

        stockService.addStock("STK-1", "INFY", "Infosys");
        stockService.addStock("STK-2", "TCS", "Tata Consultancy Services");

        userService.registerUser("U1", "Alice");
        userService.registerUser("U2", "Bob");
        userService.registerUser("U3", "Charlie");
        userService.registerUser("U4", "David");

        userService.addBalance("U1", new BigDecimal("100000"));
        userService.addBalance("U2", new BigDecimal("100000"));
        userService.addBalance("U3", new BigDecimal("100000"));
        userService.addBalance("U4", new BigDecimal("100000"));

        // Seed holdings for sell tests
        userRepository.findById("U2").orElseThrow().addHolding("STK-1", 100);
        userRepository.findById("U3").orElseThrow().addHolding("STK-1", 100);
        userRepository.findById("U4").orElseThrow().addHolding("STK-1", 100);

        System.out.println("Seed completed");
    }

    private void testHappyFlow() {
        System.out.println("\n======================================");
        System.out.println("TEST 1: HAPPY FLOW - FULL MATCH");
        System.out.println("======================================");

        PlaceOrderResult sellResult = orderService.placeOrder(
                new PlaceOrderRequest("U2", "STK-1", OrderSide.SELL, new BigDecimal("100"), 10)
        );
        printOrderResult("SELL RESULT", sellResult);

        PlaceOrderResult buyResult = orderService.placeOrder(
                new PlaceOrderRequest("U1", "STK-1", OrderSide.BUY, new BigDecimal("100"), 10)
        );
        printOrderResult("BUY RESULT", buyResult);

        printTradeHistory("U1");
        printTradeHistory("U2");
    }

    private void testPartialFill() {
        System.out.println("\n======================================");
        System.out.println("TEST 2: PARTIAL FILL");
        System.out.println("======================================");

        // Sell 20 @ 110
        PlaceOrderResult sellResult = orderService.placeOrder(
                new PlaceOrderRequest("U3", "STK-1", OrderSide.SELL, new BigDecimal("110"), 20)
        );
        printOrderResult("SELL RESULT", sellResult);

        // Buy 8 @ 120 -> should partially fill seller's 20
        PlaceOrderResult buyResult = orderService.placeOrder(
                new PlaceOrderRequest("U1", "STK-1", OrderSide.BUY, new BigDecimal("120"), 8)
        );
        printOrderResult("BUY RESULT", buyResult);

        printTradeHistory("U1");
        printTradeHistory("U3");
    }

    private void testPriceTimePriority() {
        System.out.println("\n======================================");
        System.out.println("TEST 3: PRICE-TIME PRIORITY");
        System.out.println("======================================");

        // Two sell orders at same price; U2 comes first, then U4
        PlaceOrderResult firstSell = orderService.placeOrder(
                new PlaceOrderRequest("U2", "STK-1", OrderSide.SELL, new BigDecimal("130"), 5)
        );
        printOrderResult("FIRST SELL", firstSell);

        PlaceOrderResult secondSell = orderService.placeOrder(
                new PlaceOrderRequest("U4", "STK-1", OrderSide.SELL, new BigDecimal("130"), 5)
        );
        printOrderResult("SECOND SELL", secondSell);

        // One buy order of qty 8 at 140 should match first U2 fully, then U4 partially
        PlaceOrderResult buy = orderService.placeOrder(
                new PlaceOrderRequest("U1", "STK-1", OrderSide.BUY, new BigDecimal("140"), 8)
        );
        printOrderResult("BUY RESULT", buy);

        System.out.println("Expected: U2's sell should fill before U4 because same price but earlier time.");
        printTradeHistory("U1");
        printTradeHistory("U2");
        printTradeHistory("U4");
    }

    private void testInsufficientBalance() {
        System.out.println("\n======================================");
        System.out.println("TEST 4: INSUFFICIENT BALANCE");
        System.out.println("======================================");

        try {
            orderService.placeOrder(
                    new PlaceOrderRequest("U1", "STK-1", OrderSide.BUY, new BigDecimal("1000000"), 100000)
            );
            System.out.println("ERROR: Expected insufficient balance exception");
        } catch (Exception ex) {
            System.out.println("Expected failure: " + ex.getMessage());
        }
    }

    private void testInsufficientHoldings() {
        System.out.println("\n======================================");
        System.out.println("TEST 5: INSUFFICIENT HOLDINGS");
        System.out.println("======================================");

        try {
            orderService.placeOrder(
                    new PlaceOrderRequest("U1", "STK-1", OrderSide.SELL, new BigDecimal("100"), 9999)
            );
            System.out.println("ERROR: Expected insufficient holdings exception");
        } catch (Exception ex) {
            System.out.println("Expected failure: " + ex.getMessage());
        }
    }

    private void testConcurrentBuyOverspendProtection() throws Exception {
        System.out.println("\n======================================");
        System.out.println("TEST 6: CONCURRENT BUY - OVERSPEND PROTECTION");
        System.out.println("======================================");

        userService.registerUser("U5", "Eve");
        userService.addBalance("U5", new BigDecimal("1000"));

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        Runnable buyTask1 = () -> {
            await(startLatch);
            try {
                PlaceOrderResult result = orderService.placeOrder(
                        new PlaceOrderRequest("U5", "STK-1", OrderSide.BUY, new BigDecimal("100"), 10)
                );
                printOrderResult("THREAD-1 BUY", result);
            } catch (Exception ex) {
                System.out.println("THREAD-1 expected/possible failure: " + ex.getMessage());
            } finally {
                doneLatch.countDown();
            }
        };

        Runnable buyTask2 = () -> {
            await(startLatch);
            try {
                PlaceOrderResult result = orderService.placeOrder(
                        new PlaceOrderRequest("U5", "STK-1", OrderSide.BUY, new BigDecimal("100"), 10)
                );
                printOrderResult("THREAD-2 BUY", result);
            } catch (Exception ex) {
                System.out.println("THREAD-2 expected/possible failure: " + ex.getMessage());
            } finally {
                doneLatch.countDown();
            }
        };

        new Thread(buyTask1).start();
        new Thread(buyTask2).start();

        startLatch.countDown();
        doneLatch.await();

        System.out.println("Expected: only one order should successfully reserve full 1000, second should fail.");
    }

    private void testConcurrentSellOversellProtection() throws Exception {
        System.out.println("\n======================================");
        System.out.println("TEST 7: CONCURRENT SELL - OVERSELL PROTECTION");
        System.out.println("======================================");

        userService.registerUser("U6", "Frank");
        userRepository.findById("U6").orElseThrow().addHolding("STK-1", 10);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        Runnable sellTask1 = () -> {
            await(startLatch);
            try {
                PlaceOrderResult result = orderService.placeOrder(
                        new PlaceOrderRequest("U6", "STK-1", OrderSide.SELL, new BigDecimal("90"), 10)
                );
                printOrderResult("THREAD-1 SELL", result);
            } catch (Exception ex) {
                System.out.println("THREAD-1 expected/possible failure: " + ex.getMessage());
            } finally {
                doneLatch.countDown();
            }
        };

        Runnable sellTask2 = () -> {
            await(startLatch);
            try {
                PlaceOrderResult result = orderService.placeOrder(
                        new PlaceOrderRequest("U6", "STK-1", OrderSide.SELL, new BigDecimal("90"), 10)
                );
                printOrderResult("THREAD-2 SELL", result);
            } catch (Exception ex) {
                System.out.println("THREAD-2 expected/possible failure: " + ex.getMessage());
            } finally {
                doneLatch.countDown();
            }
        };

        new Thread(sellTask1).start();
        new Thread(sellTask2).start();

        startLatch.countDown();
        doneLatch.await();

        System.out.println("Expected: only one sell order should reserve holdings, second should fail.");
    }

    private void testConcurrentMatchingSameStock() throws Exception {
        System.out.println("\n======================================");
        System.out.println("TEST 8: CONCURRENT MATCHING ON SAME STOCK");
        System.out.println("======================================");

        userService.registerUser("U7", "Gina");
        userService.registerUser("U8", "Harry");
        userService.addBalance("U7", new BigDecimal("100000"));
        userRepository.findById("U8").orElseThrow().addHolding("STK-1", 50);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        Runnable buyTask = () -> {
            await(startLatch);
            try {
                PlaceOrderResult result = orderService.placeOrder(
                        new PlaceOrderRequest("U7", "STK-1", OrderSide.BUY, new BigDecimal("200"), 20)
                );
                printOrderResult("CONCURRENT BUY", result);
            } catch (Exception ex) {
                System.out.println("BUY THREAD failure: " + ex.getMessage());
            } finally {
                doneLatch.countDown();
            }
        };

        Runnable sellTask = () -> {
            await(startLatch);
            try {
                PlaceOrderResult result = orderService.placeOrder(
                        new PlaceOrderRequest("U8", "STK-1", OrderSide.SELL, new BigDecimal("180"), 20)
                );
                printOrderResult("CONCURRENT SELL", result);
            } catch (Exception ex) {
                System.out.println("SELL THREAD failure: " + ex.getMessage());
            } finally {
                doneLatch.countDown();
            }
        };

        new Thread(buyTask).start();
        new Thread(sellTask).start();

        startLatch.countDown();
        doneLatch.await();

        printTradeHistory("U7");
        printTradeHistory("U8");

        System.out.println("Expected: matching remains correct because same stock order book is locked.");
    }

    private void printTradeHistory(String userId) {
        List<Trade> trades = tradeHistoryService.getUserTradeHistory(userId);
        System.out.println("\nTrade History for " + userId + ":");
        if (trades.isEmpty()) {
            System.out.println("No trades found");
            return;
        }

        for (Trade trade : trades) {
            System.out.println("TradeId=" + trade.getTradeId()
                    + ", stock=" + trade.getStockId()
                    + ", qty=" + trade.getQuantity()
                    + ", price=" + trade.getExecutionPrice()
                    + ", buyer=" + trade.getBuyerUserId()
                    + ", seller=" + trade.getSellerUserId());
        }
    }

    private void printOrderResult(String label, PlaceOrderResult result) {
        System.out.println(label
                + " -> orderId=" + result.getOrderId()
                + ", status=" + result.getOrderStatus()
                + ", remainingQty=" + result.getRemainingQuantity()
                + ", tradesExecuted=" + result.getExecutedTrades().size());
    }

    private void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted", e);
        }
    }
}
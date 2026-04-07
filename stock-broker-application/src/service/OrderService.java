package service;

import dto.PlaceOrderRequest;
import dto.PlaceOrderResult;
import model.Order;
import model.OrderBook;
import model.OrderSide;
import model.Trade;
import repository.OrderBookRepository;
import repository.OrderRepository;
import repository.StockRepository;
import repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;

public class OrderService {
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;
    private final OrderBookRepository orderBookRepository;
    private final MatchingEngine matchingEngine;
    private final ReservationService reservationService;
    private final OrderFactory orderFactory;

    public OrderService(UserRepository userRepository,
                        StockRepository stockRepository,
                        OrderRepository orderRepository,
                        OrderBookRepository orderBookRepository,
                        MatchingEngine matchingEngine,
                        ReservationService reservationService,
                        OrderFactory orderFactory) {
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.orderRepository = orderRepository;
        this.orderBookRepository = orderBookRepository;
        this.matchingEngine = matchingEngine;
        this.reservationService = reservationService;
        this.orderFactory = orderFactory;
    }

    public PlaceOrderResult placeOrder(PlaceOrderRequest request) {
        validateRequest(request);

        BigDecimal reservationAmount = request.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        if (request.getSide() == OrderSide.BUY) {
            reservationService.reserveCash(request.getUserId(), reservationAmount);
        } else {
            reservationService.reserveHolding(request.getUserId(), request.getStockId(), request.getQuantity());
        }

        Order order = orderFactory.createOrder(
                request.getUserId(),
                request.getStockId(),
                request.getSide(),
                request.getPrice(),
                request.getQuantity()
        );

        orderRepository.save(order);

        OrderBook orderBook = orderBookRepository.getOrCreate(request.getStockId());
        orderBook.getLock().lock();
        try {
            orderBook.addOrder(order);
            List<Trade> trades = matchingEngine.match(orderBook);
            return new PlaceOrderResult(
                    order.getOrderId(),
                    order.getStatus(),
                    order.getRemainingQuantity(),
                    trades
            );
        } finally {
            orderBook.getLock().unlock();
        }
    }

    private void validateRequest(PlaceOrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (!userRepository.existsById(request.getUserId())) {
            throw new IllegalArgumentException("User not found: " + request.getUserId());
        }
        if (!stockRepository.existsById(request.getStockId())) {
            throw new IllegalArgumentException("Stock not found: " + request.getStockId());
        }
        if (request.getSide() == null) {
            throw new IllegalArgumentException("Order side cannot be null");
        }
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }
}
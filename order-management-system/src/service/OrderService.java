package service;

import entity.Order;
import entity.Reservation;
import repository.OrderRepository;

import java.util.UUID;

public class OrderService {
    private final OrderRepository orderRepository;
    private final ReservationService reservationService;
    private final ExpiryScheduler expiryScheduler;

    public OrderService(OrderRepository orderRepository,
                        ReservationService reservationService,
                        ExpiryScheduler expiryScheduler) {
        this.orderRepository = orderRepository;
        this.reservationService = reservationService;
        this.expiryScheduler = expiryScheduler;
    }

    public String initiateOrder(String productId, int quantity, long reservationTtlMs) {
        String orderId = UUID.randomUUID().toString();
        Reservation reservation = null;

        try {
            reservation = reservationService.createReservation(
                    orderId,
                    productId,
                    quantity,
                    reservationTtlMs
            );

            Order order = new Order(orderId, productId, quantity);
            order.markReserved(reservation.getReservationId());
            orderRepository.save(order);

            expiryScheduler.scheduleExpiry(
                    reservation.getReservationId(),
                    reservation.getExpiresAt()
            );

            return orderId;
        } catch (Exception e) {
            if (reservation != null) {
                reservationService.cancelReservation(reservation.getReservationId());
            }
            throw e;
        }
    }

    public boolean confirmOrder(String orderId) {
        Order order = orderRepository.get(orderId);
        boolean success = reservationService.confirmReservation(order.getReservationId());
        if (success) {
            order.markConfirmed();
        }
        return success;
    }

    public boolean cancelOrder(String orderId) {
        Order order = orderRepository.get(orderId);
        boolean success = reservationService.cancelReservation(order.getReservationId());
        if (success) {
            order.markCancelled();
        }
        return success;
    }

    public void markOrderExpired(String orderId) {
        Order order = orderRepository.get(orderId);
        order.markExpired();
    }

    public String getOrder(String orderId) {
        return orderRepository.get(orderId).toString();
    }
}

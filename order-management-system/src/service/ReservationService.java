package service;

import entity.Reservation;
import repository.ReservationRepository;

import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final InventoryService inventoryService;
    private final LockManager lockManager;

    public ReservationService(ReservationRepository reservationRepository,
                              InventoryService inventoryService,
                              LockManager lockManager) {
        this.reservationRepository = reservationRepository;
        this.inventoryService = inventoryService;
        this.lockManager = lockManager;
    }

    public Reservation createReservation(String orderId, String productId, int quantity, long ttlMillis) {
        inventoryService.reserve(productId, quantity);

        long now = System.currentTimeMillis();
        Reservation reservation = new Reservation(
                UUID.randomUUID().toString(),
                orderId,
                productId,
                quantity,
                now,
                now + ttlMillis
        );

        reservationRepository.save(reservation);
        return reservation;
    }


    public boolean confirmReservation(String reservationId) {
        Reservation reservation = reservationRepository.get(reservationId);
        ReentrantLock productLock = lockManager.getLock(reservation.getProductId());

        synchronized (reservation) {
            if (!reservation.isActive()) {
                return false;
            }

            productLock.lock();
            try {
                inventoryService.confirm(reservation.getProductId(), reservation.getQuantity());
                reservation.markConfirmed();
                return true;
            } finally {
                productLock.unlock();
            }
        }
    }

    public boolean cancelReservation(String reservationId) {
        Reservation reservation = reservationRepository.get(reservationId);

        synchronized (reservation) {
            if (!reservation.isActive()) {
                return false;
            }
            reservation.markCancelled();
        }

        inventoryService.release(reservation.getProductId(), reservation.getQuantity());
        return true;
    }

    public boolean expireReservation(String reservationId) {
        Reservation reservation = reservationRepository.get(reservationId);

        synchronized (reservation) {
            if (!reservation.isActive()) {
                return false;
            }
            if (System.currentTimeMillis() < reservation.getExpiresAt()) {
                return false;
            }
            reservation.markExpired();
        }

        inventoryService.release(reservation.getProductId(), reservation.getQuantity());
        return true;
    }

    public Reservation getReservation(String reservationId) {
        return reservationRepository.get(reservationId);
    }
}

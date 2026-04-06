package repository;


import entity.Reservation;

import java.util.concurrent.ConcurrentHashMap;

public class ReservationRepository {
    ConcurrentHashMap<String, Reservation> reservationMap;

    public ReservationRepository() {
        this.reservationMap = new ConcurrentHashMap<>();
    }

    public void save(Reservation reservation) {
        reservationMap.put(reservation.getReservationId(), reservation);
    }


    public Reservation get(String reservationId) {
        return reservationMap.get(reservationId);
    }
}

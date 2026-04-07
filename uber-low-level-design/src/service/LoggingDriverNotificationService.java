package service;

import model.Driver;
import model.RideBooking;

import java.util.List;

public class LoggingDriverNotificationService implements DriverNotificationService {

    @Override
    public void notifyDrivers(RideBooking booking, List<Driver> candidateDrivers) {
        for (Driver driver : candidateDrivers) {
            System.out.println("Notified driver " + driver.getId() + " for ride " + booking.getId());
        }
    }
}

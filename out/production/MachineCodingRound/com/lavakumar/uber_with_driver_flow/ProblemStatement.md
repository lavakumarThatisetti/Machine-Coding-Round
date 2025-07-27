# 🛺 Cab Booking System – Machine Coding + LLD

## Goal

Design an **Uber-like system** that supports both **Rider Flow** and **Driver Flow** with realistic behaviors such as OTP verification, surge pricing, and cab assignment. The system should be:

- Extensible
- Object-oriented
- Runnable with sample simulation (console or UI)
- Aligned with real-world interactions (OTP, surge, cab types)

---

## Core Functionalities

### Rider Flow
1. Rider registers and sets current location
2. Rider selects destination
3. System displays available vehicle types with estimated fares (based on distance + vehicle type)
4. Rider chooses vehicle type and books
5. OTP is generated and shown to the rider
6. Rider shares OTP with driver to start the ride

### Driver Flow
1. Drivers register and link to a cab
2. When a rider books, all nearby available drivers of selected type are notified
3. First driver to accept gets the booking
4. Driver enters OTP to start ride
5. Only driver can end the ride

---

## System Requirements

### Business Rules
- OTP verification required to start the ride (max 3 attempts)
- Cab availability should be based on:
    - Vehicle type
    - Distance threshold (e.g., 5km)
- Fare calculation depends on:
    - Base rate per vehicle type
    - Surge pricing multiplier (based on demand)


Rider -> id, name, currentLocation
Location -> from , to
Cab -> id, driverName, location, isAvailable, vehicleType
vehicleType -> SEDAN, GO, AUTO, BIKE
Booking - id, Rider, Cab , pickupLocation, destinationLocation, bookingTime, fare, status 
package model;

import java.util.Objects;

public class DoctorSlot {
    private final SlotKey slotKey;
    private final TimeSlot timeSlot;
    private SlotStatus status;
    private String bookedPatientId;

    public DoctorSlot(SlotKey slotKey, TimeSlot timeSlot) {
        if (slotKey == null || timeSlot == null) {
            throw new IllegalArgumentException("Slot key and time slot cannot be null");
        }
        this.slotKey = slotKey;
        this.timeSlot = timeSlot;
        this.status = SlotStatus.AVAILABLE;
        this.bookedPatientId = null;
    }

    public SlotKey getSlotKey() {
        return slotKey;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public String getBookedPatientId() {
        return bookedPatientId;
    }

    public boolean isAvailable() {
        return status == SlotStatus.AVAILABLE;
    }

    public boolean isBooked() {
        return status == SlotStatus.BOOKED;
    }

    public void markBooked(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            throw new IllegalArgumentException("Patient id cannot be blank");
        }
        if (status == SlotStatus.BOOKED) {
            throw new IllegalStateException("Slot is already booked");
        }
        this.status = SlotStatus.BOOKED;
        this.bookedPatientId = patientId;
    }

    public void markAvailable() {
        if (status == SlotStatus.AVAILABLE) {
            throw new IllegalStateException("Slot is already available");
        }
        this.status = SlotStatus.AVAILABLE;
        this.bookedPatientId = null;
    }

    public boolean isBookedBy(String patientId) {
        return Objects.equals(this.bookedPatientId, patientId);
    }

    @Override
    public String toString() {
        return "DoctorSlot{" +
                "slotKey=" + slotKey +
                ", timeSlot=" + timeSlot +
                ", status=" + status +
                ", bookedPatientId='" + bookedPatientId + '\'' +
                '}';
    }
}
package model;

import java.time.Instant;

public class WaitlistEntry {
    private final String id;
    private final String doctorId;
    private final String patientId;
    private final TimeSlot timeSlot;
    private final Instant createdAt;

    public WaitlistEntry(String id, String doctorId, String patientId, TimeSlot timeSlot, Instant createdAt) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Waitlist entry id cannot be blank");
        }
        if (doctorId == null || doctorId.isBlank()) {
            throw new IllegalArgumentException("Doctor id cannot be blank");
        }
        if (patientId == null || patientId.isBlank()) {
            throw new IllegalArgumentException("Patient id cannot be blank");
        }
        if (timeSlot == null) {
            throw new IllegalArgumentException("Time slot cannot be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("CreatedAt cannot be null");
        }
        this.id = id;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.timeSlot = timeSlot;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getPatientId() {
        return patientId;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "WaitlistEntry{" +
                "id='" + id + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", timeSlot=" + timeSlot +
                ", createdAt=" + createdAt +
                '}';
    }
}
package model;

import java.time.LocalDateTime;
import java.util.Objects;

public final class SlotKey {
    private final String doctorId;
    private final LocalDateTime startTime;

    public SlotKey(String doctorId, LocalDateTime startTime) {
        if (doctorId == null || doctorId.isBlank()) {
            throw new IllegalArgumentException("Doctor id cannot be blank");
        }
        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }
        this.doctorId = doctorId;
        this.startTime = startTime;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SlotKey slotKey)) return false;
        return doctorId.equals(slotKey.doctorId) && startTime.equals(slotKey.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId, startTime);
    }

    @Override
    public String toString() {
        return "SlotKey{" +
                "doctorId='" + doctorId + '\'' +
                ", startTime=" + startTime +
                '}';
    }
}
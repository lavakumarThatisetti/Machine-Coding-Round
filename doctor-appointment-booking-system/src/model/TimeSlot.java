package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public final class TimeSlot {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start and end time cannot be null");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static TimeSlot of(LocalDateTime startTime, Duration duration) {
        if (startTime == null || duration == null) {
            throw new IllegalArgumentException("Start time and duration cannot be null");
        }
        if (duration.isZero() || duration.isNegative()) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        return new TimeSlot(startTime, startTime.plus(duration));
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Duration duration() {
        return Duration.between(startTime, endTime);
    }

    public boolean overlaps(TimeSlot other) {
        Objects.requireNonNull(other, "Other slot cannot be null");
        return this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSlot)) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return startTime.equals(timeSlot.startTime) && endTime.equals(timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
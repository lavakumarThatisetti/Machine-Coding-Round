package model;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Doctor {
    private final String id;
    private final String name;
    private final Set<Specialization> specializations;
    private final Duration slotDuration;

    public Doctor(String id, String name, Set<Specialization> specializations, Duration slotDuration) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Doctor id cannot be blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Doctor name cannot be blank");
        }
        if (specializations == null || specializations.isEmpty()) {
            throw new IllegalArgumentException("Doctor must have at least one specialization");
        }
        if (slotDuration == null || slotDuration.isZero() || slotDuration.isNegative()) {
            throw new IllegalArgumentException("Slot duration must be positive");
        }

        this.id = id;
        this.name = name;
        this.specializations = new HashSet<>(specializations);
        this.slotDuration = slotDuration;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Specialization> getSpecializations() {
        return Collections.unmodifiableSet(specializations);
    }

    public Duration getSlotDuration() {
        return slotDuration;
    }

    public boolean hasSpecialization(Specialization specialization) {
        return specializations.contains(specialization);
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", specializations=" + specializations +
                ", slotDuration=" + slotDuration +
                '}';
    }
}
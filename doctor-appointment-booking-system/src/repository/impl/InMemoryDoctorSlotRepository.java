package repository.impl;

import model.DoctorSlot;
import model.SlotKey;
import model.SlotStatus;
import repository.DoctorSlotRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDoctorSlotRepository implements DoctorSlotRepository {
    private final ConcurrentHashMap<SlotKey, DoctorSlot> slots = new ConcurrentHashMap<>();

    @Override
    public void save(DoctorSlot slot) {
        if (slot == null) {
            throw new IllegalArgumentException("Slot cannot be null");
        }
        slots.put(slot.getSlotKey(), slot);
    }

    @Override
    public Optional<DoctorSlot> findByKey(SlotKey slotKey) {
        return Optional.ofNullable(slots.get(slotKey));
    }

    @Override
    public boolean exists(SlotKey slotKey) {
        return slots.containsKey(slotKey);
    }

    @Override
    public List<DoctorSlot> findByDoctorId(String doctorId) {
        List<DoctorSlot> result = new ArrayList<>();
        for (DoctorSlot slot : slots.values()) {
            if (slot.getSlotKey().getDoctorId().equals(doctorId)) {
                result.add(slot);
            }
        }
        return result;
    }

    @Override
    public List<DoctorSlot> findAvailableByDoctorId(String doctorId) {
        List<DoctorSlot> result = new ArrayList<>();
        for (DoctorSlot slot : slots.values()) {
            if (slot.getSlotKey().getDoctorId().equals(doctorId)
                    && slot.getStatus() == SlotStatus.AVAILABLE) {
                result.add(slot);
            }
        }
        return result;
    }
}
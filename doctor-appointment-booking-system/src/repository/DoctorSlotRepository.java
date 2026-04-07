package repository;

import model.DoctorSlot;
import model.SlotKey;

import java.util.List;
import java.util.Optional;

public interface DoctorSlotRepository {
    void save(DoctorSlot slot);

    Optional<DoctorSlot> findByKey(SlotKey slotKey);

    boolean exists(SlotKey slotKey);

    List<DoctorSlot> findByDoctorId(String doctorId);

    List<DoctorSlot> findAvailableByDoctorId(String doctorId);
}
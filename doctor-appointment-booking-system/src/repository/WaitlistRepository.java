package repository;

import model.SlotKey;
import model.WaitlistEntry;

import java.util.List;
import java.util.Optional;

public interface WaitlistRepository {
    boolean enqueueIfAbsent(SlotKey slotKey, WaitlistEntry entry);

    Optional<WaitlistEntry> peek(SlotKey slotKey);

    Optional<WaitlistEntry> poll(SlotKey slotKey);

    boolean remove(SlotKey slotKey, String patientId);

    boolean contains(SlotKey slotKey, String patientId);

    List<WaitlistEntry> findAll(SlotKey slotKey);

    boolean isEmpty(SlotKey slotKey);
}
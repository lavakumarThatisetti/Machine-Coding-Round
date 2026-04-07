package service;

import infra.IdGenerator;
import infra.SystemClock;
import model.SlotKey;
import model.TimeSlot;
import model.WaitlistEntry;
import repository.WaitlistRepository;

import java.util.List;
import java.util.Optional;

public class WaitlistService {
    private final WaitlistRepository waitlistRepository;
    private final IdGenerator idGenerator;
    private final SystemClock clock;

    public WaitlistService(WaitlistRepository waitlistRepository,
                           IdGenerator idGenerator,
                           SystemClock clock) {
        this.waitlistRepository = waitlistRepository;
        this.idGenerator = idGenerator;
        this.clock = clock;
    }

    public boolean isPatientAlreadyWaitlisted(SlotKey slotKey, String patientId) {
        return waitlistRepository.contains(slotKey, patientId);
    }

    public boolean addToWaitlistIfAbsent(String doctorId, String patientId, TimeSlot timeSlot) {
        SlotKey slotKey = new SlotKey(doctorId, timeSlot.getStartTime());
        WaitlistEntry entry = new WaitlistEntry(
                idGenerator.nextWaitlistId(),
                doctorId,
                patientId,
                timeSlot,
                clock.instant()
        );
        return waitlistRepository.enqueueIfAbsent(slotKey, entry);
    }

    public Optional<WaitlistEntry> pollNext(SlotKey slotKey) {
        return waitlistRepository.poll(slotKey);
    }

    public boolean isEmpty(SlotKey slotKey) {
        return waitlistRepository.isEmpty(slotKey);
    }

    public List<WaitlistEntry> getWaitlist(SlotKey slotKey) {
        return waitlistRepository.findAll(slotKey);
    }

    public boolean removeFromWaitlist(SlotKey slotKey, String patientId) {
        return waitlistRepository.remove(slotKey, patientId);
    }
}
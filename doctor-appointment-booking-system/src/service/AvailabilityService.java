package service;

import infra.SystemClock;
import model.Doctor;
import model.DoctorSlot;
import model.SlotKey;
import model.TimeSlot;
import repository.DoctorSlotRepository;

import java.time.LocalDateTime;
import java.util.List;

public class AvailabilityService {
    private final DoctorService doctorService;
    private final DoctorSlotRepository slotRepository;
    private final SystemClock clock;

    public AvailabilityService(DoctorService doctorService,
                               DoctorSlotRepository slotRepository,
                               SystemClock clock) {
        this.doctorService = doctorService;
        this.slotRepository = slotRepository;
        this.clock = clock;
    }

    public void publishAvailability(String doctorId, List<LocalDateTime> slotStarts) {
        Doctor doctor = doctorService.getRequiredDoctor(doctorId);

        for (LocalDateTime startTime : slotStarts) {
            if (startTime == null) {
                throw new IllegalArgumentException("Slot start time cannot be null");
            }
            if (!startTime.isAfter(clock.now())) {
                throw new IllegalArgumentException("Cannot publish slot in past: " + startTime);
            }

            TimeSlot timeSlot = TimeSlot.of(startTime, doctor.getSlotDuration());
            SlotKey slotKey = new SlotKey(doctorId, startTime);

            if (slotRepository.exists(slotKey)) {
                throw new IllegalArgumentException("Slot already published: " + slotKey);
            }

            DoctorSlot doctorSlot = new DoctorSlot(slotKey, timeSlot);
            slotRepository.save(doctorSlot);
        }
    }

    public DoctorSlot getRequiredSlot(SlotKey slotKey) {
        return slotRepository.findByKey(slotKey)
                .orElseThrow(() -> new IllegalArgumentException("Slot not published: " + slotKey));
    }

    public List<DoctorSlot> getAvailableSlots(String doctorId) {
        doctorService.getRequiredDoctor(doctorId);
        return slotRepository.findAvailableByDoctorId(doctorId);
    }

    public List<DoctorSlot> getAllSlots(String doctorId) {
        doctorService.getRequiredDoctor(doctorId);
        return slotRepository.findByDoctorId(doctorId);
    }

    public void markSlotBooked(DoctorSlot slot, String patientId) {
        slot.markBooked(patientId);
        slotRepository.save(slot);
    }

    public void markSlotAvailable(DoctorSlot slot) {
        slot.markAvailable();
        slotRepository.save(slot);
    }
}
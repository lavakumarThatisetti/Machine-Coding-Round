package service;

import infra.IdGenerator;
import infra.SlotLockManager;
import infra.SystemClock;
import model.Appointment;
import model.BookingResult;
import model.CancellationResult;
import model.DoctorSlot;
import model.SlotKey;
import model.TimeSlot;
import model.WaitlistEntry;
import repository.AppointmentRepository;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentService {
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AvailabilityService availabilityService;
    private final WaitlistService waitlistService;
    private final AppointmentRepository appointmentRepository;
    private final SlotLockManager lockManager;
    private final IdGenerator idGenerator;
    private final SystemClock clock;

    public AppointmentService(DoctorService doctorService,
                              PatientService patientService,
                              AvailabilityService availabilityService,
                              WaitlistService waitlistService,
                              AppointmentRepository appointmentRepository,
                              SlotLockManager lockManager,
                              IdGenerator idGenerator,
                              SystemClock clock) {
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.availabilityService = availabilityService;
        this.waitlistService = waitlistService;
        this.appointmentRepository = appointmentRepository;
        this.lockManager = lockManager;
        this.idGenerator = idGenerator;
        this.clock = clock;
    }

    public BookingResult bookAppointment(String patientId, String doctorId, LocalDateTime slotStartTime) {
        patientService.getRequiredPatient(patientId);
        doctorService.getRequiredDoctor(doctorId);

        if (slotStartTime == null) {
            throw new IllegalArgumentException("Slot start time cannot be null");
        }

        SlotKey slotKey = new SlotKey(doctorId, slotStartTime);

        return lockManager.executeWithLock(slotKey, () -> {
            DoctorSlot slot = availabilityService.getRequiredSlot(slotKey);

            if (!slot.getTimeSlot().getEndTime().isAfter(clock.now())) {
                return BookingResult.rejected("Cannot book past slot");
            }

            if (hasPatientOverlap(patientId, slot.getTimeSlot())) {
                return BookingResult.rejected("Patient already has overlapping appointment");
            }

            if (slot.isAvailable()) {
                availabilityService.markSlotBooked(slot, patientId);

                Appointment appointment = new Appointment(
                        idGenerator.nextAppointmentId(),
                        doctorId,
                        patientId,
                        slot.getTimeSlot()
                );
                appointmentRepository.save(appointment);

                return BookingResult.confirmed(appointment.getId(), "Appointment booked successfully");
            }

            if (slot.isBookedBy(patientId)) {
                return BookingResult.rejected("Patient already booked this slot");
            }

            if (waitlistService.isPatientAlreadyWaitlisted(slotKey, patientId)) {
                return BookingResult.rejected("Patient already in waitlist for this slot");
            }

            waitlistService.addToWaitlistIfAbsent(doctorId, patientId, slot.getTimeSlot());
            return BookingResult.waitlisted("Slot already booked. Patient added to waitlist");
        });
    }

    public CancellationResult cancelAppointment(String appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment == null) {
            return CancellationResult.notFound("Appointment not found");
        }

        SlotKey slotKey = new SlotKey(appointment.getDoctorId(), appointment.getTimeSlot().getStartTime());

        return lockManager.executeWithLock(slotKey, () -> {
            Appointment latest = appointmentRepository.findById(appointmentId).orElse(null);
            if (latest == null) {
                return CancellationResult.notFound("Appointment not found");
            }

            if (!latest.isActive()) {
                return CancellationResult.alreadyCancelled("Appointment already cancelled");
            }

            DoctorSlot slot = availabilityService.getRequiredSlot(slotKey);

            latest.cancel();
            appointmentRepository.save(latest);

            if (slot.isBooked() && slot.isBookedBy(latest.getPatientId())) {
                availabilityService.markSlotAvailable(slot);
            }

            promoteNextWaitlistedPatient(slotKey, slot);

            return CancellationResult.cancelled("Appointment cancelled successfully");
        });
    }

    public Appointment getAppointment(String appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));
    }

    public List<Appointment> getAppointmentsByPatient(String patientId) {
        patientService.getRequiredPatient(patientId);
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByDoctor(String doctorId) {
        doctorService.getRequiredDoctor(doctorId);
        return appointmentRepository.findByDoctorId(doctorId);
    }

    private void promoteNextWaitlistedPatient(SlotKey slotKey, DoctorSlot slot) {
        while (!waitlistService.isEmpty(slotKey) && slot.isAvailable()) {
            WaitlistEntry entry = waitlistService.pollNext(slotKey).orElse(null);
            if (entry == null) {
                return;
            }

            if (hasPatientOverlap(entry.getPatientId(), entry.getTimeSlot())) {
                continue;
            }

            availabilityService.markSlotBooked(slot, entry.getPatientId());

            Appointment appointment = new Appointment(
                    idGenerator.nextAppointmentId(),
                    entry.getDoctorId(),
                    entry.getPatientId(),
                    entry.getTimeSlot()
            );
            appointmentRepository.save(appointment);
            return;
        }
    }

    private boolean hasPatientOverlap(String patientId, TimeSlot candidateSlot) {
        List<Appointment> activeAppointments = appointmentRepository.findActiveByPatientId(patientId);
        for (Appointment appointment : activeAppointments) {
            if (appointment.getTimeSlot().overlaps(candidateSlot)) {
                return true;
            }
        }
        return false;
    }
}
import infra.IdGenerator;
import infra.SlotLockManager;
import infra.SystemClock;
import model.Appointment;
import model.BookingResult;
import model.CancellationResult;
import model.Doctor;
import model.DoctorSlot;
import model.Patient;
import model.Specialization;
import repository.AppointmentRepository;
import repository.DoctorRepository;
import repository.DoctorSlotRepository;
import repository.PatientRepository;
import repository.WaitlistRepository;
import repository.impl.InMemoryAppointmentRepository;
import repository.impl.InMemoryDoctorRepository;
import repository.impl.InMemoryDoctorSlotRepository;
import repository.impl.InMemoryPatientRepository;
import repository.impl.InMemoryWaitlistRepository;
import service.AppointmentService;
import service.AvailabilityService;
import service.DoctorService;
import service.PatientService;
import service.WaitlistService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class Main {

    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AvailabilityService availabilityService;
    private final WaitlistService waitlistService;
    private final AppointmentService appointmentService;

    public Main() {
        IdGenerator idGenerator = new IdGenerator();
        SystemClock clock = new SystemClock();
        SlotLockManager slotLockManager = new SlotLockManager();

        DoctorRepository doctorRepository = new InMemoryDoctorRepository();
        PatientRepository patientRepository = new InMemoryPatientRepository();
        DoctorSlotRepository doctorSlotRepository = new InMemoryDoctorSlotRepository();
        AppointmentRepository appointmentRepository = new InMemoryAppointmentRepository();
        WaitlistRepository waitlistRepository = new InMemoryWaitlistRepository();

        this.doctorService = new DoctorService(doctorRepository, idGenerator);
        this.patientService = new PatientService(patientRepository, idGenerator);
        this.availabilityService = new AvailabilityService(doctorService, doctorSlotRepository, clock);
        this.waitlistService = new WaitlistService(waitlistRepository, idGenerator, clock);
        this.appointmentService = new AppointmentService(
                doctorService,
                patientService,
                availabilityService,
                waitlistService,
                appointmentRepository,
                slotLockManager,
                idGenerator,
                clock
        );
    }

    public static void main(String[] args) throws Exception {
        Main runner = new Main();
        runner.run();
    }

    private void run() throws Exception {
        testDoctorRegistrationAndSearch();
        testAvailabilityPublishing();
        testSuccessfulBooking();
        testWaitlistFlow();
        testCancellationAndPromotion();
        testDuplicateBookingProtection();
        testConcurrentBookingSameSlot();
    }

    private void testDoctorRegistrationAndSearch() {
        System.out.println("\n==================================");
        System.out.println("TEST 1: Doctor Registration and Search");
        System.out.println("==================================");

        Doctor doctor1 = doctorService.registerDoctor(
                "Dr. Rao",
                Set.of(Specialization.CARDIOLOGIST),
                Duration.ofMinutes(30)
        );

        Doctor doctor2 = doctorService.registerDoctor(
                "Dr. Mehta",
                Set.of(Specialization.DERMATOLOGIST, Specialization.GENERAL_PHYSICIAN),
                Duration.ofMinutes(15)
        );

        System.out.println("Registered doctors:");
        System.out.println(doctor1);
        System.out.println(doctor2);

        List<Doctor> cardiologists = doctorService.findDoctorsBySpecialization(Specialization.CARDIOLOGIST);
        System.out.println("Cardiologists: " + cardiologists);

        List<Doctor> dermatologists = doctorService.findDoctorsBySpecialization(Specialization.DERMATOLOGIST);
        System.out.println("Dermatologists: " + dermatologists);
    }

    private void testAvailabilityPublishing() {
        System.out.println("\n==================================");
        System.out.println("TEST 2: Publish Availability");
        System.out.println("==================================");

        Doctor doctor = doctorService.registerDoctor(
                "Dr. Sharma",
                Set.of(Specialization.ORTHOPEDIC),
                Duration.ofMinutes(30)
        );

        LocalDateTime base = LocalDateTime.now().plusHours(2).withMinute(0).withSecond(0).withNano(0);

        availabilityService.publishAvailability(
                doctor.getId(),
                Arrays.asList(
                        base,
                        base.plusMinutes(30),
                        base.plusMinutes(60)
                )
        );

        List<DoctorSlot> slots = availabilityService.getAvailableSlots(doctor.getId());
        System.out.println("Available slots for " + doctor.getName() + ":");
        for (DoctorSlot slot : slots) {
            System.out.println(slot);
        }
    }

    private void testSuccessfulBooking() {
        System.out.println("\n==================================");
        System.out.println("TEST 3: Successful Booking");
        System.out.println("==================================");

        Doctor doctor = doctorService.registerDoctor(
                "Dr. Reddy",
                Set.of(Specialization.NEUROLOGIST),
                Duration.ofMinutes(30)
        );
        Patient patient = patientService.registerPatient("Amit");

        LocalDateTime slotStart = LocalDateTime.now().plusHours(3).withMinute(0).withSecond(0).withNano(0);
        availabilityService.publishAvailability(doctor.getId(), List.of(slotStart));

        BookingResult result = appointmentService.bookAppointment(patient.getId(), doctor.getId(), slotStart);
        System.out.println("Booking result: " + result);

        if (result.getAppointmentId() != null) {
            Appointment appointment = appointmentService.getAppointment(result.getAppointmentId());
            System.out.println("Created appointment: " + appointment);
        }
    }

    private void testWaitlistFlow() {
        System.out.println("\n==================================");
        System.out.println("TEST 4: Waitlist Flow");
        System.out.println("==================================");

        Doctor doctor = doctorService.registerDoctor(
                "Dr. Iyer",
                Set.of(Specialization.GENERAL_PHYSICIAN),
                Duration.ofMinutes(30)
        );

        Patient patient1 = patientService.registerPatient("Rohit");
        Patient patient2 = patientService.registerPatient("Sneha");
        Patient patient3 = patientService.registerPatient("Kiran");

        LocalDateTime slotStart = LocalDateTime.now().plusHours(4).withMinute(0).withSecond(0).withNano(0);
        availabilityService.publishAvailability(doctor.getId(), List.of(slotStart));

        BookingResult r1 = appointmentService.bookAppointment(patient1.getId(), doctor.getId(), slotStart);
        BookingResult r2 = appointmentService.bookAppointment(patient2.getId(), doctor.getId(), slotStart);
        BookingResult r3 = appointmentService.bookAppointment(patient3.getId(), doctor.getId(), slotStart);

        System.out.println("Patient1 booking: " + r1);
        System.out.println("Patient2 booking: " + r2);
        System.out.println("Patient3 booking: " + r3);

        System.out.println("Waitlist for slot:");
        waitlistService.getWaitlist(new model.SlotKey(doctor.getId(), slotStart))
                .forEach(System.out::println);
    }

    private void testCancellationAndPromotion() {
        System.out.println("\n==================================");
        System.out.println("TEST 5: Cancellation and Waitlist Promotion");
        System.out.println("==================================");

        Doctor doctor = doctorService.registerDoctor(
                "Dr. Khan",
                Set.of(Specialization.PEDIATRICIAN),
                Duration.ofMinutes(30)
        );

        Patient patient1 = patientService.registerPatient("Nikhil");
        Patient patient2 = patientService.registerPatient("Pooja");
        Patient patient3 = patientService.registerPatient("Vikas");

        LocalDateTime slotStart = LocalDateTime.now().plusHours(5).withMinute(0).withSecond(0).withNano(0);
        availabilityService.publishAvailability(doctor.getId(), List.of(slotStart));

        BookingResult b1 = appointmentService.bookAppointment(patient1.getId(), doctor.getId(), slotStart);
        BookingResult b2 = appointmentService.bookAppointment(patient2.getId(), doctor.getId(), slotStart);
        BookingResult b3 = appointmentService.bookAppointment(patient3.getId(), doctor.getId(), slotStart);

        System.out.println("Initial booking results:");
        System.out.println(b1);
        System.out.println(b2);
        System.out.println(b3);

        CancellationResult cancellationResult = appointmentService.cancelAppointment(b1.getAppointmentId());
        System.out.println("Cancellation result: " + cancellationResult);

        System.out.println("Appointments for doctor after cancellation/promotion:");
        appointmentService.getAppointmentsByDoctor(doctor.getId()).forEach(System.out::println);

        System.out.println("Remaining waitlist:");
        waitlistService.getWaitlist(new model.SlotKey(doctor.getId(), slotStart))
                .forEach(System.out::println);
    }

    private void testDuplicateBookingProtection() {
        System.out.println("\n==================================");
        System.out.println("TEST 6: Duplicate Booking Protection");
        System.out.println("==================================");

        Doctor doctor = doctorService.registerDoctor(
                "Dr. Patel",
                Set.of(Specialization.CARDIOLOGIST),
                Duration.ofMinutes(30)
        );

        Patient patient = patientService.registerPatient("Ananya");

        LocalDateTime slotStart = LocalDateTime.now().plusHours(6).withMinute(0).withSecond(0).withNano(0);
        availabilityService.publishAvailability(doctor.getId(), List.of(slotStart));

        BookingResult first = appointmentService.bookAppointment(patient.getId(), doctor.getId(), slotStart);
        BookingResult second = appointmentService.bookAppointment(patient.getId(), doctor.getId(), slotStart);

        System.out.println("First booking attempt: " + first);
        System.out.println("Second booking attempt: " + second);
    }

    private void testConcurrentBookingSameSlot() throws Exception {
        System.out.println("\n==================================");
        System.out.println("TEST 7: Concurrent Booking Same Slot");
        System.out.println("==================================");

        Doctor doctor = doctorService.registerDoctor(
                "Dr. Verma",
                Set.of(Specialization.DERMATOLOGIST),
                Duration.ofMinutes(30)
        );

        Patient patient1 = patientService.registerPatient("User-1");
        Patient patient2 = patientService.registerPatient("User-2");
        Patient patient3 = patientService.registerPatient("User-3");

        LocalDateTime slotStart = LocalDateTime.now().plusHours(7).withMinute(0).withSecond(0).withNano(0);
        availabilityService.publishAvailability(doctor.getId(), List.of(slotStart));

        CountDownLatch readyLatch = new CountDownLatch(3);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(3);

        Runnable task1 = createConcurrentBookingTask("T1", patient1.getId(), doctor.getId(), slotStart, readyLatch, startLatch, doneLatch);
        Runnable task2 = createConcurrentBookingTask("T2", patient2.getId(), doctor.getId(), slotStart, readyLatch, startLatch, doneLatch);
        Runnable task3 = createConcurrentBookingTask("T3", patient3.getId(), doctor.getId(), slotStart, readyLatch, startLatch, doneLatch);

        new Thread(task1).start();
        new Thread(task2).start();
        new Thread(task3).start();

        readyLatch.await();
        System.out.println("All threads ready. Releasing them together...");
        startLatch.countDown();

        doneLatch.await();

        System.out.println("Final appointments for doctor:");
        appointmentService.getAppointmentsByDoctor(doctor.getId()).forEach(System.out::println);

        System.out.println("Final waitlist:");
        waitlistService.getWaitlist(new model.SlotKey(doctor.getId(), slotStart))
                .forEach(System.out::println);
    }

    private Runnable createConcurrentBookingTask(String threadName,
                                                 String patientId,
                                                 String doctorId,
                                                 LocalDateTime slotStart,
                                                 CountDownLatch readyLatch,
                                                 CountDownLatch startLatch,
                                                 CountDownLatch doneLatch) {
        return () -> {
            try {
                readyLatch.countDown();
                startLatch.await();

                BookingResult result = appointmentService.bookAppointment(patientId, doctorId, slotStart);
                System.out.println(threadName + " -> " + result);
            } catch (Exception e) {
                System.out.println(threadName + " failed: " + e.getMessage());
            } finally {
                doneLatch.countDown();
            }
        };
    }
}
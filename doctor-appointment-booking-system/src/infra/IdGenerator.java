package infra;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private final AtomicLong doctorSeq = new AtomicLong(1);
    private final AtomicLong patientSeq = new AtomicLong(1);
    private final AtomicLong appointmentSeq = new AtomicLong(1);
    private final AtomicLong waitlistSeq = new AtomicLong(1);

    public String nextDoctorId() {
        return "DOC-" + doctorSeq.getAndIncrement();
    }

    public String nextPatientId() {
        return "PAT-" + patientSeq.getAndIncrement();
    }

    public String nextAppointmentId() {
        return "APT-" + appointmentSeq.getAndIncrement();
    }

    public String nextWaitlistId() {
        return "WL-" + waitlistSeq.getAndIncrement();
    }
}
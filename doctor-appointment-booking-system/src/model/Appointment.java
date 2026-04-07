package model;

public class Appointment {
    private final String id;
    private final String doctorId;
    private final String patientId;
    private final TimeSlot timeSlot;
    private AppointmentStatus status;

    public Appointment(String id, String doctorId, String patientId, TimeSlot timeSlot) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Appointment id cannot be blank");
        }
        if (doctorId == null || doctorId.isBlank()) {
            throw new IllegalArgumentException("Doctor id cannot be blank");
        }
        if (patientId == null || patientId.isBlank()) {
            throw new IllegalArgumentException("Patient id cannot be blank");
        }
        if (timeSlot == null) {
            throw new IllegalArgumentException("Time slot cannot be null");
        }

        this.id = id;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.timeSlot = timeSlot;
        this.status = AppointmentStatus.BOOKED;
    }

    public String getId() {
        return id;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getPatientId() {
        return patientId;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public boolean isActive() {
        return status == AppointmentStatus.BOOKED;
    }

    public void cancel() {
        if (status == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Appointment already cancelled");
        }
        if (status == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Completed appointment cannot be cancelled");
        }
        this.status = AppointmentStatus.CANCELLED;
    }

    public void complete() {
        if (status != AppointmentStatus.BOOKED) {
            throw new IllegalStateException("Only booked appointment can be completed");
        }
        this.status = AppointmentStatus.COMPLETED;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id='" + id + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", timeSlot=" + timeSlot +
                ", status=" + status +
                '}';
    }
}
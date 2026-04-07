package repository;

import model.Appointment;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {
    void save(Appointment appointment);

    Optional<Appointment> findById(String appointmentId);

    List<Appointment> findAll();

    List<Appointment> findByPatientId(String patientId);

    List<Appointment> findActiveByPatientId(String patientId);

    List<Appointment> findByDoctorId(String doctorId);
}
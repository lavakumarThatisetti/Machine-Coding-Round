package repository.impl;

import model.Appointment;
import repository.AppointmentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAppointmentRepository implements AppointmentRepository {
    private final ConcurrentHashMap<String, Appointment> appointments = new ConcurrentHashMap<>();

    @Override
    public void save(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null");
        }
        appointments.put(appointment.getId(), appointment);
    }

    @Override
    public Optional<Appointment> findById(String appointmentId) {
        return Optional.ofNullable(appointments.get(appointmentId));
    }

    @Override
    public List<Appointment> findAll() {
        return new ArrayList<>(appointments.values());
    }

    @Override
    public List<Appointment> findByPatientId(String patientId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointments.values()) {
            if (appointment.getPatientId().equals(patientId)) {
                result.add(appointment);
            }
        }
        return result;
    }

    @Override
    public List<Appointment> findActiveByPatientId(String patientId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointments.values()) {
            if (appointment.getPatientId().equals(patientId) && appointment.isActive()) {
                result.add(appointment);
            }
        }
        return result;
    }

    @Override
    public List<Appointment> findByDoctorId(String doctorId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointments.values()) {
            if (appointment.getDoctorId().equals(doctorId)) {
                result.add(appointment);
            }
        }
        return result;
    }
}
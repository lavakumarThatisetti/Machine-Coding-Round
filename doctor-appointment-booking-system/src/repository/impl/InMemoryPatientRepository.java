package repository.impl;

import model.Patient;
import repository.PatientRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPatientRepository implements PatientRepository {
    private final ConcurrentHashMap<String, Patient> patients = new ConcurrentHashMap<>();

    @Override
    public void save(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }
        patients.put(patient.getId(), patient);
    }

    @Override
    public Optional<Patient> findById(String patientId) {
        return Optional.ofNullable(patients.get(patientId));
    }

    @Override
    public List<Patient> findAll() {
        return new ArrayList<>(patients.values());
    }
}
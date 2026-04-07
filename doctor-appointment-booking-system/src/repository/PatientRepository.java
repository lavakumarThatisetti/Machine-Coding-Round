package repository;

import model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {
    void save(Patient patient);

    Optional<Patient> findById(String patientId);

    List<Patient> findAll();
}
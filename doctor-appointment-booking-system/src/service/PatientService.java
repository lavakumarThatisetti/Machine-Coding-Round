package service;

import infra.IdGenerator;
import model.Patient;
import repository.PatientRepository;

import java.util.List;

public class PatientService {
    private final PatientRepository patientRepository;
    private final IdGenerator idGenerator;

    public PatientService(PatientRepository patientRepository, IdGenerator idGenerator) {
        this.patientRepository = patientRepository;
        this.idGenerator = idGenerator;
    }

    public Patient registerPatient(String name) {
        Patient patient = new Patient(idGenerator.nextPatientId(), name);
        patientRepository.save(patient);
        return patient;
    }

    public Patient getRequiredPatient(String patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));
    }

    public List<Patient> findAllPatients() {
        return patientRepository.findAll();
    }
}
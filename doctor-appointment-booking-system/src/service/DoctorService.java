package service;

import infra.IdGenerator;
import model.Doctor;
import model.Specialization;
import repository.DoctorRepository;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final IdGenerator idGenerator;

    public DoctorService(DoctorRepository doctorRepository, IdGenerator idGenerator) {
        this.doctorRepository = doctorRepository;
        this.idGenerator = idGenerator;
    }

    public Doctor registerDoctor(String name, Set<Specialization> specializations, Duration slotDuration) {
        Doctor doctor = new Doctor(
                idGenerator.nextDoctorId(),
                name,
                specializations,
                slotDuration
        );
        doctorRepository.save(doctor);
        return doctor;
    }

    public Doctor getRequiredDoctor(String doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + doctorId));
    }

    public List<Doctor> findDoctorsBySpecialization(Specialization specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    public List<Doctor> findAllDoctors() {
        return doctorRepository.findAll();
    }
}
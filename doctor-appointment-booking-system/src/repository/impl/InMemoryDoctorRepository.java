package repository.impl;

import model.Doctor;
import model.Specialization;
import repository.DoctorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDoctorRepository implements DoctorRepository {
    private final ConcurrentHashMap<String, Doctor> doctors = new ConcurrentHashMap<>();

    @Override
    public void save(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor cannot be null");
        }
        doctors.put(doctor.getId(), doctor);
    }

    @Override
    public Optional<Doctor> findById(String doctorId) {
        return Optional.ofNullable(doctors.get(doctorId));
    }

    @Override
    public List<Doctor> findAll() {
        return new ArrayList<>(doctors.values());
    }

    @Override
    public List<Doctor> findBySpecialization(Specialization specialization) {
        List<Doctor> result = new ArrayList<>();
        for (Doctor doctor : doctors.values()) {
            if (doctor.hasSpecialization(specialization)) {
                result.add(doctor);
            }
        }
        return result;
    }
}

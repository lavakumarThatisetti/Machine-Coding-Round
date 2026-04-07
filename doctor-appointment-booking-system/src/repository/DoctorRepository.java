package repository;

import model.Doctor;
import model.Specialization;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository {
    void save(Doctor doctor);

    Optional<Doctor> findById(String doctorId);

    List<Doctor> findAll();

    List<Doctor> findBySpecialization(Specialization specialization);
}
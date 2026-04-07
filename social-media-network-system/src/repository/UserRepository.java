package repository;

import model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void save(User user);

    Optional<User> findById(String userId);

    boolean existsById(String userId);

    List<User> findAll();
}
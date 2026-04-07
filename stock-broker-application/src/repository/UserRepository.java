package repository;

import entity.UserAccount;

import java.util.Optional;

public interface UserRepository {
    void save(UserAccount user);
    Optional<UserAccount> findById(String userId);
    boolean existsById(String userId);
}

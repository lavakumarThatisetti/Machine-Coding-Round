package service;

import entity.UserAccount;
import lock.LockManager;
import repository.UserRepository;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

public class UserService {
    private final UserRepository userRepository;
    private final LockManager lockManager;

    public UserService(UserRepository userRepository, LockManager lockManager) {
        this.userRepository = userRepository;
        this.lockManager = lockManager;
    }

    public void registerUser(String userId, String name) {
        if (userRepository.existsById(userId)) {
            throw new IllegalStateException("User already exists");
        }
        userRepository.save(new UserAccount(userId, name));
    }

    public void addBalance(String userId, BigDecimal amount) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ReentrantLock lock = lockManager.getUserLock(userId);
        lock.lock();
        try {
            user.addCash(amount);
        } finally {
            lock.unlock();
        }
    }
}

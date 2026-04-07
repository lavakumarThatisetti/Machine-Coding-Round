package service;

import model.UserAccount;
import lock.LockManager;
import repository.UserRepository;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

public class ReservationService {
    private final UserRepository userRepository;
    private final LockManager lockManager;

    public ReservationService(UserRepository userRepository, LockManager lockManager) {
        this.userRepository = userRepository;
        this.lockManager = lockManager;
    }

    public void reserveCash(String userId, BigDecimal amount) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ReentrantLock lock = lockManager.getUserLock(userId);
        lock.lock();
        try {
            user.reserveCash(amount);
        } finally {
            lock.unlock();
        }
    }

    public void reserveHolding(String userId, String stockId, int qty) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ReentrantLock lock = lockManager.getUserLock(userId);
        lock.lock();
        try {
            user.reserveHolding(stockId, qty);
        } finally {
            lock.unlock();
        }
    }

    public void releaseCash(String userId, BigDecimal amount) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ReentrantLock lock = lockManager.getUserLock(userId);
        lock.lock();
        try {
            user.releaseCash(amount);
        } finally {
            lock.unlock();
        }
    }

    public void releaseHolding(String userId, String stockId, int qty) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ReentrantLock lock = lockManager.getUserLock(userId);
        lock.lock();
        try {
            user.releaseHolding(stockId, qty);
        } finally {
            lock.unlock();
        }
    }
}

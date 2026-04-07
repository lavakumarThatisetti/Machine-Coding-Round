package service;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockManager {
    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public ReentrantLock getLock(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Lock key cannot be blank");
        }
        return locks.computeIfAbsent(key, ignored -> new ReentrantLock());
    }
}
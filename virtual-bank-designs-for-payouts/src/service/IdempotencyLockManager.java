package service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class IdempotencyLockManager {
    private final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    public ReentrantLock getLock(String key) {
        return lockMap.computeIfAbsent(key, k -> new ReentrantLock());
    }
}

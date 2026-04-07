package lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockManager {
    private final ConcurrentMap<String, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    public ReentrantLock getUserLock(String userId) {
        return userLocks.computeIfAbsent(userId, id -> new ReentrantLock());
    }
}

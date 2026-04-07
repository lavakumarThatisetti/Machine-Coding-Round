package infra;

import model.SlotKey;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class SlotLockManager {
    private final ConcurrentHashMap<SlotKey, ReentrantLock> locks = new ConcurrentHashMap<>();

    public void executeWithLock(SlotKey slotKey, Runnable action) {
        ReentrantLock lock = locks.computeIfAbsent(slotKey, key -> new ReentrantLock());
        lock.lock();
        try {
            action.run();
        } finally {
            lock.unlock();
        }
    }

    public <T> T executeWithLock(SlotKey slotKey, Supplier<T> action) {
        ReentrantLock lock = locks.computeIfAbsent(slotKey, key -> new ReentrantLock());
        lock.lock();
        try {
            return action.get();
        } finally {
            lock.unlock();
        }
    }
}
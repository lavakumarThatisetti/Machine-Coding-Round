package segment;

import eviction.EvictionPolicy;
import model.CacheEntry;
import sampler.KeySampler;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class CacheSegment<K, V> {

    private final int capacity;
    private final Map<K, CacheEntry<K, V>> store;
    private final EvictionPolicy<K> evictionPolicy;
    private final KeySampler<K> keySampler;
    private final ReentrantLock lock;

    public CacheSegment(int capacity,
                        EvictionPolicy<K> evictionPolicy,
                        KeySampler<K> keySampler) {
        this.capacity = capacity;
        this.store = new HashMap<>();
        this.evictionPolicy = evictionPolicy;
        this.keySampler = keySampler;
        this.lock = new ReentrantLock();
    }

    public V get(K key) {
        long now = System.currentTimeMillis();

        lock.lock();
        try {
            CacheEntry<K, V> entry = store.get(key);
            if (entry == null) {
                return null;
            }

            if (entry.isExpired(now)) {
                removeInternal(key);
                return null;
            }

            evictionPolicy.onAccess(key);
            return entry.getValue();
        } finally {
            lock.unlock();
        }
    }

    public void put(K key, V value, long ttlMillis) {
        long expireAtMillis = System.currentTimeMillis() + ttlMillis;

        lock.lock();
        try {
            CacheEntry<K, V> existing = store.get(key);
            if (existing != null) {
                existing.setValue(value);
                existing.setExpireAtMillis(expireAtMillis);
                evictionPolicy.onUpdate(key);
                return;
            }

            cleanupExpiredBoundedInternal(20, 3);

            if (store.size() >= capacity) {
                evictOneInternal();
            }

            CacheEntry<K, V> entry = new CacheEntry<>(key, value, expireAtMillis);
            store.put(key, entry);
            evictionPolicy.onInsert(key);
            keySampler.onInsert(key);
        } finally {
            lock.unlock();
        }
    }

    public void remove(K key) {
        lock.lock();
        try {
            removeInternal(key);
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return store.size();
        } finally {
            lock.unlock();
        }
    }

    public int cleanupExpiredBounded(int sampleSize, int maxRounds) {
        lock.lock();
        try {
            return cleanupExpiredBoundedInternal(sampleSize, maxRounds);
        } finally {
            lock.unlock();
        }
    }

    private int cleanupExpiredBoundedInternal(int sampleSize, int maxRounds) {
        int removed = 0;
        int rounds = 0;

        while (rounds < maxRounds && keySampler.size() > 0) {
            long now = System.currentTimeMillis();
            List<K> candidates = keySampler.sample(sampleSize);

            if (candidates.isEmpty()) {
                break;
            }

            int expiredThisRound = 0;
            for (K key : candidates) {
                CacheEntry<K, V> entry = store.get(key);
                if (entry != null && entry.isExpired(now)) {
                    removeInternal(key);
                    removed++;
                    expiredThisRound++;
                }
            }

            if (expiredThisRound <= Math.max(1, candidates.size() / 4)) {
                break;
            }

            rounds++;
        }

        return removed;
    }

    private void evictOneInternal() {
        K victim = evictionPolicy.selectVictim();
        if (victim != null) {
            removeInternal(victim);
        }
    }

    private void removeInternal(K key) {
        CacheEntry<K, V> removed = store.remove(key);
        if (removed == null) {
            return;
        }
        evictionPolicy.onRemove(key);
        keySampler.onRemove(key);
    }

    @Override
    public String toString() {
        return "CacheSegment{" +
                "capacity=" + capacity +
                ", size=" + store.size() +
                '}';
    }
}

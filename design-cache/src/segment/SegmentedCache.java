package segment;

import eviction.EvictionPolicy;
import model.Cache;
import sampler.RandomKeySampler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SegmentedCache<K, V> implements Cache<K, V> {

    private final int segmentCount;
    private final List<CacheSegment<K, V>> segments;

    public SegmentedCache(int totalCapacity,
                          int segmentCount,
                          Supplier<EvictionPolicy<K>> evictionPolicyFactory) {
        if (totalCapacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0");
        }
        if (segmentCount <= 0) {
            throw new IllegalArgumentException("segmentCount must be > 0");
        }

        this.segmentCount = segmentCount;
        this.segments = new ArrayList<>(segmentCount);

        int baseCapacity = totalCapacity / segmentCount;
        int remainder = totalCapacity % segmentCount;

        for (int i = 0; i < segmentCount; i++) {
            int segmentCapacity = baseCapacity + (i < remainder ? 1 : 0);
            segmentCapacity = Math.max(1, segmentCapacity);

            segments.add(new CacheSegment<>(
                    segmentCapacity,
                    evictionPolicyFactory.get(),
                    new RandomKeySampler<>()
            ));
        }
    }

    @Override
    public V get(K key) {
        return findSegment(key).get(key);
    }

    @Override
    public void put(K key, V value, Duration ttl) {
        long ttlMillis = ttl.toMillis();
        if (ttlMillis <= 0) {
            throw new IllegalArgumentException("ttl must be > 0");
        }
        findSegment(key).put(key, value, ttlMillis);
    }

    @Override
    public void remove(K key) {
        findSegment(key).remove(key);
    }

    @Override
    public int size() {
        int total = 0;
        for (CacheSegment<K, V> segment : segments) {
            total += segment.size();
        }
        return total;
    }

    public int cleanUpAllSegments(int sampleSize, int maxRounds) {
        int totalRemoved = 0;
        for (CacheSegment<K, V> segment : segments) {
            totalRemoved += segment.cleanupExpiredBounded(sampleSize, maxRounds);
        }
        return totalRemoved;
    }

    private CacheSegment<K, V> findSegment(K key) {
        int index = Math.floorMod(key.hashCode(), segmentCount);
        return segments.get(index);
    }
}

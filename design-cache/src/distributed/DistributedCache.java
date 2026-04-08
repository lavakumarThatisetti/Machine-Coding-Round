package distributed;

import model.Cache;

import java.time.Duration;

public class DistributedCache<K, V> implements Cache<K, V> {

    private final HashRing<K, V> hashRing;

    public DistributedCache(HashRing<K, V> hashRing) {
        this.hashRing = hashRing;
    }

    @Override
    public V get(K key) {
        CacheNode<K, V> owner = hashRing.findOwner(key);
        return owner.get(key);
    }

    @Override
    public void put(K key, V value, Duration ttl) {
        CacheNode<K, V> owner = hashRing.findOwner(key);
        owner.put(key, value, ttl);
    }

    @Override
    public void remove(K key) {
        CacheNode<K, V> owner = hashRing.findOwner(key);
        owner.remove(key);
    }

    @Override
    public int size() {
        int total = 0;
        for (CacheNode<K, V> node : hashRing.allNodes()) {
            total += node.size();
        }
        return total;
    }
}

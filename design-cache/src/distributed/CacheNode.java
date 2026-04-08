package distributed;

import model.Cache;

import java.time.Duration;

public class CacheNode<K, V> {

    private final String nodeId;
    private final Cache<K, V> localCache;

    public CacheNode(String nodeId, Cache<K, V> localCache) {
        this.nodeId = nodeId;
        this.localCache = localCache;
    }

    public String getNodeId() {
        return nodeId;
    }

    public V get(K key) {
        return localCache.get(key);
    }

    public void put(K key, V value, Duration ttl) {
        localCache.put(key, value, ttl);
    }

    public void remove(K key) {
        localCache.remove(key);
    }

    public int size() {
        return localCache.size();
    }

    @Override
    public String toString() {
        return "CacheNode{" +
                "nodeId='" + nodeId + '\'' +
                ", size=" + localCache.size() +
                '}';
    }
}

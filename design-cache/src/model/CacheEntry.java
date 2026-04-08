package model;

public class CacheEntry<K, V> {
    private final K key;
    private V value;
    private long expireAtMillis;

    public CacheEntry(K key, V value, long expireAtMillis) {
        this.key = key;
        this.value = value;
        this.expireAtMillis = expireAtMillis;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public long getExpireAtMillis() {
        return expireAtMillis;
    }

    public void setExpireAtMillis(long expireAtMillis) {
        this.expireAtMillis = expireAtMillis;
    }

    public boolean isExpired(long now) {
        return now >= expireAtMillis;
    }

    @Override
    public String toString() {
        return "CacheEntry{" +
                "key=" + key +
                ", value=" + value +
                ", expireAtMillis=" + expireAtMillis +
                '}';
    }
}

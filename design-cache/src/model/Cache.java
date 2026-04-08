package model;

import java.time.Duration;

public interface Cache<K, V> {
    V get(K key);

    void put(K key, V value, Duration ttl);

    void remove(K key);

    int size();
}

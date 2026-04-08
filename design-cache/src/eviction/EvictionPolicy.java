package eviction;

public interface EvictionPolicy<K> {
    void onInsert(K key);

    void onAccess(K key);

    void onUpdate(K key);

    void onRemove(K key);

    K selectVictim();
}

package eviction;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class LFUEvictionPolicy<K> implements EvictionPolicy<K> {

    private final Map<K, Integer> keyToFreq = new HashMap<>();
    private final Map<Integer, LinkedHashSet<K>> freqToKeys = new HashMap<>();
    private int minFreq = 0;

    @Override
    public void onInsert(K key) {
        keyToFreq.put(key, 1);
        freqToKeys.computeIfAbsent(1, ignore -> new LinkedHashSet<>()).add(key);
        minFreq = 1;
    }

    @Override
    public void onAccess(K key) {
        Integer currentFreq = keyToFreq.get(key);
        if (currentFreq == null) {
            return;
        }

        LinkedHashSet<K> currentBucket = freqToKeys.get(currentFreq);
        currentBucket.remove(key);

        if (currentBucket.isEmpty()) {
            freqToKeys.remove(currentFreq);
            if (minFreq == currentFreq) {
                minFreq++;
            }
        }

        int newFreq = currentFreq + 1;
        keyToFreq.put(key, newFreq);
        freqToKeys.computeIfAbsent(newFreq, ignore -> new LinkedHashSet<>()).add(key);
    }

    @Override
    public void onUpdate(K key) {
        onAccess(key);
    }

    @Override
    public void onRemove(K key) {
        Integer freq = keyToFreq.remove(key);
        if (freq == null) {
            return;
        }

        LinkedHashSet<K> bucket = freqToKeys.get(freq);
        if (bucket != null) {
            bucket.remove(key);
            if (bucket.isEmpty()) {
                freqToKeys.remove(freq);
            }
        }

        if (freqToKeys.isEmpty()) {
            minFreq = 0;
        } else if (!freqToKeys.containsKey(minFreq)) {
            minFreq = Collections.min(freqToKeys.keySet());
        }
    }

    @Override
    public K selectVictim() {
        LinkedHashSet<K> bucket = freqToKeys.get(minFreq);
        if (bucket == null || bucket.isEmpty()) {
            return null;
        }
        return bucket.getFirst();
    }
}

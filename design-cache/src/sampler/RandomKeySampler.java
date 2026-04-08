package sampler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomKeySampler<K> implements KeySampler<K> {

    private final List<K> keys = new ArrayList<>();
    private final Map<K, Integer> keyToIndex = new HashMap<>();
    private final Random random = new Random();

    @Override
    public void onInsert(K key) {
        if (keyToIndex.containsKey(key)) {
            return;
        }
        keys.add(key);
        keyToIndex.put(key, keys.size() - 1);
    }

    @Override
    public void onRemove(K key) {
        Integer index = keyToIndex.get(key);
        if (index == null) {
            return;
        }

        int lastIndex = keys.size() - 1;
        K lastKey = keys.get(lastIndex);

        keys.set(index, lastKey);
        keyToIndex.put(lastKey, index);

        keys.remove(lastIndex);
        keyToIndex.remove(key);
    }

    @Override
    public List<K> sample(int count) {
        List<K> result = new ArrayList<>();
        if (keys.isEmpty() || count <= 0) {
            return result;
        }

        int actual = Math.min(count, keys.size());
        for (int i = 0; i < actual; i++) {
            int idx = random.nextInt(keys.size());
            result.add(keys.get(idx));
        }
        return result;
    }

    @Override
    public int size() {
        return keys.size();
    }
}

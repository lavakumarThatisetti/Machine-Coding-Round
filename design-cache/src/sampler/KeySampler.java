package sampler;

import java.util.List;

public interface KeySampler<K> {
    void onInsert(K key);

    void onRemove(K key);

    List<K> sample(int count);

    int size();
}

package distributed;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class HashRing<K, V> {

    private final SortedMap<Integer, CacheNode<K, V>> ring = new TreeMap<>();
    private final int virtualNodesPerPhysicalNode;

    public HashRing(int virtualNodesPerPhysicalNode) {
        this.virtualNodesPerPhysicalNode = virtualNodesPerPhysicalNode;
    }

    public void addNode(CacheNode<K, V> node) {
        for (int i = 0; i < virtualNodesPerPhysicalNode; i++) {
            int hash = hash(node.getNodeId() + "#VN#" + i);
            ring.put(hash, node);
        }
    }

    public void removeNode(CacheNode<K, V> node) {
        for (int i = 0; i < virtualNodesPerPhysicalNode; i++) {
            int hash = hash(node.getNodeId() + "#VN#" + i);
            ring.remove(hash);
        }
    }

    public CacheNode<K, V> findOwner(K key) {
        if (ring.isEmpty()) {
            throw new IllegalStateException("No nodes available in ring");
        }

        int hash = hash(key);
        SortedMap<Integer, CacheNode<K, V>> tail = ring.tailMap(hash);

        int selectedHash = tail.isEmpty() ? ring.firstKey() : tail.firstKey();
        return ring.get(selectedHash);
    }

    public Collection<CacheNode<K, V>> allNodes() {
        return ring.values();
    }

    private int hash(Object value) {
        return value == null ? 0 : value.hashCode();
    }
}

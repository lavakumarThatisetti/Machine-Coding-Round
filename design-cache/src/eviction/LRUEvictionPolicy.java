package eviction;

import java.util.HashMap;
import java.util.Map;

public class LRUEvictionPolicy<K> implements EvictionPolicy<K> {

    static class DoublyLinkedNode<K> {
        K key;
        DoublyLinkedNode<K> prev;
        DoublyLinkedNode<K> next;

        DoublyLinkedNode(K key) {
            this.key = key;
        }
    }

    private final Map<K, DoublyLinkedNode<K>> nodeMap = new HashMap<>();
    private final DoublyLinkedNode<K> head = new DoublyLinkedNode<>(null);
    private final DoublyLinkedNode<K> tail = new DoublyLinkedNode<>(null);

    public LRUEvictionPolicy() {
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public void onInsert(K key) {
        DoublyLinkedNode<K> node = new DoublyLinkedNode<>(key);
        nodeMap.put(key, node);
        addFirst(node);
    }

    @Override
    public void onAccess(K key) {
        DoublyLinkedNode<K> node = nodeMap.get(key);
        if (node == null) {
            return;
        }
        remove(node);
        addFirst(node);
    }

    @Override
    public void onUpdate(K key) {
        onAccess(key);
    }

    @Override
    public void onRemove(K key) {
        DoublyLinkedNode<K> node = nodeMap.remove(key);
        if (node == null) {
            return;
        }
        remove(node);
    }

    @Override
    public K selectVictim() {
        return tail.prev == head ? null : tail.prev.key;
    }

    private void addFirst(DoublyLinkedNode<K> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void remove(DoublyLinkedNode<K> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
}

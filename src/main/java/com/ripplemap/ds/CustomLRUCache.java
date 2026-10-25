package com.ripplemap.ds;

/**
 * Least Recently Used (LRU) Cache combining CustomHashMap and CustomDoublyLinkedList.
 * Caches simulation results so repeated runs with identical parameters return in O(1) time.
 *
 * Complexities:
 * - Get: Time O(1), Space O(1)
 * - Put: Time O(1), Space O(1)
 * - Eviction: Time O(1) from doubly linked list tail
 * - Space: O(capacity)
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class CustomLRUCache<K, V> {

    public static class CacheEntry<K, V> {
        public final K key;
        public V value;

        public CacheEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    private final int capacity;
    private final CustomHashMap<K, CustomDoublyLinkedList.Node<CacheEntry<K, V>>> map;
    private final CustomDoublyLinkedList<CacheEntry<K, V>> dll;

    private long hits = 0;
    private long misses = 0;

    public CustomLRUCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive: " + capacity);
        this.capacity = capacity;
        this.map = new CustomHashMap<>(capacity * 2, 0.75f);
        this.dll = new CustomDoublyLinkedList<>();
    }

    public synchronized V get(K key) {
        CustomDoublyLinkedList.Node<CacheEntry<K, V>> node = map.get(key);
        if (node == null) {
            misses++;
            return null;
        }
        hits++;
        dll.moveToHead(node);
        return node.data.value;
    }

    public synchronized void put(K key, V value) {
        CustomDoublyLinkedList.Node<CacheEntry<K, V>> existing = map.get(key);
        if (existing != null) {
            existing.data.value = value;
            dll.moveToHead(existing);
            return;
        }

        if (dll.size() >= capacity) {
            // Evict least recently used entry at tail
            CustomDoublyLinkedList.Node<CacheEntry<K, V>> tailNode = dll.getTail();
            if (tailNode != null) {
                map.remove(tailNode.data.key);
                dll.removeLast();
            }
        }

        CacheEntry<K, V> entry = new CacheEntry<>(key, value);
        CustomDoublyLinkedList.Node<CacheEntry<K, V>> newNode = dll.addFirst(entry);
        map.put(key, newNode);
    }

    public synchronized boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public synchronized int size() {
        return dll.size();
    }

    public int capacity() {
        return capacity;
    }

    public long getHits() { return hits; }
    public long getMisses() { return misses; }

    public double getHitRatio() {
        long total = hits + misses;
        return (total == 0) ? 0.0 : ((double) hits / total);
    }

    public synchronized void clear() {
        map.clear();
        dll.clear();
        hits = 0;
        misses = 0;
    }
}

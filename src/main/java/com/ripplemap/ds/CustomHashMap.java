package com.ripplemap.ds;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Custom HashMap implementation using Separate Chaining with dynamic rehashing.
 * 
 * Complexities:
 * - Put: Average O(1), Worst O(n) upon high collisions; Space O(1)
 * - Get: Average O(1), Worst O(n)
 * - Remove: Average O(1), Worst O(n)
 * - Rehashing: Time O(n), Space O(n) when load factor threshold (0.75) is exceeded
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class CustomHashMap<K, V> implements Iterable<CustomHashMap.Entry<K, V>> {

    public static class Entry<K, V> {
        public final K key;
        public V value;
        public Entry<K, V> next;

        public Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public K getKey() { return key; }
        public V getValue() { return value; }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Entry<K, V>[] table;
    private int size;
    private final float loadFactor;
    private int threshold;

    @SuppressWarnings("unchecked")
    public CustomHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity <= 0) initialCapacity = DEFAULT_CAPACITY;
        if (loadFactor <= 0) loadFactor = DEFAULT_LOAD_FACTOR;

        this.loadFactor = loadFactor;
        this.table = (Entry<K, V>[]) new Entry[initialCapacity];
        this.size = 0;
        this.threshold = (int) (initialCapacity * loadFactor);
    }

    public CustomHashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private int hash(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        // Smear high bits down for better dispersion
        h ^= (h >>> 16);
        return Math.abs(h % table.length);
    }

    /**
     * Associates the specified value with the specified key.
     * Returns previous value if replaced, or null if key was newly added.
     */
    public V put(K key, V value) {
        int bucketIndex = hash(key);
        Entry<K, V> current = table[bucketIndex];

        while (current != null) {
            if (Objects.equals(current.key, key)) {
                V oldVal = current.value;
                current.value = value;
                return oldVal;
            }
            current = current.next;
        }

        // Key not found in bucket, insert at head of chain
        table[bucketIndex] = new Entry<>(key, value, table[bucketIndex]);
        size++;

        if (size >= threshold) {
            rehash();
        }

        return null;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if not found.
     */
    public V get(K key) {
        int bucketIndex = hash(key);
        Entry<K, V> current = table[bucketIndex];
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public V getOrDefault(K key, V defaultValue) {
        V val = get(key);
        return (val != null) ? val : defaultValue;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     */
    public V remove(K key) {
        int bucketIndex = hash(key);
        Entry<K, V> current = table[bucketIndex];
        Entry<K, V> prev = null;

        while (current != null) {
            if (Objects.equals(current.key, key)) {
                if (prev == null) {
                    table[bucketIndex] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return current.value;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }

    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }

    public CustomArrayList<K> keySet() {
        CustomArrayList<K> keys = new CustomArrayList<>(size);
        for (Entry<K, V> entry : this) {
            keys.add(entry.key);
        }
        return keys;
    }

    public CustomArrayList<V> values() {
        CustomArrayList<V> vals = new CustomArrayList<>(size);
        for (Entry<K, V> entry : this) {
            vals.add(entry.value);
        }
        return vals;
    }

    public CustomArrayList<Entry<K, V>> entrySet() {
        CustomArrayList<Entry<K, V>> entries = new CustomArrayList<>(size);
        for (Entry<K, V> entry : this) {
            entries.add(entry);
        }
        return entries;
    }

    @SuppressWarnings("unchecked")
    private void rehash() {
        int newCapacity = table.length * 2;
        Entry<K, V>[] newTable = (Entry<K, V>[]) new Entry[newCapacity];
        threshold = (int) (newCapacity * loadFactor);

        for (int i = 0; i < table.length; i++) {
            Entry<K, V> current = table[i];
            while (current != null) {
                Entry<K, V> next = current.next;
                int newBucket;
                if (current.key == null) {
                    newBucket = 0;
                } else {
                    int h = current.key.hashCode();
                    h ^= (h >>> 16);
                    newBucket = Math.abs(h % newCapacity);
                }
                current.next = newTable[newBucket];
                newTable[newBucket] = current;
                current = next;
            }
        }
        this.table = newTable;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new Iterator<Entry<K, V>>() {
            private int bucketIndex = 0;
            private Entry<K, V> current = null;

            {
                advanceToNext();
            }

            private void advanceToNext() {
                if (current != null && current.next != null) {
                    current = current.next;
                    return;
                }
                current = null;
                while (bucketIndex < table.length) {
                    if (table[bucketIndex] != null) {
                        current = table[bucketIndex++];
                        return;
                    }
                    bucketIndex++;
                }
            }

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Entry<K, V> next() {
                if (!hasNext()) throw new NoSuchElementException();
                Entry<K, V> res = current;
                advanceToNext();
                return res;
            }
        };
    }
}

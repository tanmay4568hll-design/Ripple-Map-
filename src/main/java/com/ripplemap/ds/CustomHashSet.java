package com.ripplemap.ds;

import java.util.Iterator;

/**
 * Custom HashSet implementation built on top of CustomHashMap.
 *
 * Complexities:
 * - Add: Average O(1), Worst O(n)
 * - Contains: Average O(1), Worst O(n)
 * - Remove: Average O(1), Worst O(n)
 * - Space: O(n)
 *
 * @param <T> Element type
 */
public class CustomHashSet<T> implements Iterable<T> {

    private final CustomHashMap<T, Boolean> map;

    public CustomHashSet() {
        this.map = new CustomHashMap<>();
    }

    public CustomHashSet(int initialCapacity) {
        this.map = new CustomHashMap<>(initialCapacity, 0.75f);
    }

    public boolean add(T element) {
        return map.put(element, Boolean.TRUE) == null;
    }

    public boolean remove(T element) {
        return map.remove(element) != null;
    }

    public boolean contains(T element) {
        return map.containsKey(element);
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public void clear() {
        map.clear();
    }

    public CustomArrayList<T> toList() {
        return map.keySet();
    }

    @Override
    public Iterator<T> iterator() {
        return map.keySet().iterator();
    }
}

package com.ripplemap.ds;

import java.util.Comparator;

/**
 * Priority Queue implementation backed by CustomHeap.
 * Used to power Dijkstra's shortest arrival time algorithm.
 *
 * @param <T> Element type
 */
public class CustomPriorityQueue<T> {

    private final CustomHeap<T> heap;

    public CustomPriorityQueue(Comparator<T> comparator) {
        this.heap = new CustomHeap<>(comparator);
    }

    public void offer(T item) {
        heap.insert(item);
    }

    public T poll() {
        return heap.extractRoot();
    }

    public T peek() {
        return heap.peek();
    }

    public void updatePriority(T item) {
        heap.updatePriority(item);
    }

    public boolean contains(T item) {
        return heap.contains(item);
    }

    public int size() {
        return heap.size();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public void clear() {
        heap.clear();
    }
}

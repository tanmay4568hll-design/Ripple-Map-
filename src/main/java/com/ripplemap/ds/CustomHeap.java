package com.ripplemap.ds;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * Generic Binary Heap implementation supporting Min-Heap or Max-Heap behavior,
 * equipped with an index map for O(log n) decrease-key / priority updates.
 *
 * Powers Dijkstra's shortest arrival time algorithm.
 *
 * Complexities:
 * - Insert / Push: Time O(log n), Space O(1)
 * - Extract Root: Time O(log n), Space O(1)
 * - Peek Root: Time O(1), Space O(1)
 * - Decrease Key / Priority Update: Time O(log n) via position map lookup
 * - Build Heap: Time O(n), Space O(1)
 *
 * @param <T> Element type
 */
public class CustomHeap<T> {

    private final CustomArrayList<T> heap;
    private final CustomHashMap<T, Integer> positionMap;
    private final Comparator<T> comparator;

    public CustomHeap(Comparator<T> comparator) {
        this.heap = new CustomArrayList<>();
        this.positionMap = new CustomHashMap<>();
        this.comparator = comparator;
    }

    public int size() {
        return heap.size();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        return heap.get(0);
    }

    /**
     * Inserts a new element into the heap. Time: O(log n).
     */
    public void insert(T item) {
        heap.add(item);
        int index = heap.size() - 1;
        positionMap.put(item, index);
        siftUp(index);
    }

    /**
     * Extracts and returns the root (min or max according to comparator). Time: O(log n).
     */
    public T extractRoot() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        T root = heap.get(0);
        positionMap.remove(root);

        T lastItem = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, lastItem);
            positionMap.put(lastItem, 0);
            siftDown(0);
        }
        return root;
    }

    /**
     * Updates an element's priority and restores heap order in O(log n) time.
     */
    public void updatePriority(T item) {
        Integer index = positionMap.get(item);
        if (index == null) return;

        siftUp(index);
        siftDown(index);
    }

    public boolean contains(T item) {
        return positionMap.containsKey(item);
    }

    public void clear() {
        heap.clear();
        positionMap.clear();
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (comparator.compare(heap.get(index), heap.get(parent)) < 0) {
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }

    private void siftDown(int index) {
        int n = heap.size();
        while (true) {
            int smallest = index;
            int left = 2 * index + 1;
            int right = 2 * index + 2;

            if (left < n && comparator.compare(heap.get(left), heap.get(smallest)) < 0) {
                smallest = left;
            }
            if (right < n && comparator.compare(heap.get(right), heap.get(smallest)) < 0) {
                smallest = right;
            }

            if (smallest != index) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        T itemI = heap.get(i);
        T itemJ = heap.get(j);

        heap.set(i, itemJ);
        heap.set(j, itemI);

        positionMap.put(itemJ, i);
        positionMap.put(itemI, j);
    }
}

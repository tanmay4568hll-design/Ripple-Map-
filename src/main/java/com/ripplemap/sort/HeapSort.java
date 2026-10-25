package com.ripplemap.sort;

import com.ripplemap.ds.CustomArrayList;
import java.util.Comparator;

/**
 * Heap Sort implementation using an in-place binary max-heap.
 *
 * Complexities:
 * - Best Time: O(n log n)
 * - Average Time: O(n log n)
 * - Worst Time: O(n log n)
 * - Space: O(1) auxiliary space (strict in-place)
 * - Stable: No
 * - In-Place: Yes
 *
 * @param <T> Element type
 */
public class HeapSort<T> implements SortStrategy<T> {

    @Override
    public void sort(CustomArrayList<T> list, Comparator<T> comparator, SortMetrics metrics) {
        if (metrics != null) {
            metrics.reset();
            populateMetrics(metrics);
        }
        long startTime = System.nanoTime();

        int n = list.size();
        if (n <= 1) return;

        // Build max heap bottom-up in O(n)
        for (int i = n / 2 - 1; i >= 0; i--) {
            siftDown(list, n, i, comparator, metrics);
        }

        // Extract elements from heap one by one
        for (int i = n - 1; i > 0; i--) {
            // Move current root (maximum) to end
            swap(list, 0, i, metrics);
            // Sift down the new root on reduced heap of size i
            siftDown(list, i, 0, comparator, metrics);
        }

        if (metrics != null) {
            metrics.setExecutionTimeMs((System.nanoTime() - startTime) / 1e6);
        }
    }

    private void siftDown(CustomArrayList<T> list, int heapSize, int root, Comparator<T> comparator, SortMetrics metrics) {
        int largest = root;
        int left = 2 * root + 1;
        int right = 2 * root + 2;

        if (left < heapSize) {
            if (metrics != null) metrics.incrementComparisons();
            if (comparator.compare(list.get(left), list.get(largest)) > 0) {
                largest = left;
            }
        }

        if (right < heapSize) {
            if (metrics != null) metrics.incrementComparisons();
            if (comparator.compare(list.get(right), list.get(largest)) > 0) {
                largest = right;
            }
        }

        if (largest != root) {
            swap(list, root, largest, metrics);
            siftDown(list, heapSize, largest, comparator, metrics);
        }
    }

    private void swap(CustomArrayList<T> list, int i, int j, SortMetrics metrics) {
        if (i == j) return;
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
        if (metrics != null) metrics.incrementSwaps();
    }

    private void populateMetrics(SortMetrics metrics) {
        metrics.setAlgorithmName(getName());
        metrics.setTimeComplexity(getTimeComplexity());
        metrics.setSpaceComplexity(getSpaceComplexity());
        metrics.setStable(isStable());
        metrics.setInPlace(isInPlace());
    }

    @Override public String getName() { return "Heap Sort"; }
    @Override public String getTimeComplexity() { return "Best O(n log n), Avg O(n log n), Worst O(n log n)"; }
    @Override public String getSpaceComplexity() { return "O(1)"; }
    @Override public boolean isStable() { return false; }
    @Override public boolean isInPlace() { return true; }
}

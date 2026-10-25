package com.ripplemap.sort;

import com.ripplemap.ds.CustomArrayList;
import java.util.Comparator;
import java.util.Random;

/**
 * Quick Sort with Randomized Pivot selection to avoid O(n^2) degradation on sorted data.
 *
 * Complexities:
 * - Best Time: O(n log n)
 * - Average Time: O(n log n)
 * - Worst Time: O(n^2) (mitigated by randomized pivot)
 * - Space: O(log n) recursion stack
 * - Stable: No
 * - In-Place: Yes
 *
 * @param <T> Element type
 */
public class QuickSort<T> implements SortStrategy<T> {

    private final Random random = new Random();

    @Override
    public void sort(CustomArrayList<T> list, Comparator<T> comparator, SortMetrics metrics) {
        if (metrics != null) {
            metrics.reset();
            populateMetrics(metrics);
        }
        long startTime = System.nanoTime();

        if (list.size() > 1) {
            quickSort(list, 0, list.size() - 1, comparator, metrics);
        }

        if (metrics != null) {
            metrics.setExecutionTimeMs((System.nanoTime() - startTime) / 1e6);
        }
    }

    private void quickSort(CustomArrayList<T> list, int low, int high, Comparator<T> comparator, SortMetrics metrics) {
        if (low < high) {
            int pivotIndex = randomizedPartition(list, low, high, comparator, metrics);
            quickSort(list, low, pivotIndex - 1, comparator, metrics);
            quickSort(list, pivotIndex + 1, high, comparator, metrics);
        }
    }

    private int randomizedPartition(CustomArrayList<T> list, int low, int high, Comparator<T> comparator, SortMetrics metrics) {
        int randPivot = low + random.nextInt(high - low + 1);
        swap(list, randPivot, high, metrics);
        return partition(list, low, high, comparator, metrics);
    }

    private int partition(CustomArrayList<T> list, int low, int high, Comparator<T> comparator, SortMetrics metrics) {
        T pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (metrics != null) metrics.incrementComparisons();
            if (comparator.compare(list.get(j), pivot) <= 0) {
                i++;
                swap(list, i, j, metrics);
            }
        }
        swap(list, i + 1, high, metrics);
        return i + 1;
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

    @Override public String getName() { return "Quick Sort (Randomized)"; }
    @Override public String getTimeComplexity() { return "Best O(n log n), Avg O(n log n), Worst O(n^2)"; }
    @Override public String getSpaceComplexity() { return "O(log n) stack"; }
    @Override public boolean isStable() { return false; }
    @Override public boolean isInPlace() { return true; }
}

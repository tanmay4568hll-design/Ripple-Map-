package com.ripplemap.sort;

import com.ripplemap.ds.CustomArrayList;
import java.util.Comparator;

/**
 * Bubble Sort implementation.
 *
 * Complexities:
 * - Best Time: O(n) with swapped flag when array is already sorted
 * - Average Time: O(n^2)
 * - Worst Time: O(n^2)
 * - Space: O(1) auxiliary space
 * - Stable: Yes (does not swap equal elements)
 * - In-Place: Yes
 *
 * @param <T> Element type
 */
public class BubbleSort<T> implements SortStrategy<T> {

    @Override
    public void sort(CustomArrayList<T> list, Comparator<T> comparator, SortMetrics metrics) {
        if (metrics != null) {
            metrics.reset();
            populateMetrics(metrics);
        }
        long startTime = System.nanoTime();

        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (metrics != null) metrics.incrementComparisons();
                if (comparator.compare(list.get(j), list.get(j + 1)) > 0) {
                    T temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                    swapped = true;
                    if (metrics != null) metrics.incrementSwaps();
                }
            }
            if (!swapped) break;
        }

        if (metrics != null) {
            metrics.setExecutionTimeMs((System.nanoTime() - startTime) / 1e6);
        }
    }

    private void populateMetrics(SortMetrics metrics) {
        metrics.setAlgorithmName(getName());
        metrics.setTimeComplexity(getTimeComplexity());
        metrics.setSpaceComplexity(getSpaceComplexity());
        metrics.setStable(isStable());
        metrics.setInPlace(isInPlace());
    }

    @Override public String getName() { return "Bubble Sort"; }
    @Override public String getTimeComplexity() { return "Best O(n), Avg O(n^2), Worst O(n^2)"; }
    @Override public String getSpaceComplexity() { return "O(1)"; }
    @Override public boolean isStable() { return true; }
    @Override public boolean isInPlace() { return true; }
}

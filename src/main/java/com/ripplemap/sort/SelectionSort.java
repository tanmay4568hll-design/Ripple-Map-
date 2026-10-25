package com.ripplemap.sort;

import com.ripplemap.ds.CustomArrayList;
import java.util.Comparator;

/**
 * Selection Sort implementation.
 *
 * Complexities:
 * - Best Time: O(n^2)
 * - Average Time: O(n^2)
 * - Worst Time: O(n^2)
 * - Space: O(1) auxiliary space
 * - Stable: No (long distance swaps can reorder equal keys)
 * - In-Place: Yes
 *
 * @param <T> Element type
 */
public class SelectionSort<T> implements SortStrategy<T> {

    @Override
    public void sort(CustomArrayList<T> list, Comparator<T> comparator, SortMetrics metrics) {
        if (metrics != null) {
            metrics.reset();
            populateMetrics(metrics);
        }
        long startTime = System.nanoTime();

        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (metrics != null) metrics.incrementComparisons();
                if (comparator.compare(list.get(j), list.get(minIdx)) < 0) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                T temp = list.get(i);
                list.set(i, list.get(minIdx));
                list.set(minIdx, temp);
                if (metrics != null) metrics.incrementSwaps();
            }
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

    @Override public String getName() { return "Selection Sort"; }
    @Override public String getTimeComplexity() { return "Best O(n^2), Avg O(n^2), Worst O(n^2)"; }
    @Override public String getSpaceComplexity() { return "O(1)"; }
    @Override public boolean isStable() { return false; }
    @Override public boolean isInPlace() { return true; }
}

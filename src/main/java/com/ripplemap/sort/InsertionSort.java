package com.ripplemap.sort;

import com.ripplemap.ds.CustomArrayList;
import java.util.Comparator;

/**
 * Insertion Sort implementation.
 *
 * Complexities:
 * - Best Time: O(n) when already sorted
 * - Average Time: O(n^2)
 * - Worst Time: O(n^2) when reverse sorted
 * - Space: O(1) auxiliary space
 * - Stable: Yes
 * - In-Place: Yes
 *
 * @param <T> Element type
 */
public class InsertionSort<T> implements SortStrategy<T> {

    @Override
    public void sort(CustomArrayList<T> list, Comparator<T> comparator, SortMetrics metrics) {
        if (metrics != null) {
            metrics.reset();
            populateMetrics(metrics);
        }
        long startTime = System.nanoTime();

        int n = list.size();
        for (int i = 1; i < n; i++) {
            T key = list.get(i);
            int j = i - 1;

            while (j >= 0) {
                if (metrics != null) metrics.incrementComparisons();
                if (comparator.compare(list.get(j), key) > 0) {
                    list.set(j + 1, list.get(j));
                    if (metrics != null) metrics.incrementSwaps();
                    j--;
                } else {
                    break;
                }
            }
            list.set(j + 1, key);
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

    @Override public String getName() { return "Insertion Sort"; }
    @Override public String getTimeComplexity() { return "Best O(n), Avg O(n^2), Worst O(n^2)"; }
    @Override public String getSpaceComplexity() { return "O(1)"; }
    @Override public boolean isStable() { return true; }
    @Override public boolean isInPlace() { return true; }
}

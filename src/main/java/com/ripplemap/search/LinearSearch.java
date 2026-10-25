package com.ripplemap.search;

import com.ripplemap.ds.CustomArrayList;
import java.util.function.Predicate;

/**
 * Linear Search implementation for unsorted data.
 *
 * Complexities:
 * - Best Time: O(1) when target is at index 0
 * - Average Time: O(n)
 * - Worst Time: O(n)
 * - Space: O(1)
 *
 * @param <T> Element type
 */
public class LinearSearch<T> {

    public int search(CustomArrayList<T> list, Predicate<T> predicate, SearchMetrics metrics) {
        if (metrics != null) metrics.reset();
        long startTime = System.nanoTime();

        int resultIndex = -1;
        for (int i = 0; i < list.size(); i++) {
            if (metrics != null) metrics.recordProbe(i);
            if (predicate.test(list.get(i))) {
                resultIndex = i;
                break;
            }
        }

        if (metrics != null) {
            metrics.setFoundIndex(resultIndex);
            metrics.setExecutionTimeMs((System.nanoTime() - startTime) / 1e6);
        }
        return resultIndex;
    }
}

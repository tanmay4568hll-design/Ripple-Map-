package com.ripplemap.search;

import com.ripplemap.ds.CustomArrayList;
import java.util.Comparator;

/**
 * Binary Search implementation providing both iterative and recursive strategies on sorted lists.
 *
 * Complexities:
 * - Best Time: O(1) when target is at exact midpoint
 * - Average Time: O(log n)
 * - Worst Time: O(log n)
 * - Iterative Space: O(1)
 * - Recursive Space: O(log n) call stack
 *
 * @param <T> Element type
 */
public class BinarySearch<T> {

    /**
     * Iterative binary search.
     */
    public int searchIterative(CustomArrayList<T> list, T target, Comparator<T> comparator, SearchMetrics metrics) {
        if (metrics != null) metrics.reset();
        long startTime = System.nanoTime();

        int low = 0;
        int high = list.size() - 1;
        int foundIndex = -1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (metrics != null) metrics.recordProbe(mid);

            int cmp = comparator.compare(list.get(mid), target);
            if (cmp == 0) {
                foundIndex = mid;
                break;
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        if (metrics != null) {
            metrics.setFoundIndex(foundIndex);
            metrics.setExecutionTimeMs((System.nanoTime() - startTime) / 1e6);
        }
        return foundIndex;
    }

    /**
     * Recursive binary search.
     */
    public int searchRecursive(CustomArrayList<T> list, T target, Comparator<T> comparator, SearchMetrics metrics) {
        if (metrics != null) metrics.reset();
        long startTime = System.nanoTime();

        int foundIndex = searchRecursiveHelper(list, target, 0, list.size() - 1, comparator, metrics);

        if (metrics != null) {
            metrics.setFoundIndex(foundIndex);
            metrics.setExecutionTimeMs((System.nanoTime() - startTime) / 1e6);
        }
        return foundIndex;
    }

    private int searchRecursiveHelper(CustomArrayList<T> list, T target, int low, int high,
                                      Comparator<T> comparator, SearchMetrics metrics) {
        if (low > high) return -1;
        int mid = low + (high - low) / 2;
        if (metrics != null) metrics.recordProbe(mid);

        int cmp = comparator.compare(list.get(mid), target);
        if (cmp == 0) {
            return mid;
        } else if (cmp < 0) {
            return searchRecursiveHelper(list, target, mid + 1, high, comparator, metrics);
        } else {
            return searchRecursiveHelper(list, target, low, mid - 1, comparator, metrics);
        }
    }

    /**
     * Finds the index of the first element greater than or equal to target (lower bound).
     * Returns list.size() if no such element exists.
     */
    public int lowerBound(CustomArrayList<T> list, T target, Comparator<T> comparator) {
        int low = 0;
        int high = list.size();
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (comparator.compare(list.get(mid), target) >= 0) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        return low;
    }
}

package com.ripplemap.sort;

import com.ripplemap.ds.CustomArrayList;
import java.util.Comparator;
import java.util.Random;

/**
 * Quick Select algorithm for finding the k-th order statistic or top-K elements in linear average time.
 * Used to extract top-K most impacted nodes without paying full O(n log n) sorting cost.
 *
 * Complexities:
 * - Best Time: O(n)
 * - Average Time: O(n)
 * - Worst Time: O(n^2)
 * - Space: O(1) auxiliary space (tail-call eliminated iterative partitioning)
 * - In-Place: Yes
 *
 * @param <T> Element type
 */
public class QuickSelect<T> {

    private static final Random RANDOM = new Random();

    /**
     * Finds the k-th smallest element (0-indexed) in list according to comparator.
     */
    public T select(CustomArrayList<T> list, int k, Comparator<T> comparator) {
        if (list == null || list.isEmpty() || k < 0 || k >= list.size()) {
            throw new IllegalArgumentException("Invalid index k = " + k + " for list size " + (list != null ? list.size() : 0));
        }

        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            if (low == high) {
                return list.get(low);
            }

            int pivotIndex = randomizedPartition(list, low, high, comparator);
            if (k == pivotIndex) {
                return list.get(k);
            } else if (k < pivotIndex) {
                high = pivotIndex - 1;
            } else {
                low = pivotIndex + 1;
            }
        }
        return list.get(k);
    }

    /**
     * Selects top-K elements according to comparator into a new CustomArrayList.
     * Operates in O(n) average time.
     */
    public CustomArrayList<T> getTopK(CustomArrayList<T> list, int k, Comparator<T> comparator) {
        CustomArrayList<T> result = new CustomArrayList<>(k);
        if (list == null || list.isEmpty() || k <= 0) {
            return result;
        }

        int targetK = Math.min(k, list.size());
        // Make working shallow copy
        CustomArrayList<T> copy = new CustomArrayList<>(list.size());
        for (T item : list) copy.add(item);

        // Partition so first targetK elements are the top elements
        select(copy, targetK - 1, comparator);

        for (int i = 0; i < targetK; i++) {
            result.add(copy.get(i));
        }
        return result;
    }

    private int randomizedPartition(CustomArrayList<T> list, int low, int high, Comparator<T> comparator) {
        int randPivot = low + RANDOM.nextInt(high - low + 1);
        swap(list, randPivot, high);
        return partition(list, low, high, comparator);
    }

    private int partition(CustomArrayList<T> list, int low, int high, Comparator<T> comparator) {
        T pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (comparator.compare(list.get(j), pivot) <= 0) {
                i++;
                swap(list, i, j);
            }
        }
        swap(list, i + 1, high);
        return i + 1;
    }

    private void swap(CustomArrayList<T> list, int i, int j) {
        if (i == j) return;
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}

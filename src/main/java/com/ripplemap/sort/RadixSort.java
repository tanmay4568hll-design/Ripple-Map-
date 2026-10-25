package com.ripplemap.sort;

import com.ripplemap.ds.CustomArrayList;
import java.util.function.ToIntFunction;

/**
 * Radix Sort (LSD) implementation for non-negative integer keys.
 *
 * Complexities:
 * - Time: O(d * (n + b)) where d is the number of digits and b is base 10
 * - Space: O(n + b)
 * - Stable: Yes
 * - In-Place: No
 *
 * @param <T> Element type
 */
public class RadixSort<T> {

    public void sort(CustomArrayList<T> list, ToIntFunction<T> keyExtractor, SortMetrics metrics) {
        if (metrics != null) {
            metrics.reset();
            metrics.setAlgorithmName("Radix Sort (LSD)");
            metrics.setTimeComplexity("O(d * (n + b))");
            metrics.setSpaceComplexity("O(n + b)");
            metrics.setStable(true);
            metrics.setInPlace(false);
        }
        long startTime = System.nanoTime();

        int n = list.size();
        if (n <= 1) return;

        // Find max element to know number of digits
        int max = keyExtractor.applyAsInt(list.get(0));
        for (int i = 1; i < n; i++) {
            int val = keyExtractor.applyAsInt(list.get(i));
            if (metrics != null) metrics.incrementComparisons();
            if (val > max) max = val;
        }

        // Do counting sort for every digit exponent (1, 10, 100, ...)
        for (int exp = 1; max / exp > 0; exp *= 10) {
            countingSortByDigit(list, keyExtractor, exp, metrics);
        }

        if (metrics != null) {
            metrics.setExecutionTimeMs((System.nanoTime() - startTime) / 1e6);
        }
    }

    private void countingSortByDigit(CustomArrayList<T> list, ToIntFunction<T> keyExtractor, int exp, SortMetrics metrics) {
        int n = list.size();
        CustomArrayList<T> output = new CustomArrayList<>(n);
        for (int i = 0; i < n; i++) output.add(null);

        int[] count = new int[10];

        // Store count of occurrences
        for (int i = 0; i < n; i++) {
            int digit = (keyExtractor.applyAsInt(list.get(i)) / exp) % 10;
            count[digit]++;
        }

        // Change count[i] so it contains actual position in output
        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }

        // Build output array in reverse for stability
        for (int i = n - 1; i >= 0; i--) {
            T item = list.get(i);
            int digit = (keyExtractor.applyAsInt(item) / exp) % 10;
            output.set(count[digit] - 1, item);
            count[digit]--;
            if (metrics != null) metrics.incrementSwaps();
        }

        // Copy back
        for (int i = 0; i < n; i++) {
            list.set(i, output.get(i));
        }
    }
}

package com.ripplemap.sort;

import com.ripplemap.ds.CustomArrayList;
import java.util.function.ToIntFunction;

/**
 * Counting Sort implementation for discrete integer keys (such as node hop levels).
 *
 * Complexities:
 * - Time: O(n + k) where k is the range of keys [min, max]
 * - Space: O(n + k) auxiliary count and output arrays
 * - Stable: Yes (iterates backwards when filling output array)
 * - In-Place: No
 *
 * @param <T> Element type
 */
public class CountingSort<T> {

    public void sort(CustomArrayList<T> list, ToIntFunction<T> keyExtractor, SortMetrics metrics) {
        if (metrics != null) {
            metrics.reset();
            metrics.setAlgorithmName("Counting Sort");
            metrics.setTimeComplexity("O(n + k)");
            metrics.setSpaceComplexity("O(n + k)");
            metrics.setStable(true);
            metrics.setInPlace(false);
        }
        long startTime = System.nanoTime();

        int n = list.size();
        if (n <= 1) return;

        // 1. Find min and max key
        int min = keyExtractor.applyAsInt(list.get(0));
        int max = min;
        for (int i = 1; i < n; i++) {
            int k = keyExtractor.applyAsInt(list.get(i));
            if (metrics != null) metrics.incrementComparisons();
            if (k < min) min = k;
            if (k > max) max = k;
        }

        int range = max - min + 1;
        int[] count = new int[range];

        // 2. Tally frequencies
        for (int i = 0; i < n; i++) {
            count[keyExtractor.applyAsInt(list.get(i)) - min]++;
        }

        // 3. Accumulate prefix sums for stable positions
        for (int i = 1; i < range; i++) {
            count[i] += count[i - 1];
        }

        // 4. Build output array in reverse to maintain stability
        CustomArrayList<T> output = new CustomArrayList<>(n);
        for (int i = 0; i < n; i++) output.add(null);

        for (int i = n - 1; i >= 0; i--) {
            T item = list.get(i);
            int k = keyExtractor.applyAsInt(item) - min;
            int targetIndex = count[k] - 1;
            output.set(targetIndex, item);
            count[k]--;
            if (metrics != null) metrics.incrementSwaps();
        }

        // 5. Copy back to original list
        for (int i = 0; i < n; i++) {
            list.set(i, output.get(i));
        }

        if (metrics != null) {
            metrics.setExecutionTimeMs((System.nanoTime() - startTime) / 1e6);
        }
    }
}

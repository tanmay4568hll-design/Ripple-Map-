package com.ripplemap.search;

import com.ripplemap.ds.CustomArrayList;

/**
 * Interpolation Search implementation for uniformly distributed numerical data (e.g. arrival times in hours).
 *
 * Probe calculation:
 * pos = low + ((target - arr[low]) * (high - low)) / (arr[high] - arr[low])
 *
 * Complexities:
 * - Best Time: O(1)
 * - Average Time: O(log log n) on uniformly distributed values
 * - Worst Time: O(n) on exponentially skewed distributions
 * - Space: O(1)
 */
public class InterpolationSearch {

    public int search(CustomArrayList<Double> list, double target, SearchMetrics metrics) {
        if (metrics != null) metrics.reset();
        long startTime = System.nanoTime();

        int low = 0;
        int high = list.size() - 1;
        int foundIndex = -1;

        while (low <= high && target >= list.get(low) && target <= list.get(high)) {
            if (low == high) {
                if (metrics != null) metrics.recordProbe(low);
                if (Math.abs(list.get(low) - target) < 1e-9) {
                    foundIndex = low;
                }
                break;
            }

            double lowVal = list.get(low);
            double highVal = list.get(high);

            // Compute interpolated probe position
            int pos = low + (int) (((target - lowVal) / (highVal - lowVal)) * (high - low));
            if (pos < low) pos = low;
            if (pos > high) pos = high;

            if (metrics != null) metrics.recordProbe(pos);

            double posVal = list.get(pos);
            if (Math.abs(posVal - target) < 1e-9) {
                foundIndex = pos;
                break;
            } else if (posVal < target) {
                low = pos + 1;
            } else {
                high = pos - 1;
            }
        }

        if (metrics != null) {
            metrics.setFoundIndex(foundIndex);
            metrics.setExecutionTimeMs((System.nanoTime() - startTime) / 1e6);
        }
        return foundIndex;
    }
}

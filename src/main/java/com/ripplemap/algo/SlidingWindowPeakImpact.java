package com.ripplemap.algo;

import com.ripplemap.ds.CustomArrayList;
import com.ripplemap.ds.CustomDeque;

/**
 * Calculates peak (maximum) impact over a sliding time window of size K
 * using a monotonic decreasing CustomDeque in O(n) linear time.
 *
 * Algorithm complexity:
 * - Time: O(n) where n is the number of time points (each index added and removed at most once)
 * - Space: O(K) auxiliary space for the deque
 */
public class SlidingWindowPeakImpact {

    public static class WindowResult {
        public final int windowStartIndex;
        public final int windowEndIndex;
        public final double maxImpact;

        public WindowResult(int windowStartIndex, int windowEndIndex, double maxImpact) {
            this.windowStartIndex = windowStartIndex;
            this.windowEndIndex = windowEndIndex;
            this.maxImpact = maxImpact;
        }

        @Override
        public String toString() {
            return "[" + windowStartIndex + ".." + windowEndIndex + "]: max=" + String.format("%.3f", maxImpact);
        }
    }

    /**
     * Computes the sliding window maximums.
     *
     * @param impacts Array of impact scores
     * @param k Window size
     * @return CustomArrayList of WindowResult
     */
    public static CustomArrayList<WindowResult> calculatePeakImpacts(double[] impacts, int k) {
        CustomArrayList<WindowResult> results = new CustomArrayList<>();
        if (impacts == null || impacts.length == 0 || k <= 0) {
            return results;
        }

        int n = impacts.length;
        if (k > n) k = n;

        // CustomDeque stores indices of elements in decreasing order of value
        CustomDeque<Integer> deque = new CustomDeque<>();

        for (int i = 0; i < n; i++) {
            // 1. Remove indices that are outside the current window [i - k + 1, i]
            while (!deque.isEmpty() && deque.peekFirst() <= i - k) {
                deque.removeFirst();
            }

            // 2. Maintain monotonic decreasing property: remove elements smaller than impacts[i]
            while (!deque.isEmpty() && impacts[deque.peekLast()] <= impacts[i]) {
                deque.removeLast();
            }

            // 3. Add current element index
            deque.addLast(i);

            // 4. Once we have reached at least window size k, record the maximum at front of deque
            if (i >= k - 1) {
                int maxIdx = deque.peekFirst();
                results.add(new WindowResult(i - k + 1, i, impacts[maxIdx]));
            }
        }

        return results;
    }
}

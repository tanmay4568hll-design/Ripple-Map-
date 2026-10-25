package com.ripplemap.ds;

/**
 * Segment Tree implementation for Range Impact Queries on the simulation timeline.
 * Supports range sum (cumulative impact) and range max (peak shock) queries in O(log n) time.
 *
 * Complexities:
 * - Tree Construction: Time O(n), Space O(n)
 * - Range Query [L, R]: Time O(log n)
 * - Point Update (hour H): Time O(log n)
 */
public class SegmentTree {

    private final double[] treeSum;
    private final double[] treeMax;
    private final int n;

    public SegmentTree(double[] data) {
        if (data == null || data.length == 0) {
            this.n = 0;
            this.treeSum = new double[0];
            this.treeMax = new double[0];
        } else {
            this.n = data.length;
            this.treeSum = new double[4 * n];
            this.treeMax = new double[4 * n];
            build(data, 1, 0, n - 1);
        }
    }

    private void build(double[] data, int node, int start, int end) {
        if (start == end) {
            treeSum[node] = data[start];
            treeMax[node] = data[start];
            return;
        }

        int mid = start + (end - start) / 2;
        int leftChild = 2 * node;
        int rightChild = 2 * node + 1;

        build(data, leftChild, start, mid);
        build(data, rightChild, mid + 1, end);

        treeSum[node] = treeSum[leftChild] + treeSum[rightChild];
        treeMax[node] = Math.max(treeMax[leftChild], treeMax[rightChild]);
    }

    /**
     * Updates value at index in O(log n) time.
     */
    public void update(int index, double value) {
        if (n == 0 || index < 0 || index >= n) return;
        updateHelper(1, 0, n - 1, index, value);
    }

    private void updateHelper(int node, int start, int end, int index, double value) {
        if (start == end) {
            treeSum[node] = value;
            treeMax[node] = value;
            return;
        }

        int mid = start + (end - start) / 2;
        int leftChild = 2 * node;
        int rightChild = 2 * node + 1;

        if (index <= mid) {
            updateHelper(leftChild, start, mid, index, value);
        } else {
            updateHelper(rightChild, mid + 1, end, index, value);
        }

        treeSum[node] = treeSum[leftChild] + treeSum[rightChild];
        treeMax[node] = Math.max(treeMax[leftChild], treeMax[rightChild]);
    }

    /**
     * Queries total sum of impact between hour L and hour R (inclusive). Time: O(log n).
     */
    public double querySum(int L, int R) {
        if (n == 0 || L > R || R < 0 || L >= n) return 0.0;
        int qL = Math.max(0, L);
        int qR = Math.min(n - 1, R);
        return querySumHelper(1, 0, n - 1, qL, qR);
    }

    private double querySumHelper(int node, int start, int end, int L, int R) {
        if (L <= start && end <= R) {
            return treeSum[node];
        }
        if (end < L || start > R) {
            return 0.0;
        }

        int mid = start + (end - start) / 2;
        double left = querySumHelper(2 * node, start, mid, L, R);
        double right = querySumHelper(2 * node + 1, mid + 1, end, L, R);
        return left + right;
    }

    /**
     * Queries maximum peak impact between hour L and hour R (inclusive). Time: O(log n).
     */
    public double queryMax(int L, int R) {
        if (n == 0 || L > R || R < 0 || L >= n) return 0.0;
        int qL = Math.max(0, L);
        int qR = Math.min(n - 1, R);
        return queryMaxHelper(1, 0, n - 1, qL, qR);
    }

    private double queryMaxHelper(int node, int start, int end, int L, int R) {
        if (L <= start && end <= R) {
            return treeMax[node];
        }
        if (end < L || start > R) {
            return Double.NEGATIVE_INFINITY;
        }

        int mid = start + (end - start) / 2;
        double left = queryMaxHelper(2 * node, start, mid, L, R);
        double right = queryMaxHelper(2 * node + 1, mid + 1, end, L, R);
        return Math.max(left, right);
    }
}

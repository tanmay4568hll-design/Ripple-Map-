package com.ripplemap.ds;

/**
 * Disjoint Set Union (DSU) / Union-Find data structure with Path Compression and Union by Rank.
 * Powers Kruskal's Minimum Spanning Tree (MST) algorithm to compute the backbone network.
 *
 * Complexities:
 * - Find: Amortized O(alpha(n)) ~ O(1) via two-pass path compression
 * - Union: Amortized O(alpha(n)) ~ O(1) via rank-based tree height minimization
 * - Space: O(n) for parent and rank arrays
 */
public class DisjointSetUnion {

    private final int[] parent;
    private final int[] rank;
    private int components;

    public DisjointSetUnion(int n) {
        if (n < 0) throw new IllegalArgumentException("Size cannot be negative");
        this.parent = new int[n];
        this.rank = new int[n];
        this.components = n;

        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    /**
     * Finds the representative root of set containing x with full path compression.
     */
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression
        }
        return parent[x];
    }

    /**
     * Unites the sets containing x and y using union by rank.
     * Returns true if two disjoint sets were merged, false if already in the same set.
     */
    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) {
            return false; // already connected (would form cycle)
        }

        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }

        components--;
        return true;
    }

    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    public int getComponentCount() {
        return components;
    }
}

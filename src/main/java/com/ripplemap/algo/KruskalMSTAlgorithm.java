package com.ripplemap.algo;

import com.ripplemap.ds.CustomArrayList;
import com.ripplemap.ds.CustomHashMap;
import com.ripplemap.ds.DisjointSetUnion;
import com.ripplemap.graph.Graph;
import com.ripplemap.graph.GraphEdge;
import com.ripplemap.sort.MergeSort;

import java.util.Comparator;

/**
 * Kruskal's Minimum Spanning Tree (MST) Algorithm.
 * Uses Disjoint Set Union (DSU) and manual MergeSort to extract the critical backbone network.
 *
 * Complexities:
 * - Time: O(E log E) to sort edges + O(E * alpha(V)) for DSU operations = O(E log V)
 * - Space: O(V + E)
 */
public class KruskalMSTAlgorithm {

    public static class MstResult {
        public final CustomArrayList<GraphEdge> mstEdges = new CustomArrayList<>();
        public double totalCost = 0.0;
        public boolean isSpanning = false;
        public final AlgorithmExecutionReport report = new AlgorithmExecutionReport(
            "Kruskal's Minimum Spanning Tree",
            "O(E log V)",
            "O(V + E)"
        );
    }

    public MstResult execute(Graph graph) {
        MstResult result = new MstResult();
        long startTime = System.nanoTime();

        if (graph == null || graph.nodeCount() == 0) {
            return result;
        }

        CustomArrayList<GraphEdge> allEdges = graph.getAllEdges();
        if (allEdges.isEmpty()) {
            return result;
        }

        // 1. Sort edges in ascending order of delayHours (or weight cost) using manual MergeSort
        Comparator<GraphEdge> edgeComp = Comparator.comparingDouble(GraphEdge::getDelayHours);
        new MergeSort<GraphEdge>().sort(allEdges, edgeComp, null);

        // Map node IDs to 0-based contiguous indices for DSU
        CustomHashMap<Integer, Integer> idToIndex = new CustomHashMap<>();
        int index = 0;
        for (int id : graph.getNodeIds()) {
            idToIndex.put(id, index++);
        }

        DisjointSetUnion dsu = new DisjointSetUnion(graph.nodeCount());
        int edgesCounted = 0;

        // 2. Greedily select edges that do not create cycles
        for (GraphEdge edge : allEdges) {
            int uIdx = idToIndex.get(edge.getSourceId());
            int vIdx = idToIndex.get(edge.getTargetId());

            if (dsu.union(uIdx, vIdx)) {
                result.mstEdges.add(edge);
                result.totalCost += edge.getDelayHours();
                edgesCounted++;
                if (edgesCounted == graph.nodeCount() - 1) {
                    break;
                }
            }
        }

        result.isSpanning = (dsu.getComponentCount() == 1);

        double durationMs = (System.nanoTime() - startTime) / 1e6;
        result.report.setDurationMs(durationMs);
        result.report.setNodesVisited(graph.nodeCount());
        result.report.setEdgesTraversed(allEdges.size());
        result.report.setSummaryText("Kruskal MST selected " + result.mstEdges.size() +
                                    " backbone links (Total latency: " + String.format("%.2f", result.totalCost) + "h).");

        return result;
    }
}

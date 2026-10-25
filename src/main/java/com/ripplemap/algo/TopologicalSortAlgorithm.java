package com.ripplemap.algo;

import com.ripplemap.ds.CustomArrayList;
import com.ripplemap.ds.CustomHashMap;
import com.ripplemap.ds.LinkedListQueue;
import com.ripplemap.graph.Graph;
import com.ripplemap.graph.GraphEdge;

/**
 * Kahn's Algorithm for Topological Sorting.
 * Establishes dependency / failure cascade order when the network graph is a DAG.
 *
 * Complexities:
 * - Time: O(V + E)
 * - Space: O(V) for in-degree map and queue
 */
public class TopologicalSortAlgorithm {

    public static class TopoResult {
        public boolean isDAG = false;
        public final CustomArrayList<Integer> sortedOrder = new CustomArrayList<>();
        public final AlgorithmExecutionReport report = new AlgorithmExecutionReport(
            "Kahn's Topological Sort",
            "O(V + E)",
            "O(V)"
        );
    }

    public TopoResult execute(Graph graph) {
        TopoResult result = new TopoResult();
        long startTime = System.nanoTime();

        if (graph == null || graph.nodeCount() == 0) {
            result.isDAG = true;
            return result;
        }

        CustomHashMap<Integer, Integer> inDegree = new CustomHashMap<>();
        for (int nodeId : graph.getNodeIds()) {
            inDegree.put(nodeId, 0);
        }

        // Compute in-degrees
        for (GraphEdge edge : graph.getAllEdges()) {
            int t = edge.getTargetId();
            Integer deg = inDegree.get(t);
            inDegree.put(t, (deg != null ? deg : 0) + 1);
        }

        // Initialize queue with all nodes having 0 in-degree
        LinkedListQueue<Integer> queue = new LinkedListQueue<>();
        for (int nodeId : graph.getNodeIds()) {
            if (inDegree.get(nodeId) == 0) {
                queue.enqueue(nodeId);
            }
        }

        int processed = 0;
        while (!queue.isEmpty()) {
            int u = queue.dequeue();
            result.sortedOrder.add(u);
            processed++;

            for (GraphEdge edge : graph.getOutgoingEdges(u)) {
                int v = edge.getTargetId();
                int newDeg = inDegree.get(v) - 1;
                inDegree.put(v, newDeg);
                if (newDeg == 0) {
                    queue.enqueue(v);
                }
            }
        }

        result.isDAG = (processed == graph.nodeCount());

        double durationMs = (System.nanoTime() - startTime) / 1e6;
        result.report.setDurationMs(durationMs);
        result.report.setNodesVisited(processed);
        result.report.setSummaryText(result.isDAG
            ? "Network is a DAG. Successfully derived linear failure order across " + processed + " nodes."
            : "Network contains cycles; topological sort is incomplete (resolved " + processed + "/" + graph.nodeCount() + " nodes).");

        return result;
    }
}

package com.ripplemap.algo;

import com.ripplemap.ds.CustomHashMap;
import com.ripplemap.ds.CustomPriorityQueue;
import com.ripplemap.graph.Graph;
import com.ripplemap.graph.GraphEdge;

import java.util.Comparator;
import java.util.Objects;

/**
 * Dijkstra's Algorithm for earliest time-of-impact per node.
 * Uses edge delayHours as positive edge weights.
 *
 * Complexities:
 * - Time: O((V + E) log V) using CustomPriorityQueue
 * - Space: O(V) for distance and predecessor maps
 */
public class DijkstraTimeAlgorithm {

    public static class NodeDistance {
        public final int nodeId;
        public double distance; // arrival time in hours

        public NodeDistance(int nodeId, double distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NodeDistance that = (NodeDistance) o;
            return nodeId == that.nodeId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(nodeId);
        }
    }

    public static class DijkstraResult {
        public final CustomHashMap<Integer, Double> earliestArrival = new CustomHashMap<>();
        public final CustomHashMap<Integer, Integer> predecessorMap = new CustomHashMap<>();
        public final AlgorithmExecutionReport report = new AlgorithmExecutionReport(
            "Dijkstra Earliest Time-of-Impact",
            "O((V + E) log V)",
            "O(V)"
        );
    }

    public DijkstraResult execute(Graph graph, int sourceNodeId) {
        DijkstraResult result = new DijkstraResult();
        long startTime = System.nanoTime();

        if (graph == null || !graph.containsNode(sourceNodeId)) {
            return result;
        }

        Comparator<NodeDistance> comp = Comparator.comparingDouble(nd -> nd.distance);
        CustomPriorityQueue<NodeDistance> pq = new CustomPriorityQueue<>(comp);
        CustomHashMap<Integer, NodeDistance> nodeDistanceMap = new CustomHashMap<>();

        // Initialize distances
        for (int nodeId : graph.getNodeIds()) {
            double initialDist = (nodeId == sourceNodeId) ? 0.0 : Double.POSITIVE_INFINITY;
            NodeDistance nd = new NodeDistance(nodeId, initialDist);
            nodeDistanceMap.put(nodeId, nd);
            result.earliestArrival.put(nodeId, initialDist);
            pq.offer(nd);
        }

        int visitedNodes = 0;
        int traversedEdges = 0;

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            int u = current.nodeId;
            double distU = current.distance;

            if (Double.isInfinite(distU)) {
                break; // remaining nodes unreachable from source
            }

            visitedNodes++;

            for (GraphEdge edge : graph.getOutgoingEdges(u)) {
                traversedEdges++;
                int v = edge.getTargetId();
                double newDist = distU + edge.getDelayHours();

                NodeDistance targetNd = nodeDistanceMap.get(v);
                if (targetNd != null && newDist < targetNd.distance) {
                    targetNd.distance = newDist;
                    result.earliestArrival.put(v, newDist);
                    result.predecessorMap.put(v, u);
                    pq.updatePriority(targetNd);
                }
            }
        }

        double durationMs = (System.nanoTime() - startTime) / 1e6;
        result.report.setDurationMs(durationMs);
        result.report.setNodesVisited(visitedNodes);
        result.report.setEdgesTraversed(traversedEdges);
        result.report.setSummaryText("Dijkstra evaluated shortest arrival latencies for " + visitedNodes + " reachable nodes.");

        return result;
    }
}

package com.ripplemap.algo;

import com.ripplemap.ds.CustomArrayList;
import com.ripplemap.ds.CustomHashMap;
import com.ripplemap.ds.CustomPriorityQueue;
import com.ripplemap.graph.Graph;
import com.ripplemap.graph.GraphEdge;

import java.util.Comparator;
import java.util.Objects;

/**
 * "Why Was X Affected?" - Strongest Causal Path Reconstruction.
 * Identifies the primary transmission chain responsible for delivering the maximum impact
 * from the triggered event node to any selected downstream target node.
 *
 * Employs a Max-Product Dijkstra search: finds the path maximizing the product of edge weights.
 *
 * Complexities:
 * - Time: O((V + E) log V)
 * - Space: O(V)
 */
public class CausalPathReconstructionAlgorithm {

    public static class PathNode {
        public final int nodeId;
        public double maxImpact;

        public PathNode(int nodeId, double maxImpact) {
            this.nodeId = nodeId;
            this.maxImpact = maxImpact;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PathNode pathNode = (PathNode) o;
            return nodeId == pathNode.nodeId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(nodeId);
        }
    }

    public static class CausalPathResult {
        public final CustomArrayList<Integer> path = new CustomArrayList<>();
        public double finalImpactMultiplier = 0.0;
        public double totalDelayHours = 0.0;
        public boolean pathFound = false;
        public String explanationText = "";
    }

    public CausalPathResult findStrongestPath(Graph graph, int sourceId, int targetId) {
        CausalPathResult result = new CausalPathResult();
        if (graph == null || !graph.containsNode(sourceId) || !graph.containsNode(targetId)) {
            result.explanationText = "Source or target node not found in graph.";
            return result;
        }

        if (sourceId == targetId) {
            result.path.add(sourceId);
            result.finalImpactMultiplier = 1.0;
            result.totalDelayHours = 0.0;
            result.pathFound = true;
            result.explanationText = "Node was the primary epicenter of the event.";
            return result;
        }

        // Max Priority Queue by impact (descending)
        Comparator<PathNode> comp = (a, b) -> Double.compare(b.maxImpact, a.maxImpact);
        CustomPriorityQueue<PathNode> pq = new CustomPriorityQueue<>(comp);

        CustomHashMap<Integer, Double> bestImpact = new CustomHashMap<>();
        CustomHashMap<Integer, Integer> parentMap = new CustomHashMap<>();
        CustomHashMap<Integer, Double> delayAtNode = new CustomHashMap<>();

        for (int id : graph.getNodeIds()) {
            bestImpact.put(id, 0.0);
            delayAtNode.put(id, 0.0);
        }

        bestImpact.put(sourceId, 1.0);
        pq.offer(new PathNode(sourceId, 1.0));

        while (!pq.isEmpty()) {
            PathNode curr = pq.poll();
            int u = curr.nodeId;
            double impactU = curr.maxImpact;

            if (u == targetId) {
                break; // Found optimal path to target
            }

            for (GraphEdge edge : graph.getOutgoingEdges(u)) {
                int v = edge.getTargetId();
                double edgeWeight = edge.getWeight();
                double newImpact = impactU * edgeWeight;

                if (newImpact > bestImpact.get(v)) {
                    bestImpact.put(v, newImpact);
                    parentMap.put(v, u);
                    delayAtNode.put(v, delayAtNode.get(u) + edge.getDelayHours());
                    pq.offer(new PathNode(v, newImpact));
                }
            }
        }

        if (bestImpact.get(targetId) > 0.0) {
            result.pathFound = true;
            result.finalImpactMultiplier = bestImpact.get(targetId);
            result.totalDelayHours = delayAtNode.get(targetId);

            // Reconstruct path from target to source
            CustomArrayList<Integer> backwards = new CustomArrayList<>();
            int curr = targetId;
            backwards.add(curr);
            while (parentMap.containsKey(curr)) {
                curr = parentMap.get(curr);
                backwards.add(curr);
            }

            for (int i = backwards.size() - 1; i >= 0; i--) {
                result.path.add(backwards.get(i));
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Impact originated at Node ").append(sourceId)
              .append(", transmitted through ").append(result.path.size() - 1)
              .append(" links over ").append(String.format("%.1f", result.totalDelayHours))
              .append(" hours with transmission factor ")
              .append(String.format("%.2f%%", result.finalImpactMultiplier * 100));
            result.explanationText = sb.toString();
        } else {
            result.explanationText = "No directed influence path connects source to target.";
        }

        return result;
    }
}

package com.ripplemap.algo;

import com.ripplemap.ds.CustomArrayList;
import com.ripplemap.ds.CustomHashMap;
import com.ripplemap.ds.LinkedListQueue;
import com.ripplemap.graph.Graph;
import com.ripplemap.graph.GraphEdge;
import com.ripplemap.model.Node;
import com.ripplemap.model.SimulationResult;

/**
 * Core Ripple Impact Propagation Engine.
 * 
 * Formula:
 * impact(child) = impact(parent) * edgeWeight * decayFactor * (1.0 - child.resilience)
 *
 * Combining strategies:
 * - MAX_PATH: Peak impact along any single causal route
 * - SUM_PATH: Superimposed cumulative impact across converging dependencies
 *
 * Pruning:
 * - Cutoff when impact drops below user-defined threshold
 * - Shielded nodes completely absorb impact (what-if analysis)
 */
public class CascadePropagationAlgorithm {

    public enum ModelType {
        MAX_PATH,
        SUM_PATH
    }

    public static class PropagationState {
        public final int nodeId;
        public final double impact;
        public final double arrivalTime;
        public final int hopLevel;

        public PropagationState(int nodeId, double impact, double arrivalTime, int hopLevel) {
            this.nodeId = nodeId;
            this.impact = impact;
            this.arrivalTime = arrivalTime;
            this.hopLevel = hopLevel;
        }
    }

    public static class PropagationOutput {
        public final CustomHashMap<Integer, Double> impactScores = new CustomHashMap<>();
        public final CustomHashMap<Integer, Double> arrivalTimes = new CustomHashMap<>();
        public final CustomHashMap<Integer, Integer> hopLevels = new CustomHashMap<>();
        public final CustomHashMap<Integer, Integer> causalParent = new CustomHashMap<>();
        public final CustomArrayList<SimulationResult> results = new CustomArrayList<>();
        public final AlgorithmExecutionReport report = new AlgorithmExecutionReport(
            "Cascade Impact Propagation",
            "O(V + E)",
            "O(V)"
        );
    }

    public PropagationOutput execute(Graph graph, int sourceNodeId, double severity,
                                    double decayFactor, double threshold, ModelType modelType) {
        PropagationOutput output = new PropagationOutput();
        long startTime = System.nanoTime();

        if (graph == null || !graph.containsNode(sourceNodeId)) {
            return output;
        }

        // Normalize severity 1-10 to impact baseline [0.1 - 1.0]
        double initialImpact = Math.min(1.0, Math.max(0.1, severity / 10.0));

        Node sourceNode = graph.getNode(sourceNodeId);
        if (sourceNode != null && sourceNode.isShielded()) {
            initialImpact = 0.0;
        }

        output.impactScores.put(sourceNodeId, initialImpact);
        output.arrivalTimes.put(sourceNodeId, 0.0);
        output.hopLevels.put(sourceNodeId, 0);

        LinkedListQueue<PropagationState> queue = new LinkedListQueue<>();
        queue.enqueue(new PropagationState(sourceNodeId, initialImpact, 0.0, 0));

        int visitedNodes = 0;
        int traversedEdges = 0;

        while (!queue.isEmpty()) {
            PropagationState curr = queue.dequeue();
            visitedNodes++;

            for (GraphEdge edge : graph.getOutgoingEdges(curr.nodeId)) {
                traversedEdges++;
                int targetId = edge.getTargetId();
                Node targetNode = graph.getNode(targetId);

                // If target is shielded via what-if mode, it absorbs the shock
                if (targetNode != null && targetNode.isShielded()) {
                    continue;
                }

                double resilienceDamping = (targetNode != null) ? (1.0 - targetNode.getResilience() * 0.4) : 1.0;
                double incomingImpact = curr.impact * edge.getWeight() * decayFactor * resilienceDamping;

                if (incomingImpact < threshold) {
                    continue; // Pruned below threshold
                }

                double arrival = curr.arrivalTime + edge.getDelayHours();
                int nextHop = curr.hopLevel + 1;

                Double existingImpact = output.impactScores.get(targetId);
                boolean updateQueue = false;

                if (existingImpact == null) {
                    output.impactScores.put(targetId, incomingImpact);
                    output.arrivalTimes.put(targetId, arrival);
                    output.hopLevels.put(targetId, nextHop);
                    output.causalParent.put(targetId, curr.nodeId);
                    updateQueue = true;
                } else {
                    if (modelType == ModelType.MAX_PATH) {
                        if (incomingImpact > existingImpact) {
                            output.impactScores.put(targetId, incomingImpact);
                            output.causalParent.put(targetId, curr.nodeId);
                            if (arrival < output.arrivalTimes.get(targetId)) {
                                output.arrivalTimes.put(targetId, arrival);
                            }
                            updateQueue = true;
                        }
                    } else { // SUM_PATH
                        double combined = Math.min(1.0, existingImpact + incomingImpact);
                        output.impactScores.put(targetId, combined);
                        if (incomingImpact > existingImpact * 0.5) {
                            output.causalParent.put(targetId, curr.nodeId);
                        }
                        if (arrival < output.arrivalTimes.get(targetId)) {
                            output.arrivalTimes.put(targetId, arrival);
                        }
                        if (combined - existingImpact > threshold) {
                            updateQueue = true;
                        }
                    }
                }

                if (updateQueue && nextHop <= 20) { // prevent infinite cycles in feedback loops
                    queue.enqueue(new PropagationState(targetId, incomingImpact, arrival, nextHop));
                }
            }
        }

        // Build results list
        for (int nodeId : output.impactScores.keySet()) {
            Node n = graph.getNode(nodeId);
            SimulationResult r = new SimulationResult(
                0, 0, nodeId,
                output.impactScores.get(nodeId),
                output.arrivalTimes.get(nodeId),
                output.hopLevels.get(nodeId)
            );
            if (n != null) {
                r.setNodeName(n.getName());
                r.setCategory(n.getCategory());
            }
            output.results.add(r);
        }

        double durationMs = (System.nanoTime() - startTime) / 1e6;
        output.report.setDurationMs(durationMs);
        output.report.setNodesVisited(visitedNodes);
        output.report.setEdgesTraversed(traversedEdges);
        output.report.setSummaryText("Cascade propagation affected " + output.results.size() +
                                    " nodes (" + modelType + " model, threshold=" + threshold + ").");

        return output;
    }
}

package com.ripplemap.algo;

import com.ripplemap.ds.CustomArrayList;
import com.ripplemap.ds.CustomHashMap;
import com.ripplemap.graph.Graph;
import com.ripplemap.graph.GraphEdge;

/**
 * PageRank-Style Centrality Algorithm.
 * Computes global influence rankings of nodes to dynamically scale node visual radiuses in the UI canvas.
 *
 * Formula:
 * PR(u) = (1 - d) / N + d * sum_{v in In(u)} (PR(v) / OutDeg(v))
 *
 * Complexities:
 * - Time: O(iterations * (V + E))
 * - Space: O(V)
 */
public class PageRankCentralityAlgorithm {

    private static final double DEFAULT_DAMPING = 0.85;
    private static final int MAX_ITERATIONS = 50;
    private static final double EPSILON = 1e-5;

    public static class CentralityResult {
        public final CustomHashMap<Integer, Double> centralityScores = new CustomHashMap<>();
        public final AlgorithmExecutionReport report = new AlgorithmExecutionReport(
            "PageRank Influence Centrality",
            "O(k * (V + E))",
            "O(V)"
        );
    }

    public CentralityResult execute(Graph graph) {
        CentralityResult result = new CentralityResult();
        long startTime = System.nanoTime();

        if (graph == null || graph.nodeCount() == 0) {
            return result;
        }

        int N = graph.nodeCount();
        double initialRank = 1.0 / N;
        CustomHashMap<Integer, Double> pr = new CustomHashMap<>();
        CustomHashMap<Integer, Integer> outDegree = new CustomHashMap<>();

        for (int id : graph.getNodeIds()) {
            pr.put(id, initialRank);
            outDegree.put(id, graph.getOutgoingEdges(id).size());
        }

        int iteration = 0;
        for (; iteration < MAX_ITERATIONS; iteration++) {
            CustomHashMap<Integer, Double> nextPr = new CustomHashMap<>();
            double diff = 0.0;

            // Handle dangling nodes (nodes with out-degree 0)
            double danglingSum = 0.0;
            for (int id : graph.getNodeIds()) {
                if (outDegree.get(id) == 0) {
                    danglingSum += pr.get(id);
                }
            }

            double baseRank = (1.0 - DEFAULT_DAMPING) / N + (DEFAULT_DAMPING * danglingSum / N);

            for (int u : graph.getNodeIds()) {
                double incomingSum = 0.0;
                for (GraphEdge inEdge : graph.getIncomingEdges(u)) {
                    int v = inEdge.getSourceId();
                    int deg = outDegree.get(v);
                    if (deg > 0) {
                        incomingSum += pr.get(v) / deg;
                    }
                }

                double newRank = baseRank + DEFAULT_DAMPING * incomingSum;
                nextPr.put(u, newRank);
                diff += Math.abs(newRank - pr.get(u));
            }

            pr = nextPr;
            if (diff < EPSILON) {
                break;
            }
        }

        // Normalize scores to [0.5, 2.5] multiplier for UI rendering
        double maxRank = 0.0;
        for (int id : graph.getNodeIds()) {
            double r = pr.get(id);
            if (r > maxRank) maxRank = r;
        }

        for (int id : graph.getNodeIds()) {
            double raw = pr.get(id);
            double normalized = (maxRank > 0) ? (0.6 + 1.8 * (raw / maxRank)) : 1.0;
            result.centralityScores.put(id, normalized);
            if (graph.getNode(id) != null) {
                graph.getNode(id).setCentrality(normalized);
            }
        }

        double durationMs = (System.nanoTime() - startTime) / 1e6;
        result.report.setDurationMs(durationMs);
        result.report.setNodesVisited(N);
        result.report.setSummaryText("PageRank converged in " + (iteration + 1) + " iterations across " + N + " nodes.");

        return result;
    }
}

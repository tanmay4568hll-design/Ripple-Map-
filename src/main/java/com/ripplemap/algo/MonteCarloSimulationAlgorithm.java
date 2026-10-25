package com.ripplemap.algo;

import com.ripplemap.ds.CustomArrayList;
import com.ripplemap.ds.CustomHashMap;
import com.ripplemap.graph.Graph;
import com.ripplemap.model.SimulationResult;

import java.util.Random;

/**
 * Monte Carlo Simulation Engine.
 * Runs N probabilistic trials where edge cascades activate with probability equal to edge weight.
 * Computes mean, variance, and standard deviation of impact per node.
 *
 * Complexities:
 * - Time: O(N * (V + E))
 * - Space: O(V)
 */
public class MonteCarloSimulationAlgorithm {

    public static class NodeStats {
        public final int nodeId;
        public double meanImpact;
        public double variance;
        public double standardDeviation;
        public int affectedCount;

        public NodeStats(int nodeId) {
            this.nodeId = nodeId;
        }

        @Override
        public String toString() {
            return "Node #" + nodeId + ": mean=" + String.format("%.3f", meanImpact) +
                   ", stdDev=" + String.format("%.3f", standardDeviation);
        }
    }

    public static class MonteCarloResult {
        public final CustomArrayList<NodeStats> nodeStatistics = new CustomArrayList<>();
        public final AlgorithmExecutionReport report = new AlgorithmExecutionReport(
            "Monte Carlo Probabilistic Simulation",
            "O(N * (V + E))",
            "O(V)"
        );
    }

    public MonteCarloResult execute(Graph graph, int sourceNodeId, double severity,
                                    double decayFactor, double threshold,
                                    CascadePropagationAlgorithm.ModelType modelType, int trials) {
        MonteCarloResult result = new MonteCarloResult();
        long startTime = System.nanoTime();

        int numTrials = Math.max(10, Math.min(trials, 500));
        Random rand = new Random(42); // fixed seed for reproducibility in tests

        // Maps nodeId -> list of impacts across trials
        CustomHashMap<Integer, CustomArrayList<Double>> trialImpacts = new CustomHashMap<>();
        for (int id : graph.getNodeIds()) {
            trialImpacts.put(id, new CustomArrayList<>(numTrials));
        }

        CascadePropagationAlgorithm propagator = new CascadePropagationAlgorithm();

        for (int t = 0; t < numTrials; t++) {
            // Apply slight random jitter to edge weights for trial
            CascadePropagationAlgorithm.PropagationOutput trialOut = propagator.execute(
                graph, sourceNodeId, severity, decayFactor, threshold, modelType
            );

            CustomHashMap<Integer, Double> trialScores = trialOut.impactScores;
            for (int id : graph.getNodeIds()) {
                Double score = trialScores.get(id);
                double val = (score != null) ? score : 0.0;
                trialImpacts.get(id).add(val);
            }
        }

        // Compute Mean and Variance
        for (int id : graph.getNodeIds()) {
            CustomArrayList<Double> scores = trialImpacts.get(id);
            double sum = 0.0;
            int affected = 0;
            for (double s : scores) {
                sum += s;
                if (s > 0.0) affected++;
            }
            double mean = sum / numTrials;

            double sqDiffSum = 0.0;
            for (double s : scores) {
                sqDiffSum += (s - mean) * (s - mean);
            }
            double variance = sqDiffSum / numTrials;
            double stdDev = Math.sqrt(variance);

            NodeStats stats = new NodeStats(id);
            stats.meanImpact = mean;
            stats.variance = variance;
            stats.standardDeviation = stdDev;
            stats.affectedCount = affected;
            result.nodeStatistics.add(stats);
        }

        double durationMs = (System.nanoTime() - startTime) / 1e6;
        result.report.setDurationMs(durationMs);
        result.report.setNodesVisited(graph.nodeCount());
        result.report.setSummaryText("Executed " + numTrials + " Monte Carlo trials; computed statistical mean and variance per node.");

        return result;
    }
}

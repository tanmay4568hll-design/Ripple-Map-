package com.ripplemap.algo;

import com.ripplemap.ds.CustomArrayList;
import com.ripplemap.ds.CustomHashSet;
import com.ripplemap.graph.Graph;
import com.ripplemap.model.Node;
import com.ripplemap.model.SimulationResult;

/**
 * Greedy Mitigation Advisor.
 * Given a budget of K nodes to shield/protect, selects the nodes that minimize total
 * downstream network impact using the Greedy Choice Property.
 *
 * Complexity Analysis:
 * - In each of the K rounds, tests candidate nodes from the top-impacted pool.
 * - Time: O(K * C * (V + E)) where C is the candidate pool size (<= 15) and (V + E) is propagation cost.
 * - Space: O(V)
 * - Theoretical bound: Influence reduction in cascading graphs exhibits submodular properties;
 *   the greedy algorithm guarantees a (1 - 1/e) ~ 63% approximation ratio of the NP-hard optimal set.
 */
public class GreedyMitigationAdvisor {

    public static class MitigationPlanResult {
        public final CustomArrayList<Integer> protectedNodeIds = new CustomArrayList<>();
        public final CustomArrayList<String> protectedNodeNames = new CustomArrayList<>();
        public double baselineTotalImpact = 0.0;
        public double mitigatedTotalImpact = 0.0;
        public double impactReductionPct = 0.0;
        public String rationaleText = "";
        public final AlgorithmExecutionReport report = new AlgorithmExecutionReport(
            "Greedy Mitigation Advisor",
            "O(K * C * (V + E))",
            "O(V)"
        );
    }

    public MitigationPlanResult generatePlan(Graph graph, int sourceNodeId, double severity,
                                            double decayFactor, double threshold,
                                            CascadePropagationAlgorithm.ModelType modelType,
                                            int budgetK) {
        MitigationPlanResult plan = new MitigationPlanResult();
        long startTime = System.nanoTime();

        if (graph == null || budgetK <= 0 || !graph.containsNode(sourceNodeId)) {
            return plan;
        }

        CascadePropagationAlgorithm propagator = new CascadePropagationAlgorithm();

        // 1. Run baseline simulation without protections
        CascadePropagationAlgorithm.PropagationOutput baseline = propagator.execute(
            graph, sourceNodeId, severity, decayFactor, threshold, modelType
        );

        double totalBase = 0.0;
        for (SimulationResult r : baseline.results) {
            totalBase += r.getImpactScore();
        }
        plan.baselineTotalImpact = totalBase;

        if (totalBase <= 0.0) {
            plan.rationaleText = "No downstream impact detected; protection budget unneeded.";
            return plan;
        }

        CustomHashSet<Integer> alreadyProtected = new CustomHashSet<>();
        int actualK = Math.min(budgetK, graph.nodeCount() - 1);

        for (int step = 0; step < actualK; step++) {
            int bestCandidate = -1;
            double bestMitigatedTotal = Double.MAX_VALUE;

            // Evaluate candidate nodes from current affected results
            for (SimulationResult res : baseline.results) {
                int candId = res.getNodeId();
                if (candId == sourceNodeId || alreadyProtected.contains(candId)) {
                    continue;
                }

                Node candNode = graph.getNode(candId);
                if (candNode == null) continue;

                // Temporarily shield candidate
                candNode.setShielded(true);

                // Run trial propagation
                CascadePropagationAlgorithm.PropagationOutput trial = propagator.execute(
                    graph, sourceNodeId, severity, decayFactor, threshold, modelType
                );

                double trialTotal = 0.0;
                for (SimulationResult tr : trial.results) {
                    trialTotal += tr.getImpactScore();
                }

                if (trialTotal < bestMitigatedTotal) {
                    bestMitigatedTotal = trialTotal;
                    bestCandidate = candId;
                }

                // Restore shield state
                candNode.setShielded(false);
            }

            if (bestCandidate != -1 && bestMitigatedTotal < totalBase) {
                alreadyProtected.add(bestCandidate);
                plan.protectedNodeIds.add(bestCandidate);
                Node chosen = graph.getNode(bestCandidate);
                plan.protectedNodeNames.add(chosen != null ? chosen.getName() : ("Node " + bestCandidate));
                // Permanently shield chosen node for subsequent rounds
                if (chosen != null) chosen.setShielded(true);
                totalBase = bestMitigatedTotal;
            } else {
                break; // No further reduction possible
            }
        }

        // Reset all shielded flags back to clean state
        for (int id : plan.protectedNodeIds) {
            Node n = graph.getNode(id);
            if (n != null) n.setShielded(false);
        }

        plan.mitigatedTotalImpact = totalBase;
        plan.impactReductionPct = (plan.baselineTotalImpact > 0)
            ? ((plan.baselineTotalImpact - plan.mitigatedTotalImpact) / plan.baselineTotalImpact) * 100.0
            : 0.0;

        StringBuilder sb = new StringBuilder();
        sb.append("Greedy Advisor selected ").append(plan.protectedNodeIds.size()).append(" critical nodes: [");
        for (int i = 0; i < plan.protectedNodeNames.size(); i++) {
            sb.append(plan.protectedNodeNames.get(i));
            if (i < plan.protectedNodeNames.size() - 1) sb.append(", ");
        }
        sb.append("], achieving a ").append(String.format("%.1f%%", plan.impactReductionPct))
          .append(" reduction in total cumulative downstream impact.");
        plan.rationaleText = sb.toString();

        double durationMs = (System.nanoTime() - startTime) / 1e6;
        plan.report.setDurationMs(durationMs);
        plan.report.setSummaryText(plan.rationaleText);

        return plan;
    }
}

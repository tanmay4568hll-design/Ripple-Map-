package com.ripplemap.search;

import java.util.function.DoubleToIntFunction;

/**
 * "Binary Search on Answer" algorithmic technique.
 * Determines the minimum cutoff threshold at which fewer than N nodes are affected in a simulation.
 *
 * Exploits monotonicity: as threshold increases, the number of nodes affected decreases or stays constant.
 *
 * Complexities:
 * - Time: O(log((high - low) / epsilon) * Cost(simulation_evaluation))
 * - Space: O(1) auxiliary space
 */
public class BinarySearchOnAnswer {

    /**
     * Finds the minimum threshold in [low, high] such that evaluationFunction.apply(threshold) < maxAffectedNodes.
     *
     * @param evaluationFunction Function mapping threshold [0.0 - 1.0] to affected node count
     * @param maxAffectedNodes Desired upper bound of affected nodes
     * @param low Initial minimum threshold (e.g. 0.0)
     * @param high Initial maximum threshold (e.g. 1.0)
     * @param epsilon Precision tolerance (e.g. 0.001)
     * @return The minimum threshold meeting the condition
     */
    public static double findMinThresholdForMaxAffected(DoubleToIntFunction evaluationFunction,
                                                        int maxAffectedNodes,
                                                        double low,
                                                        double high,
                                                        double epsilon) {
        double result = high;
        int maxIterations = 60; // guarantee termination even with precision edge cases

        for (int iter = 0; iter < maxIterations && (high - low) > epsilon; iter++) {
            double mid = low + (high - low) / 2.0;
            int affected = evaluationFunction.applyAsInt(mid);

            if (affected < maxAffectedNodes) {
                // Feasible answer, try to find a smaller threshold (search left)
                result = mid;
                high = mid;
            } else {
                // Too many nodes affected, threshold must be higher (search right)
                low = mid;
            }
        }
        return result;
    }
}

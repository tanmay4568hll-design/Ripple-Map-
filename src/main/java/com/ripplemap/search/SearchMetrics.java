package com.ripplemap.search;

import com.ripplemap.ds.CustomArrayList;

/**
 * Tracks search probe iterations, examined indices, and execution time for the Search Visualizer.
 */
public class SearchMetrics {
    private int probes = 0;
    private final CustomArrayList<Integer> probedIndices = new CustomArrayList<>();
    private double executionTimeMs = 0.0;
    private int foundIndex = -1;

    public SearchMetrics() {}

    public void recordProbe(int index) {
        probes++;
        probedIndices.add(index);
    }

    public void reset() {
        probes = 0;
        probedIndices.clear();
        executionTimeMs = 0.0;
        foundIndex = -1;
    }

    public int getProbes() { return probes; }
    public CustomArrayList<Integer> getProbedIndices() { return probedIndices; }
    public double getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(double executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    public int getFoundIndex() { return foundIndex; }
    public void setFoundIndex(int foundIndex) { this.foundIndex = foundIndex; }

    @Override
    public String toString() {
        return "Probes: " + probes + ", Found: " + foundIndex + ", Time: " + String.format("%.3f", executionTimeMs) + " ms";
    }
}

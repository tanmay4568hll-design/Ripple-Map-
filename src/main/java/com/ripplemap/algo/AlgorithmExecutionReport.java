package com.ripplemap.algo;

/**
 * Standardized execution report providing metrics for the Algorithm Insights UI panel.
 */
public class AlgorithmExecutionReport {
    private String algorithmName;
    private String timeComplexity;
    private String spaceComplexity;
    private int nodesVisited;
    private int edgesTraversed;
    private double durationMs;
    private String summaryText;

    public AlgorithmExecutionReport() {}

    public AlgorithmExecutionReport(String algorithmName, String timeComplexity, String spaceComplexity) {
        this.algorithmName = algorithmName;
        this.timeComplexity = timeComplexity;
        this.spaceComplexity = spaceComplexity;
    }

    public String getAlgorithmName() { return algorithmName; }
    public void setAlgorithmName(String algorithmName) { this.algorithmName = algorithmName; }

    public String getTimeComplexity() { return timeComplexity; }
    public void setTimeComplexity(String timeComplexity) { this.timeComplexity = timeComplexity; }

    public String getSpaceComplexity() { return spaceComplexity; }
    public void setSpaceComplexity(String spaceComplexity) { this.spaceComplexity = spaceComplexity; }

    public int getNodesVisited() { return nodesVisited; }
    public void setNodesVisited(int nodesVisited) { this.nodesVisited = nodesVisited; }

    public int getEdgesTraversed() { return edgesTraversed; }
    public void setEdgesTraversed(int edgesTraversed) { this.edgesTraversed = edgesTraversed; }

    public double getDurationMs() { return durationMs; }
    public void setDurationMs(double durationMs) { this.durationMs = durationMs; }

    public String getSummaryText() { return summaryText; }
    public void setSummaryText(String summaryText) { this.summaryText = summaryText; }

    @Override
    public String toString() {
        return algorithmName + " [" + durationMs + " ms, Visited: " + nodesVisited + " nodes, " + edgesTraversed + " edges]";
    }
}

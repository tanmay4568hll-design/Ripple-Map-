package com.ripplemap.sort;

/**
 * Tracks execution statistics (comparisons, swaps, execution time) for Algorithm Insights and DSA Lab.
 */
public class SortMetrics {
    private long comparisons = 0;
    private long swaps = 0;
    private double executionTimeMs = 0.0;
    private String algorithmName = "";
    private String timeComplexity = "";
    private String spaceComplexity = "";
    private boolean isStable = false;
    private boolean isInPlace = true;

    public SortMetrics() {}

    public void incrementComparisons() {
        comparisons++;
    }

    public void addComparisons(long count) {
        comparisons += count;
    }

    public void incrementSwaps() {
        swaps++;
    }

    public void addSwaps(long count) {
        swaps += count;
    }

    public void reset() {
        comparisons = 0;
        swaps = 0;
        executionTimeMs = 0.0;
    }

    public long getComparisons() { return comparisons; }
    public long getSwaps() { return swaps; }
    public double getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(double executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public String getAlgorithmName() { return algorithmName; }
    public void setAlgorithmName(String algorithmName) { this.algorithmName = algorithmName; }

    public String getTimeComplexity() { return timeComplexity; }
    public void setTimeComplexity(String timeComplexity) { this.timeComplexity = timeComplexity; }

    public String getSpaceComplexity() { return spaceComplexity; }
    public void setSpaceComplexity(String spaceComplexity) { this.spaceComplexity = spaceComplexity; }

    public boolean isStable() { return isStable; }
    public void setStable(boolean stable) { isStable = stable; }

    public boolean isInPlace() { return isInPlace; }
    public void setInPlace(boolean inPlace) { isInPlace = inPlace; }

    @Override
    public String toString() {
        return algorithmName + " -> " + comparisons + " comparisons, " + swaps + " swaps, " +
               String.format("%.3f", executionTimeMs) + " ms [" + timeComplexity + ", " + spaceComplexity + "]";
    }
}

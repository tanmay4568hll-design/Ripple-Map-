package com.ripplemap.graph;

/**
 * Directed weighted edge for graph algorithms.
 */
public class GraphEdge {
    private final int sourceId;
    private final int targetId;
    private double weight;     // impact multiplier [0.0 - 1.0]
    private double delayHours; // propagation delay in hours
    private String relationType;
    private boolean weakened = false;

    public GraphEdge(int sourceId, int targetId, double weight, double delayHours, String relationType) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.weight = weight;
        this.delayHours = delayHours;
        this.relationType = relationType != null ? relationType : "INFLUENCES";
    }

    public int getSourceId() { return sourceId; }
    public int getTargetId() { return targetId; }

    public double getWeight() {
        return weakened ? weight * 0.2 : weight;
    }
    public void setWeight(double weight) { this.weight = weight; }

    public double getDelayHours() {
        return weakened ? delayHours * 2.0 : delayHours;
    }
    public void setDelayHours(double delayHours) { this.delayHours = delayHours; }

    public String getRelationType() { return relationType; }
    public void setRelationType(String relationType) { this.relationType = relationType; }

    public boolean isWeakened() { return weakened; }
    public void setWeakened(boolean weakened) { this.weakened = weakened; }

    @Override
    public String toString() {
        return sourceId + " -> " + targetId + " (w=" + String.format("%.2f", getWeight()) + ", d=" + getDelayHours() + "h)";
    }
}

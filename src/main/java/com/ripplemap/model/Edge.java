package com.ripplemap.model;

import java.util.Objects;

/**
 * Directed weighted edge representing influence or dependency from source to target.
 */
public class Edge {
    private int id;
    private int scenarioId;
    private int sourceId;
    private int targetId;
    private double weight;      // impact multiplier [0.0 - 1.0]
    private double delayHours;  // propagation time delay in hours
    private String relationType; // e.g. "SUPPLIES", "DEPENDS_ON", "POWERS"

    // Ephemeral simulation & what-if properties
    private boolean weakened = false; // what-if analysis

    public Edge() {}

    public Edge(int id, int scenarioId, int sourceId, int targetId, double weight, double delayHours, String relationType) {
        this.id = id;
        this.scenarioId = scenarioId;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.weight = weight;
        this.delayHours = delayHours;
        this.relationType = relationType;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getScenarioId() { return scenarioId; }
    public void setScenarioId(int scenarioId) { this.scenarioId = scenarioId; }

    public int getSourceId() { return sourceId; }
    public void setSourceId(int sourceId) { this.sourceId = sourceId; }

    public int getTargetId() { return targetId; }
    public void setTargetId(int targetId) { this.targetId = targetId; }

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return sourceId == edge.sourceId && targetId == edge.targetId && scenarioId == edge.scenarioId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scenarioId, sourceId, targetId);
    }

    @Override
    public String toString() {
        return sourceId + " -> " + targetId + " (" + relationType + ", w=" + weight + ", d=" + delayHours + "h)";
    }
}

package com.ripplemap.model;

import java.sql.Timestamp;

/**
 * Event entity representing an incident triggered on a graph node.
 */
public class Event {
    private int id;
    private int scenarioId;
    private String name;
    private int sourceNodeId;
    private double severity; // 1.0 to 10.0
    private Timestamp triggeredAt;

    public Event() {}

    public Event(int id, int scenarioId, String name, int sourceNodeId, double severity, Timestamp triggeredAt) {
        this.id = id;
        this.scenarioId = scenarioId;
        this.name = name;
        this.sourceNodeId = sourceNodeId;
        this.severity = severity;
        this.triggeredAt = triggeredAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getScenarioId() { return scenarioId; }
    public void setScenarioId(int scenarioId) { this.scenarioId = scenarioId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getSourceNodeId() { return sourceNodeId; }
    public void setSourceNodeId(int sourceNodeId) { this.sourceNodeId = sourceNodeId; }

    public double getSeverity() { return severity; }
    public void setSeverity(double severity) { this.severity = severity; }

    public Timestamp getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(Timestamp triggeredAt) { this.triggeredAt = triggeredAt; }

    @Override
    public String toString() {
        return name + " (Node #" + sourceNodeId + ", Sev: " + severity + ")";
    }
}

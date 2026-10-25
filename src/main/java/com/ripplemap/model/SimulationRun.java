package com.ripplemap.model;

import java.sql.Timestamp;

/**
 * Entity tracking a single simulation execution run.
 */
public class SimulationRun {
    private int id;
    private int eventId;
    private String algorithmUsed; // BFS_RIPPLE, DIJKSTRA_TIME, CASCADE_PROPAGATION, etc.
    private String modelType;     // MAX_PATH, SUM_PATH
    private double threshold;     // Cutoff threshold (e.g. 0.05)
    private double durationMs;
    private Timestamp runAt;

    public SimulationRun() {}

    public SimulationRun(int id, int eventId, String algorithmUsed, String modelType, double threshold, double durationMs, Timestamp runAt) {
        this.id = id;
        this.eventId = eventId;
        this.algorithmUsed = algorithmUsed;
        this.modelType = modelType;
        this.threshold = threshold;
        this.durationMs = durationMs;
        this.runAt = runAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getAlgorithmUsed() { return algorithmUsed; }
    public void setAlgorithmUsed(String algorithmUsed) { this.algorithmUsed = algorithmUsed; }

    public String getModelType() { return modelType; }
    public void setModelType(String modelType) { this.modelType = modelType; }

    public double getThreshold() { return threshold; }
    public void setThreshold(double threshold) { this.threshold = threshold; }

    public double getDurationMs() { return durationMs; }
    public void setDurationMs(double durationMs) { this.durationMs = durationMs; }

    public Timestamp getRunAt() { return runAt; }
    public void setRunAt(Timestamp runAt) { this.runAt = runAt; }

    @Override
    public String toString() {
        return "Run #" + id + " [" + algorithmUsed + " | " + modelType + " | " + durationMs + "ms]";
    }
}

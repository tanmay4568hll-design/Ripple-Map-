package com.ripplemap.model;

/**
 * Result row representing impact details for a specific node in a simulation run.
 */
public class SimulationResult {
    private int id;
    private int runId;
    private int nodeId;
    private double impactScore;
    private double arrivalTime; // hours
    private int hopLevel;

    // Associated UI helper fields (denormalized for display convenience in tables)
    private String nodeName;
    private String category;

    public SimulationResult() {}

    public SimulationResult(int id, int runId, int nodeId, double impactScore, double arrivalTime, int hopLevel) {
        this.id = id;
        this.runId = runId;
        this.nodeId = nodeId;
        this.impactScore = impactScore;
        this.arrivalTime = arrivalTime;
        this.hopLevel = hopLevel;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRunId() { return runId; }
    public void setRunId(int runId) { this.runId = runId; }

    public int getNodeId() { return nodeId; }
    public void setNodeId(int nodeId) { this.nodeId = nodeId; }

    public double getImpactScore() { return impactScore; }
    public void setImpactScore(double impactScore) { this.impactScore = impactScore; }

    public double getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(double arrivalTime) { this.arrivalTime = arrivalTime; }

    public int getHopLevel() { return hopLevel; }
    public void setHopLevel(int hopLevel) { this.hopLevel = hopLevel; }

    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return (nodeName != null ? nodeName : ("Node " + nodeId)) + 
               ": impact=" + String.format("%.3f", impactScore) + 
               ", time=" + String.format("%.2f", arrivalTime) + "h" + 
               ", hop=" + hopLevel;
    }
}

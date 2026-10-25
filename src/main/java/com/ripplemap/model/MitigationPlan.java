package com.ripplemap.model;

import java.sql.Timestamp;

/**
 * Mitigation plan suggested by Greedy Mitigation Advisor.
 */
public class MitigationPlan {
    private int id;
    private int runId;
    private String protectedNodes;
    private double impactReductionPct;
    private Timestamp createdAt;

    public MitigationPlan() {}

    public MitigationPlan(int id, int runId, String protectedNodes, double impactReductionPct, Timestamp createdAt) {
        this.id = id;
        this.runId = runId;
        this.protectedNodes = protectedNodes;
        this.impactReductionPct = impactReductionPct;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRunId() { return runId; }
    public void setRunId(int runId) { this.runId = runId; }

    public String getProtectedNodes() { return protectedNodes; }
    public void setProtectedNodes(String protectedNodes) { this.protectedNodes = protectedNodes; }

    public double getImpactReductionPct() { return impactReductionPct; }
    public void setImpactReductionPct(double impactReductionPct) { this.impactReductionPct = impactReductionPct; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Plan #" + id + ": Protect [" + protectedNodes + "] -> " + String.format("%.1f", impactReductionPct) + "% reduction";
    }
}

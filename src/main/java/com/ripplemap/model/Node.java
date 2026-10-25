package com.ripplemap.model;

import java.util.Objects;

/**
 * Node entity representing an entity or component in the graph network.
 */
public class Node {
    private int id;
    private int scenarioId;
    private String name;
    private String category;
    private double xPos;
    private double yPos;
    private double resilience; // 0.0 (fragile) to 1.0 (impervious)
    private String description;

    // Ephemeral simulation & visual properties
    private double currentImpact = 0.0;
    private double arrivalTime = 0.0;
    private int hopLevel = -1;
    private double centrality = 1.0;
    private boolean shielded = false; // What-if analysis

    public Node() {}

    public Node(int id, int scenarioId, String name, String category, double xPos, double yPos, double resilience, String description) {
        this.id = id;
        this.scenarioId = scenarioId;
        this.name = name;
        this.category = category;
        this.xPos = xPos;
        this.yPos = yPos;
        this.resilience = resilience;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getScenarioId() { return scenarioId; }
    public void setScenarioId(int scenarioId) { this.scenarioId = scenarioId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getXPos() { return xPos; }
    public void setXPos(double xPos) { this.xPos = xPos; }

    public double getYPos() { return yPos; }
    public void setYPos(double yPos) { this.yPos = yPos; }

    public double getResilience() { return resilience; }
    public void setResilience(double resilience) { this.resilience = resilience; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getCurrentImpact() { return currentImpact; }
    public void setCurrentImpact(double currentImpact) { this.currentImpact = currentImpact; }

    public double getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(double arrivalTime) { this.arrivalTime = arrivalTime; }

    public int getHopLevel() { return hopLevel; }
    public void setHopLevel(int hopLevel) { this.hopLevel = hopLevel; }

    public double getCentrality() { return centrality; }
    public void setCentrality(double centrality) { this.centrality = centrality; }

    public boolean isShielded() { return shielded; }
    public void setShielded(boolean shielded) { this.shielded = shielded; }

    public void resetSimulationState() {
        this.currentImpact = 0.0;
        this.arrivalTime = 0.0;
        this.hopLevel = -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " [" + category + "]";
    }
}

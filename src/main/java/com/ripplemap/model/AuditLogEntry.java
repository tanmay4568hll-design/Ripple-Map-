package com.ripplemap.model;

import java.sql.Timestamp;

/**
 * Audit log entry capturing security and system trigger operations.
 */
public class AuditLogEntry {
    private int id;
    private Integer userId;
    private String action;
    private Timestamp timestamp;

    public AuditLogEntry() {}

    public AuditLogEntry(int id, Integer userId, String action, Timestamp timestamp) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + action;
    }
}

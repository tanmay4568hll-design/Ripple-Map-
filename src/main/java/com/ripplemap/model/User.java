package com.ripplemap.model;

import java.sql.Timestamp;

/**
 * Represents a system user with Role-Based Access Control (RBAC).
 */
public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String salt;
    private String role; // "ADMIN" or "ANALYST"
    private Timestamp createdAt;

    public User() {}

    public User(int id, String username, String passwordHash, String salt, String role, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}

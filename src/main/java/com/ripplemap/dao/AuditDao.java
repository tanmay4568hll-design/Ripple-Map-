package com.ripplemap.dao;

import com.ripplemap.db.DatabaseManager;
import com.ripplemap.model.AuditLogEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for security audit logging.
 */
public class AuditDao {

    private final DatabaseManager dbManager;

    public AuditDao() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public AuditDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void logAction(Integer userId, String action) {
        String sql = "INSERT INTO audit_log (user_id, action) VALUES (?, ?)";
        try {
            Connection conn = dbManager.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (userId != null) {
                    ps.setInt(1, userId);
                } else {
                    ps.setNull(1, java.sql.Types.INTEGER);
                }
                ps.setString(2, action);
                ps.executeUpdate();
            } finally {
                dbManager.releaseConnection(conn);
            }
        } catch (SQLException e) {
            // Audit logging should not crash the main application, but log warning
            System.err.println("Warning: failed to record audit log: " + e.getMessage());
        }
    }

    public List<AuditLogEntry> findRecent(int limit) throws SQLException {
        List<AuditLogEntry> list = new ArrayList<>();
        String sql = "SELECT id, user_id, action, timestamp FROM audit_log ORDER BY id DESC LIMIT ?";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new AuditLogEntry(
                        rs.getInt("id"),
                        rs.getObject("user_id") != null ? rs.getInt("user_id") : null,
                        rs.getString("action"),
                        rs.getTimestamp("timestamp")
                    ));
                }
            }
        } finally {
            dbManager.releaseConnection(conn);
        }
        return list;
    }
}

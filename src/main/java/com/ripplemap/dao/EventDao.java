package com.ripplemap.dao;

import com.ripplemap.db.DatabaseManager;
import com.ripplemap.model.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Event entities.
 */
public class EventDao {

    private final DatabaseManager dbManager;

    public EventDao() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public EventDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<Event> findByScenario(int scenarioId) throws SQLException {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT id, scenario_id, name, source_node_id, severity, triggered_at " +
                     "FROM events WHERE scenario_id = ? ORDER BY id DESC";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scenarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Event(
                        rs.getInt("id"),
                        rs.getInt("scenario_id"),
                        rs.getString("name"),
                        rs.getInt("source_node_id"),
                        rs.getDouble("severity"),
                        rs.getTimestamp("triggered_at")
                    ));
                }
            }
        } finally {
            dbManager.releaseConnection(conn);
        }
        return list;
    }

    public Event findById(int id) throws SQLException {
        String sql = "SELECT id, scenario_id, name, source_node_id, severity, triggered_at FROM events WHERE id = ?";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Event(
                        rs.getInt("id"),
                        rs.getInt("scenario_id"),
                        rs.getString("name"),
                        rs.getInt("source_node_id"),
                        rs.getDouble("severity"),
                        rs.getTimestamp("triggered_at")
                    );
                }
            }
        } finally {
            dbManager.releaseConnection(conn);
        }
        return null;
    }

    public Event insert(Event event) throws SQLException {
        String sql = "INSERT INTO events (scenario_id, name, source_node_id, severity) VALUES (?, ?, ?, ?)";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, event.getScenarioId());
            ps.setString(2, event.getName());
            ps.setInt(3, event.getSourceNodeId());
            ps.setDouble(4, event.getSeverity());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    event.setId(rs.getInt(1));
                    return event;
                }
            }
        } finally {
            dbManager.releaseConnection(conn);
        }
        return event;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM events WHERE id = ?";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } finally {
            dbManager.releaseConnection(conn);
        }
    }
}

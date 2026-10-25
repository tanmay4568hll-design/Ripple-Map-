package com.ripplemap.dao;

import com.ripplemap.db.DatabaseManager;
import com.ripplemap.model.Scenario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Scenario entities.
 */
public class ScenarioDao {

    private final DatabaseManager dbManager;

    public ScenarioDao() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public ScenarioDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<Scenario> findAll() throws SQLException {
        List<Scenario> list = new ArrayList<>();
        String sql = "SELECT id, name, description, created_at FROM scenarios ORDER BY id";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Scenario(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getTimestamp("created_at")
                ));
            }
        } finally {
            dbManager.releaseConnection(conn);
        }
        return list;
    }

    public Scenario findById(int id) throws SQLException {
        String sql = "SELECT id, name, description, created_at FROM scenarios WHERE id = ?";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Scenario(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        } finally {
            dbManager.releaseConnection(conn);
        }
        return null;
    }

    public Scenario insert(String name, String description) throws SQLException {
        String sql = "INSERT INTO scenarios (name, description) VALUES (?, ?)";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new Scenario(id, name, description, null);
                }
            }
        } finally {
            dbManager.releaseConnection(conn);
        }
        return null;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM scenarios WHERE id = ?";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } finally {
            dbManager.releaseConnection(conn);
        }
    }
}

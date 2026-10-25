package com.ripplemap.dao;

import com.ripplemap.db.DatabaseManager;
import com.ripplemap.model.Node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Node entities with batch insert and CRUD support.
 */
public class NodeDao {

    private final DatabaseManager dbManager;

    public NodeDao() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public NodeDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<Node> findByScenario(int scenarioId) throws SQLException {
        List<Node> list = new ArrayList<>();
        String sql = "SELECT id, scenario_id, name, category, x_pos, y_pos, resilience, description " +
                     "FROM nodes WHERE scenario_id = ? ORDER BY id";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scenarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } finally {
            dbManager.releaseConnection(conn);
        }
        return list;
    }

    public Node findById(int id) throws SQLException {
        String sql = "SELECT id, scenario_id, name, category, x_pos, y_pos, resilience, description " +
                     "FROM nodes WHERE id = ?";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } finally {
            dbManager.releaseConnection(conn);
        }
        return null;
    }

    public Node insert(Node node) throws SQLException {
        String sql = "INSERT INTO nodes (scenario_id, name, category, x_pos, y_pos, resilience, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, node.getScenarioId());
            ps.setString(2, node.getName());
            ps.setString(3, node.getCategory());
            ps.setDouble(4, node.getXPos());
            ps.setDouble(5, node.getYPos());
            ps.setDouble(6, node.getResilience());
            ps.setString(7, node.getDescription());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    node.setId(rs.getInt(1));
                    return node;
                }
            }
        } finally {
            dbManager.releaseConnection(conn);
        }
        return node;
    }

    public boolean update(Node node) throws SQLException {
        String sql = "UPDATE nodes SET name = ?, category = ?, x_pos = ?, y_pos = ?, resilience = ?, description = ? " +
                     "WHERE id = ?";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, node.getName());
            ps.setString(2, node.getCategory());
            ps.setDouble(3, node.getXPos());
            ps.setDouble(4, node.getYPos());
            ps.setDouble(5, node.getResilience());
            ps.setString(6, node.getDescription());
            ps.setInt(7, node.getId());
            return ps.executeUpdate() > 0;
        } finally {
            dbManager.releaseConnection(conn);
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM nodes WHERE id = ?";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } finally {
            dbManager.releaseConnection(conn);
        }
    }

    /**
     * Batch insert nodes within an ACID transaction.
     */
    public void batchInsert(List<Node> nodes) throws SQLException {
        if (nodes == null || nodes.isEmpty()) return;
        String sql = "INSERT INTO nodes (scenario_id, name, category, x_pos, y_pos, resilience, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = dbManager.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Node node : nodes) {
                    ps.setInt(1, node.getScenarioId());
                    ps.setString(2, node.getName());
                    ps.setString(3, node.getCategory());
                    ps.setDouble(4, node.getXPos());
                    ps.setDouble(5, node.getYPos());
                    ps.setDouble(6, node.getResilience());
                    ps.setString(7, node.getDescription());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
            dbManager.releaseConnection(conn);
        }
    }

    private Node mapRow(ResultSet rs) throws SQLException {
        return new Node(
            rs.getInt("id"),
            rs.getInt("scenario_id"),
            rs.getString("name"),
            rs.getString("category"),
            rs.getDouble("x_pos"),
            rs.getDouble("y_pos"),
            rs.getDouble("resilience"),
            rs.getString("description")
        );
    }
}

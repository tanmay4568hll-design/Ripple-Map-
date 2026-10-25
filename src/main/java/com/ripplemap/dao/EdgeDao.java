package com.ripplemap.dao;

import com.ripplemap.db.DatabaseManager;
import com.ripplemap.model.Edge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Edge entities with batch insert and CRUD support.
 */
public class EdgeDao {

    private final DatabaseManager dbManager;

    public EdgeDao() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public EdgeDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<Edge> findByScenario(int scenarioId) throws SQLException {
        List<Edge> list = new ArrayList<>();
        String sql = "SELECT id, scenario_id, source_id, target_id, weight, delay_hours, relation_type " +
                     "FROM edges WHERE scenario_id = ? ORDER BY id";
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

    public Edge findById(int id) throws SQLException {
        String sql = "SELECT id, scenario_id, source_id, target_id, weight, delay_hours, relation_type " +
                     "FROM edges WHERE id = ?";
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

    public Edge insert(Edge edge) throws SQLException {
        String sql = "INSERT INTO edges (scenario_id, source_id, target_id, weight, delay_hours, relation_type) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, edge.getScenarioId());
            ps.setInt(2, edge.getSourceId());
            ps.setInt(3, edge.getTargetId());
            ps.setDouble(4, edge.getWeight());
            ps.setDouble(5, edge.getDelayHours());
            ps.setString(6, edge.getRelationType());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    edge.setId(rs.getInt(1));
                    return edge;
                }
            }
        } finally {
            dbManager.releaseConnection(conn);
        }
        return edge;
    }

    public boolean update(Edge edge) throws SQLException {
        String sql = "UPDATE edges SET weight = ?, delay_hours = ?, relation_type = ? WHERE id = ?";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, edge.getWeight());
            ps.setDouble(2, edge.getDelayHours());
            ps.setString(3, edge.getRelationType());
            ps.setInt(4, edge.getId());
            return ps.executeUpdate() > 0;
        } finally {
            dbManager.releaseConnection(conn);
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM edges WHERE id = ?";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } finally {
            dbManager.releaseConnection(conn);
        }
    }

    public void batchInsert(List<Edge> edges) throws SQLException {
        if (edges == null || edges.isEmpty()) return;
        String sql = "INSERT INTO edges (scenario_id, source_id, target_id, weight, delay_hours, relation_type) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = dbManager.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Edge edge : edges) {
                    ps.setInt(1, edge.getScenarioId());
                    ps.setInt(2, edge.getSourceId());
                    ps.setInt(3, edge.getTargetId());
                    ps.setDouble(4, edge.getWeight());
                    ps.setDouble(5, edge.getDelayHours());
                    ps.setString(6, edge.getRelationType());
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

    private Edge mapRow(ResultSet rs) throws SQLException {
        return new Edge(
            rs.getInt("id"),
            rs.getInt("scenario_id"),
            rs.getInt("source_id"),
            rs.getInt("target_id"),
            rs.getDouble("weight"),
            rs.getDouble("delay_hours"),
            rs.getString("relation_type")
        );
    }
}

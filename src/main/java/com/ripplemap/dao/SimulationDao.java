package com.ripplemap.dao;

import com.ripplemap.db.DatabaseManager;
import com.ripplemap.model.MitigationPlan;
import com.ripplemap.model.SimulationResult;
import com.ripplemap.model.SimulationRun;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Simulation runs, results, and mitigation plans with transactional integrity.
 */
public class SimulationDao {

    private final DatabaseManager dbManager;

    public SimulationDao() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public SimulationDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Persists a simulation run and all associated node impact results in a single atomic transaction.
     */
    public SimulationRun saveRunAndResults(SimulationRun run, List<SimulationResult> results, MitigationPlan plan) throws SQLException {
        Connection conn = dbManager.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            // 1. Insert simulation_runs
            String runSql = "INSERT INTO simulation_runs (event_id, algorithm_used, model_type, threshold, duration_ms) " +
                           "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psRun = conn.prepareStatement(runSql, Statement.RETURN_GENERATED_KEYS)) {
                psRun.setInt(1, run.getEventId());
                psRun.setString(2, run.getAlgorithmUsed());
                psRun.setString(3, run.getModelType());
                psRun.setDouble(4, run.getThreshold());
                psRun.setDouble(5, run.getDurationMs());
                psRun.executeUpdate();
                try (ResultSet rs = psRun.getGeneratedKeys()) {
                    if (rs.next()) {
                        run.setId(rs.getInt(1));
                    }
                }
            }

            // 2. Batch insert simulation_results
            if (results != null && !results.isEmpty()) {
                String resSql = "INSERT INTO simulation_results (run_id, node_id, impact_score, arrival_time, hop_level) " +
                               "VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement psRes = conn.prepareStatement(resSql)) {
                    for (SimulationResult res : results) {
                        res.setRunId(run.getId());
                        psRes.setInt(1, run.getId());
                        psRes.setInt(2, res.getNodeId());
                        psRes.setDouble(3, res.getImpactScore());
                        psRes.setDouble(4, res.getArrivalTime());
                        psRes.setInt(5, res.getHopLevel());
                        psRes.addBatch();
                    }
                    psRes.executeBatch();
                }
            }

            // 3. Optional insert mitigation_plans
            if (plan != null) {
                String planSql = "INSERT INTO mitigation_plans (run_id, protected_nodes, impact_reduction_pct) VALUES (?, ?, ?)";
                try (PreparedStatement psPlan = conn.prepareStatement(planSql, Statement.RETURN_GENERATED_KEYS)) {
                    psPlan.setInt(1, run.getId());
                    psPlan.setString(2, plan.getProtectedNodes());
                    psPlan.setDouble(3, plan.getImpactReductionPct());
                    psPlan.executeUpdate();
                    try (ResultSet rs = psPlan.getGeneratedKeys()) {
                        if (rs.next()) {
                            plan.setId(rs.getInt(1));
                            plan.setRunId(run.getId());
                        }
                    }
                }
            }

            conn.commit();
            return run;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
            dbManager.releaseConnection(conn);
        }
    }

    public List<SimulationResult> findResultsByRun(int runId) throws SQLException {
        List<SimulationResult> list = new ArrayList<>();
        String sql = "SELECT sr.id, sr.run_id, sr.node_id, sr.impact_score, sr.arrival_time, sr.hop_level, " +
                     "n.name AS node_name, n.category " +
                     "FROM simulation_results sr " +
                     "JOIN nodes n ON sr.node_id = n.id " +
                     "WHERE sr.run_id = ? " +
                     "ORDER BY sr.impact_score DESC";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, runId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SimulationResult r = new SimulationResult(
                        rs.getInt("id"),
                        rs.getInt("run_id"),
                        rs.getInt("node_id"),
                        rs.getDouble("impact_score"),
                        rs.getDouble("arrival_time"),
                        rs.getInt("hop_level")
                    );
                    r.setNodeName(rs.getString("node_name"));
                    r.setCategory(rs.getString("category"));
                    list.add(r);
                }
            }
        } finally {
            dbManager.releaseConnection(conn);
        }
        return list;
    }

    public List<SimulationRun> findRunsByEvent(int eventId) throws SQLException {
        List<SimulationRun> list = new ArrayList<>();
        String sql = "SELECT id, event_id, algorithm_used, model_type, threshold, duration_ms, run_at " +
                     "FROM simulation_runs WHERE event_id = ? ORDER BY id DESC";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SimulationRun(
                        rs.getInt("id"),
                        rs.getInt("event_id"),
                        rs.getString("algorithm_used"),
                        rs.getString("model_type"),
                        rs.getDouble("threshold"),
                        rs.getDouble("duration_ms"),
                        rs.getTimestamp("run_at")
                    ));
                }
            }
        } finally {
            dbManager.releaseConnection(conn);
        }
        return list;
    }

    public MitigationPlan findMitigationPlanByRun(int runId) throws SQLException {
        String sql = "SELECT id, run_id, protected_nodes, impact_reduction_pct, created_at FROM mitigation_plans WHERE run_id = ?";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, runId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MitigationPlan(
                        rs.getInt("id"),
                        rs.getInt("run_id"),
                        rs.getString("protected_nodes"),
                        rs.getDouble("impact_reduction_pct"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        } finally {
            dbManager.releaseConnection(conn);
        }
        return null;
    }
}

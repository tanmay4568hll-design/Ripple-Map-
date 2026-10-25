package com.ripplemap.dao;

import com.ripplemap.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object providing complex SQL queries for the Reports & Analytics tab.
 */
public class ReportDao {

    private final DatabaseManager dbManager;

    public ReportDao() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public ReportDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Query 1: Top N most impacted nodes across all historical simulation runs.
     */
    public List<Map<String, Object>> getTopImpactedNodesAcrossRuns(int limit) throws SQLException {
        String sql = "SELECT n.id, n.name, n.category, s.name AS scenario_name, " +
                     "COUNT(sr.id) AS occurrences, " +
                     "AVG(sr.impact_score) AS avg_impact, " +
                     "MAX(sr.impact_score) AS max_impact " +
                     "FROM simulation_results sr " +
                     "JOIN nodes n ON sr.node_id = n.id " +
                     "JOIN scenarios s ON n.scenario_id = s.id " +
                     "GROUP BY n.id, n.name, n.category, s.name " +
                     "ORDER BY avg_impact DESC LIMIT ?";

        return executeQueryToList(sql, ps -> ps.setInt(1, limit));
    }

    /**
     * Query 2: Average impact score and arrival time aggregated by entity category.
     */
    public List<Map<String, Object>> getAverageImpactByCategory() throws SQLException {
        String sql = "SELECT n.category, " +
                     "COUNT(DISTINCT n.id) AS node_count, " +
                     "COALESCE(AVG(sr.impact_score), 0.0) AS avg_impact, " +
                     "COALESCE(AVG(sr.arrival_time), 0.0) AS avg_arrival_hours " +
                     "FROM nodes n " +
                     "LEFT JOIN simulation_results sr ON n.id = sr.node_id " +
                     "GROUP BY n.category " +
                     "ORDER BY avg_impact DESC";

        return executeQueryToList(sql, null);
    }

    /**
     * Query 3: Multi-table JOIN combining Events, Nodes, Scenarios, and Runs.
     */
    public List<Map<String, Object>> getEventHistoryWithJoins(int scenarioId) throws SQLException {
        String sql = "SELECT e.id AS event_id, e.name AS event_name, e.severity, e.triggered_at, " +
                     "n.name AS source_node_name, n.category AS source_category, " +
                     "s.name AS scenario_name, " +
                     "COUNT(sim.id) AS total_runs " +
                     "FROM events e " +
                     "JOIN nodes n ON e.source_node_id = n.id " +
                     "JOIN scenarios s ON e.scenario_id = s.id " +
                     "LEFT JOIN simulation_runs sim ON e.id = sim.event_id " +
                     "WHERE (? = 0 OR e.scenario_id = ?) " +
                     "GROUP BY e.id, e.name, e.severity, e.triggered_at, n.name, n.category, s.name " +
                     "ORDER BY e.triggered_at DESC";

        return executeQueryToList(sql, ps -> {
            ps.setInt(1, scenarioId);
            ps.setInt(2, scenarioId);
        });
    }

    /**
     * Query 4: GROUP BY and HAVING aggregate for high-vulnerability nodes.
     */
    public List<Map<String, Object>> getHighRiskNodesGroupByHaving(double minAvgImpact, int minOccurrences) throws SQLException {
        String sql = "SELECT n.id, n.name, n.category, " +
                     "COUNT(sr.id) AS run_count, " +
                     "AVG(sr.impact_score) AS avg_impact, " +
                     "MIN(sr.arrival_time) AS fastest_arrival_hours " +
                     "FROM simulation_results sr " +
                     "JOIN nodes n ON sr.node_id = n.id " +
                     "GROUP BY n.id, n.name, n.category " +
                     "HAVING AVG(sr.impact_score) >= ? AND COUNT(sr.id) >= ? " +
                     "ORDER BY avg_impact DESC";

        return executeQueryToList(sql, ps -> {
            ps.setDouble(1, minAvgImpact);
            ps.setInt(2, minOccurrences);
        });
    }

    /**
     * Query 5: Querying database VIEW `v_scenario_summary`.
     */
    public List<Map<String, Object>> getScenarioSummariesFromView() throws SQLException {
        String sql = "SELECT scenario_id, scenario_name, total_nodes, total_edges, " +
                     "total_events_simulated, avg_historical_impact " +
                     "FROM v_scenario_summary ORDER BY scenario_id";

        return executeQueryToList(sql, null);
    }

    @FunctionalInterface
    public interface ParamSetter {
        void setParams(PreparedStatement ps) throws SQLException;
    }

    private List<Map<String, Object>> executeQueryToList(String sql, ParamSetter setter) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (setter != null) {
                setter.setParams(ps);
            }
            try (ResultSet rs = ps.executeQuery()) {
                int colCount = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= colCount; i++) {
                        String colName = rs.getMetaData().getColumnLabel(i);
                        row.put(colName, rs.getObject(i));
                    }
                    results.add(row);
                }
            }
        } finally {
            dbManager.releaseConnection(conn);
        }
        return results;
    }
}

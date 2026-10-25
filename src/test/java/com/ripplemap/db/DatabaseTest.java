package com.ripplemap.db;

import com.ripplemap.dao.AuditDao;
import com.ripplemap.dao.EventDao;
import com.ripplemap.dao.NodeDao;
import com.ripplemap.dao.ReportDao;
import com.ripplemap.dao.ScenarioDao;
import com.ripplemap.dao.SimulationDao;
import com.ripplemap.dao.UserDao;
import com.ripplemap.model.AuditLogEntry;
import com.ripplemap.model.Event;
import com.ripplemap.model.MitigationPlan;
import com.ripplemap.model.Node;
import com.ripplemap.model.Scenario;
import com.ripplemap.model.SimulationResult;
import com.ripplemap.model.SimulationRun;
import com.ripplemap.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Phase 1: Database, Connection Pool, and DAO Layer Tests")
public class DatabaseTest {

    private static final String TEST_DB = "test_ripplemap.db";
    private static DatabaseManager dbManager;
    private static UserDao userDao;
    private static ScenarioDao scenarioDao;
    private static NodeDao nodeDao;
    private static EventDao eventDao;
    private static SimulationDao simDao;
    private static AuditDao auditDao;
    private static ReportDao reportDao;

    @BeforeAll
    static void setUp() {
        File dbFile = new File(TEST_DB);
        if (dbFile.exists()) {
            dbFile.delete();
        }

        DatabaseManager.resetInstance("jdbc:sqlite:" + TEST_DB);
        dbManager = DatabaseManager.getInstance();
        dbManager.initializeSchemaAndSeed();

        userDao = new UserDao(dbManager);
        scenarioDao = new ScenarioDao(dbManager);
        nodeDao = new NodeDao(dbManager);
        eventDao = new EventDao(dbManager);
        simDao = new SimulationDao(dbManager);
        auditDao = new AuditDao(dbManager);
        reportDao = new ReportDao(dbManager);
    }

    @AfterAll
    static void tearDown() {
        if (dbManager != null) {
            dbManager.shutdown();
        }
        File dbFile = new File(TEST_DB);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    @DisplayName("Verify Schema & Seed Data populated all 4 scenarios")
    void testScenariosAndSeedData() throws SQLException {
        List<Scenario> scenarios = scenarioDao.findAll();
        assertNotNull(scenarios);
        assertEquals(4, scenarios.size(), "Expected 4 built-in seed scenarios");

        // Verify Scenario 1 has 25 nodes
        List<Node> nodesScen1 = nodeDao.findByScenario(1);
        assertTrue(nodesScen1.size() >= 25, "Scenario 1 should have at least 25 seed nodes");
        assertNotNull(nodeDao.findById(104), "TSMC Wafer Foundry should exist");
    }

    @Test
    @DisplayName("Verify User Authentication with Salted SHA-256")
    void testUserAuthentication() throws SQLException {
        User admin = userDao.authenticate("admin", "admin123");
        assertNotNull(admin, "Admin should authenticate with admin123");
        assertTrue(admin.isAdmin(), "Admin role should be verified");

        User wrongPass = userDao.authenticate("admin", "wrong_pass");
        assertNull(wrongPass, "Wrong password should fail authentication");

        User analyst = userDao.authenticate("analyst", "analyst123");
        assertNotNull(analyst);
        assertFalse(analyst.isAdmin(), "Analyst should not be admin");
    }

    @Test
    @DisplayName("Verify Transactional Batch Insertion of Nodes")
    void testNodeBatchInsertTransaction() throws SQLException {
        List<Node> newNodes = new ArrayList<>();
        newNodes.add(new Node(0, 1, "Test Sensor Alpha", "Sensor", 50, 50, 0.6, "Test"));
        newNodes.add(new Node(0, 1, "Test Sensor Beta", "Sensor", 60, 60, 0.7, "Test"));

        nodeDao.batchInsert(newNodes);

        List<Node> allNodes = nodeDao.findByScenario(1);
        assertTrue(allNodes.stream().anyMatch(n -> "Test Sensor Alpha".equals(n.getName())));
        assertTrue(allNodes.stream().anyMatch(n -> "Test Sensor Beta".equals(n.getName())));
    }

    @Test
    @DisplayName("Verify SQLite Trigger auto-logs event insertion into audit_log")
    void testEventInsertionTrigger() throws SQLException {
        Event testEvent = new Event(0, 1, "Cyber Attack Simulation", 104, 7.5, null);
        Event saved = eventDao.insert(testEvent);
        assertNotNull(saved);
        assertTrue(saved.getId() > 0);

        List<AuditLogEntry> auditLogs = auditDao.findRecent(10);
        assertFalse(auditLogs.isEmpty());
        boolean triggerFired = auditLogs.stream()
            .anyMatch(log -> log.getAction().contains("Cyber Attack Simulation"));
        assertTrue(triggerFired, "Expected trigger 'trg_event_audit_log' to record action in audit_log");
    }

    @Test
    @DisplayName("Verify Atomic Simulation Run, Results, and Mitigation Plan Save")
    void testSimulationAtomicSave() throws SQLException {
        SimulationRun run = new SimulationRun(0, 1, "BFS_RIPPLE", "MAX_PATH", 0.05, 12.5, null);
        List<SimulationResult> results = new ArrayList<>();
        results.add(new SimulationResult(0, 0, 101, 0.95, 0.0, 0));
        results.add(new SimulationResult(0, 0, 106, 0.80, 48.0, 1));

        MitigationPlan plan = new MitigationPlan(0, 0, "Wafer Foundry TSMC", 35.0, null);

        SimulationRun savedRun = simDao.saveRunAndResults(run, results, plan);
        assertTrue(savedRun.getId() > 0);

        List<SimulationResult> retrievedResults = simDao.findResultsByRun(savedRun.getId());
        assertEquals(2, retrievedResults.size());

        MitigationPlan retrievedPlan = simDao.findMitigationPlanByRun(savedRun.getId());
        assertNotNull(retrievedPlan);
        assertEquals("Wafer Foundry TSMC", retrievedPlan.getProtectedNodes());
    }

    @Test
    @DisplayName("Verify 5 SQL Analytical Reports and View Queries")
    void testReportQueries() throws SQLException {
        // Query 1: Top Impacted Nodes
        List<Map<String, Object>> topNodes = reportDao.getTopImpactedNodesAcrossRuns(5);
        assertNotNull(topNodes);

        // Query 2: Category Breakdown
        List<Map<String, Object>> categories = reportDao.getAverageImpactByCategory();
        assertNotNull(categories);
        assertFalse(categories.isEmpty());

        // Query 3: Multi-table JOIN event history
        List<Map<String, Object>> history = reportDao.getEventHistoryWithJoins(1);
        assertNotNull(history);

        // Query 4: GROUP BY and HAVING high-risk nodes
        List<Map<String, Object>> highRisk = reportDao.getHighRiskNodesGroupByHaving(0.1, 1);
        assertNotNull(highRisk);

        // Query 5: Database VIEW v_scenario_summary
        List<Map<String, Object>> summaries = reportDao.getScenarioSummariesFromView();
        assertNotNull(summaries);
        assertEquals(4, summaries.size(), "VIEW should present summary for all 4 scenarios");
    }
}

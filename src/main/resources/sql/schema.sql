-- ====================================================================
-- RippleMap Database Schema (3NF Normalized)
-- Supports SQLite and MySQL
-- ====================================================================

-- 1. Scenarios Table
CREATE TABLE IF NOT EXISTS scenarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Nodes Table (3NF: all non-key attributes functionally dependent on primary key)
CREATE TABLE IF NOT EXISTS nodes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    scenario_id INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    x_pos DOUBLE NOT NULL DEFAULT 0.0,
    y_pos DOUBLE NOT NULL DEFAULT 0.0,
    resilience DOUBLE NOT NULL DEFAULT 0.5, -- resilience score [0.0 - 1.0], dampens impact
    description TEXT,
    FOREIGN KEY (scenario_id) REFERENCES scenarios(id) ON DELETE CASCADE,
    UNIQUE(scenario_id, name)
);

CREATE INDEX IF NOT EXISTS idx_nodes_scenario ON nodes(scenario_id);
CREATE INDEX IF NOT EXISTS idx_nodes_category ON nodes(category);

-- 3. Edges Table (Influence links between nodes)
CREATE TABLE IF NOT EXISTS edges (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    scenario_id INTEGER NOT NULL,
    source_id INTEGER NOT NULL,
    target_id INTEGER NOT NULL,
    weight DOUBLE NOT NULL DEFAULT 1.0, -- impact multiplier [0.0 - 1.0]
    delay_hours DOUBLE NOT NULL DEFAULT 1.0, -- propagation latency in hours
    relation_type VARCHAR(50) DEFAULT 'DEPENDS_ON',
    FOREIGN KEY (scenario_id) REFERENCES scenarios(id) ON DELETE CASCADE,
    FOREIGN KEY (source_id) REFERENCES nodes(id) ON DELETE CASCADE,
    FOREIGN KEY (target_id) REFERENCES nodes(id) ON DELETE CASCADE,
    UNIQUE(scenario_id, source_id, target_id)
);

CREATE INDEX IF NOT EXISTS idx_edges_scenario ON edges(scenario_id);
CREATE INDEX IF NOT EXISTS idx_edges_source ON edges(source_id);
CREATE INDEX IF NOT EXISTS idx_edges_target ON edges(target_id);

-- 4. Users Table (Authentication & RBAC: Admin, Analyst)
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(128) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ANALYST', -- 'ADMIN' or 'ANALYST'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Audit Log Table
CREATE TABLE IF NOT EXISTS audit_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    action TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON audit_log(timestamp);

-- 6. Events Table (Triggered incidents on a source node)
CREATE TABLE IF NOT EXISTS events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    scenario_id INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    source_node_id INTEGER NOT NULL,
    severity DOUBLE NOT NULL, -- 1.0 to 10.0
    triggered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (scenario_id) REFERENCES scenarios(id) ON DELETE CASCADE,
    FOREIGN KEY (source_node_id) REFERENCES nodes(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_events_scenario ON events(scenario_id);

-- 7. Simulation Runs Table
CREATE TABLE IF NOT EXISTS simulation_runs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    event_id INTEGER NOT NULL,
    algorithm_used VARCHAR(50) NOT NULL, -- 'BFS_RIPPLE', 'DIJKSTRA_TIME', 'CASCADE_PROPAGATION', etc.
    model_type VARCHAR(30) NOT NULL,    -- 'MAX_PATH' or 'SUM_PATH'
    threshold DOUBLE NOT NULL,          -- impact cutoff threshold (e.g. 0.05)
    duration_ms DOUBLE NOT NULL,
    run_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_sim_runs_event ON simulation_runs(event_id);

-- 8. Simulation Results Table (Per-node impact metrics for a run)
CREATE TABLE IF NOT EXISTS simulation_results (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    run_id INTEGER NOT NULL,
    node_id INTEGER NOT NULL,
    impact_score DOUBLE NOT NULL,
    arrival_time DOUBLE NOT NULL, -- in hours
    hop_level INTEGER NOT NULL,
    FOREIGN KEY (run_id) REFERENCES simulation_runs(id) ON DELETE CASCADE,
    FOREIGN KEY (node_id) REFERENCES nodes(id) ON DELETE CASCADE,
    UNIQUE(run_id, node_id)
);

CREATE INDEX IF NOT EXISTS idx_sim_results_run ON simulation_results(run_id);
CREATE INDEX IF NOT EXISTS idx_sim_results_node ON simulation_results(node_id);

-- 9. Mitigation Plans Table (Greedy Advisor results)
CREATE TABLE IF NOT EXISTS mitigation_plans (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    run_id INTEGER NOT NULL,
    protected_nodes TEXT NOT NULL, -- JSON or comma-separated list of protected node names
    impact_reduction_pct DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (run_id) REFERENCES simulation_runs(id) ON DELETE CASCADE
);

-- ====================================================================
-- VIEW: Scenario Risk Summary
-- Aggregates node counts, edge counts, and event frequencies
-- ====================================================================
CREATE VIEW IF NOT EXISTS v_scenario_summary AS
SELECT 
    s.id AS scenario_id,
    s.name AS scenario_name,
    COUNT(DISTINCT n.id) AS total_nodes,
    COUNT(DISTINCT e.id) AS total_edges,
    COUNT(DISTINCT ev.id) AS total_events_simulated,
    COALESCE(AVG(sr.impact_score), 0.0) AS avg_historical_impact
FROM scenarios s
LEFT JOIN nodes n ON s.id = n.scenario_id
LEFT JOIN edges e ON s.id = e.scenario_id
LEFT JOIN events ev ON s.id = ev.scenario_id
LEFT JOIN simulation_runs sim ON ev.id = sim.event_id
LEFT JOIN simulation_results sr ON sim.id = sr.run_id
GROUP BY s.id, s.name;

-- ====================================================================
-- TRIGGER: Audit Log Trigger on Event Insertion
-- Automatically logs every event trigger to audit_log
-- ====================================================================
CREATE TRIGGER IF NOT EXISTS trg_event_audit_log
AFTER INSERT ON events
BEGIN
    INSERT INTO audit_log (user_id, action, timestamp)
    VALUES (
        NULL,
        'EVENT_TRIGGERED: Event "' || NEW.name || '" on Node ID ' || NEW.source_node_id || ' (Severity: ' || NEW.severity || ') in Scenario ID ' || NEW.scenario_id,
        CURRENT_TIMESTAMP
    );
END;

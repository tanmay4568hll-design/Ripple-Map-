package com.ripplemap.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton database manager providing connection pooling and schema initialization.
 */
public class DatabaseManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());

    private static volatile DatabaseManager instance;
    private SimpleConnectionPool connectionPool;
    private String dbType = "sqlite";
    private boolean initialized = false;

    private DatabaseManager() {
        initPool(null);
    }

    private DatabaseManager(String customJdbcUrl) {
        initPool(customJdbcUrl);
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    /**
     * Initializes or resets database manager with a custom JDBC URL (used for testing, e.g. SQLite in-memory).
     */
    public static synchronized void resetInstance(String customJdbcUrl) {
        if (instance != null) {
            instance.shutdown();
        }
        instance = new DatabaseManager(customJdbcUrl);
    }

    private void initPool(String customJdbcUrl) {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "db.properties not found, using default SQLite configuration.", e);
        }

        dbType = props.getProperty("db.type", "sqlite").trim().toLowerCase();
        String url;
        String user = null;
        String pass = null;

        if (customJdbcUrl != null && !customJdbcUrl.isEmpty()) {
            url = customJdbcUrl;
        } else if ("mysql".equals(dbType)) {
            url = props.getProperty("db.mysql.url");
            user = props.getProperty("db.mysql.user", "root");
            pass = props.getProperty("db.mysql.password", "");
        } else {
            url = props.getProperty("db.sqlite.url", "jdbc:sqlite:ripplemap.db");
        }

        int initialSize = Integer.parseInt(props.getProperty("pool.initialSize", "5"));
        int maxSize = Integer.parseInt(props.getProperty("pool.maxSize", "15"));
        long timeoutMs = Long.parseLong(props.getProperty("pool.timeoutMs", "5000"));

        connectionPool = new SimpleConnectionPool(url, user, pass, initialSize, maxSize, timeoutMs);
        LOGGER.info("DatabaseManager connected via pool to " + url);
    }

    public Connection getConnection() throws SQLException {
        Connection conn = connectionPool.getConnection();
        if ("sqlite".equalsIgnoreCase(dbType)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            } catch (SQLException e) {
                LOGGER.log(Level.FINE, "Failed to enable foreign keys in SQLite: " + e.getMessage());
            }
        }
        return conn;
    }

    public void releaseConnection(Connection connection) {
        if (connectionPool != null && connection != null) {
            connectionPool.releaseConnection(connection);
        }
    }

    /**
     * Initializes database schema and runs seed data if not yet initialized.
     */
    public synchronized void initializeSchemaAndSeed() {
        if (initialized) return;

        Connection conn = null;
        try {
            conn = getConnection();
            executeSqlScript(conn, "sql/schema.sql");
            executeSqlScript(conn, "sql/seed.sql");
            initialized = true;
            LOGGER.info("Database schema and seed scripts executed successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database schema and seed data", e);
            throw new RuntimeException("Database initialization failure", e);
        } finally {
            if (conn != null) releaseConnection(conn);
        }
    }

    /**
     * Reads and executes a SQL script from resources, accounting for SQLite triggers and multiline statements.
     */
    public void executeSqlScript(Connection conn, String resourcePath) throws IOException, SQLException {
        InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("--") || trimmed.isEmpty()) {
                    continue;
                }
                sb.append(line).append("\n");
            }
        }

        String allSql = sb.toString();
        // Split statements by semicolon, but preserve TRIGGER definitions (BEGIN ... END;)
        String[] rawStatements = allSql.split(";");
        try (Statement stmt = conn.createStatement()) {
            StringBuilder statementBuffer = new StringBuilder();
            boolean insideTrigger = false;

            for (String raw : rawStatements) {
                String s = raw.trim();
                if (s.isEmpty()) continue;

                if (s.toUpperCase().contains("CREATE TRIGGER")) {
                    insideTrigger = true;
                }

                if (statementBuffer.length() > 0) {
                    statementBuffer.append(";");
                }
                statementBuffer.append(s);

                if (insideTrigger) {
                    if (s.toUpperCase().contains("END")) {
                        insideTrigger = false;
                        stmt.execute(statementBuffer.toString());
                        statementBuffer.setLength(0);
                    }
                } else {
                    stmt.execute(statementBuffer.toString());
                    statementBuffer.setLength(0);
                }
            }

            if (statementBuffer.length() > 0 && !statementBuffer.toString().trim().isEmpty()) {
                stmt.execute(statementBuffer.toString());
            }
        }
    }

    public synchronized void shutdown() {
        if (connectionPool != null) {
            connectionPool.shutdown();
            connectionPool = null;
        }
        initialized = false;
    }

    public String getDbType() {
        return dbType;
    }
}

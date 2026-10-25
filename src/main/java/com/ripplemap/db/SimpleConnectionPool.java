package com.ripplemap.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lightweight, thread-safe JDBC connection pool.
 */
public class SimpleConnectionPool {
    private static final Logger LOGGER = Logger.getLogger(SimpleConnectionPool.class.getName());

    private final String url;
    private final String user;
    private final String password;
    private final int maxSize;
    private final long timeoutMs;

    private final List<Connection> availableConnections;
    private final List<Connection> usedConnections;
    private boolean isShutdown = false;

    public SimpleConnectionPool(String url, String user, String password, int initialSize, int maxSize, long timeoutMs) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.maxSize = maxSize;
        this.timeoutMs = timeoutMs;
        this.availableConnections = new ArrayList<>(maxSize);
        this.usedConnections = new ArrayList<>(maxSize);

        for (int i = 0; i < initialSize; i++) {
            try {
                availableConnections.add(createNewConnection());
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to initialize connection in pool: " + e.getMessage());
            }
        }
        LOGGER.info("SimpleConnectionPool initialized with " + availableConnections.size() + " connections (max " + maxSize + ")");
    }

    private Connection createNewConnection() throws SQLException {
        if (user != null && !user.isEmpty()) {
            return DriverManager.getConnection(url, user, password);
        } else {
            return DriverManager.getConnection(url);
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        if (isShutdown) {
            throw new SQLException("Connection pool has been shut down.");
        }

        long deadline = System.currentTimeMillis() + timeoutMs;
        while (availableConnections.isEmpty()) {
            if (usedConnections.size() < maxSize) {
                Connection conn = createNewConnection();
                usedConnections.add(conn);
                return conn;
            }

            long waitTime = deadline - System.currentTimeMillis();
            if (waitTime <= 0) {
                throw new SQLException("Timeout waiting for available database connection from pool.");
            }
            try {
                wait(waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SQLException("Interrupted while waiting for connection.", e);
            }
        }

        Connection conn = availableConnections.remove(availableConnections.size() - 1);
        try {
            if (conn.isClosed() || !conn.isValid(2)) {
                LOGGER.fine("Stale connection discarded, opening new connection.");
                conn = createNewConnection();
            }
        } catch (SQLException e) {
            conn = createNewConnection();
        }

        usedConnections.add(conn);
        return conn;
    }

    public synchronized void releaseConnection(Connection connection) {
        if (connection == null) return;
        usedConnections.remove(connection);

        if (isShutdown) {
            try { connection.close(); } catch (SQLException ignored) {}
            return;
        }

        try {
            if (!connection.isClosed()) {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                }
                availableConnections.add(connection);
                notifyAll();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error returning connection to pool: " + e.getMessage());
            try { connection.close(); } catch (SQLException ignored) {}
        }
    }

    public synchronized void shutdown() {
        isShutdown = true;
        for (Connection c : availableConnections) {
            try { c.close(); } catch (SQLException ignored) {}
        }
        for (Connection c : usedConnections) {
            try { c.close(); } catch (SQLException ignored) {}
        }
        availableConnections.clear();
        usedConnections.clear();
        LOGGER.info("Connection pool shutdown successfully.");
    }

    public synchronized int getActiveCount() {
        return usedConnections.size();
    }

    public synchronized int getIdleCount() {
        return availableConnections.size();
    }
}

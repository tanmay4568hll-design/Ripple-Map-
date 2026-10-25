package com.ripplemap.dao;

import com.ripplemap.db.DatabaseManager;
import com.ripplemap.model.User;
import com.ripplemap.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Users with salted authentication.
 */
public class UserDao {

    private final DatabaseManager dbManager;

    public UserDao() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public UserDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, salt, role, created_at FROM users WHERE username = ?";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("salt"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        } finally {
            dbManager.releaseConnection(conn);
        }
        return null;
    }

    public User authenticate(String username, String rawPassword) throws SQLException {
        User user = findByUsername(username);
        if (user != null && PasswordUtil.verifyPassword(rawPassword, user.getSalt(), user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    public User register(String username, String rawPassword, String role) throws SQLException {
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(rawPassword, salt);
        String sql = "INSERT INTO users (username, password_hash, salt, role) VALUES (?, ?, ?, ?)";

        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, salt);
            ps.setString(4, role != null ? role.toUpperCase() : "ANALYST");
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new User(id, username, hash, salt, role, null);
                }
            }
        } finally {
            dbManager.releaseConnection(conn);
        }
        return null;
    }

    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, username, password_hash, salt, role, created_at FROM users ORDER BY username";
        Connection conn = dbManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getString("salt"),
                    rs.getString("role"),
                    rs.getTimestamp("created_at")
                ));
            }
        } finally {
            dbManager.releaseConnection(conn);
        }
        return list;
    }
}

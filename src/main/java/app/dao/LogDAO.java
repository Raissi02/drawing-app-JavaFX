package app.dao;

import app.model.LogEntry;
import app.singleton.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO Pattern: handles persistence of log entries to SQLite.
 */
public class LogDAO {

    private final Connection connection;

    public LogDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public void save(LogEntry entry) {
        String sql = "INSERT INTO logs (action, details) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entry.getAction());
            ps.setString(2, entry.getDetails());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("LogDAO save error: " + e.getMessage());
        }
    }

    public List<LogEntry> getAll() throws SQLException {
        List<LogEntry> logs = new ArrayList<>();
        String sql = "SELECT action, details FROM logs ORDER BY timestamp DESC LIMIT 100";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                logs.add(new LogEntry(rs.getString("action"), rs.getString("details")));
            }
        }
        return logs;
    }
}

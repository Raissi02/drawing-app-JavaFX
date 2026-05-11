package app.strategy;

import app.dao.LogDAO;
import app.model.LogEntry;

/**
 * Strategy Pattern: logs to the SQLite database.
 */
public class DatabaseLoggerStrategy implements LoggingStrategy {

    private final LogDAO logDAO;

    public DatabaseLoggerStrategy() {
        this.logDAO = new LogDAO();
    }

    @Override
    public void log(LogEntry entry) {
        logDAO.save(entry);
    }

    @Override
    public String getStrategyName() {
        return "Database";
    }
}

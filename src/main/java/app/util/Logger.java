package app.util;

import app.model.LogEntry;
import app.strategy.LoggingStrategy;
import app.strategy.ConsoleLoggerStrategy;

/**
 * Utility class that delegates logging to the active LoggingStrategy.
 * The strategy can be switched at runtime (Strategy Pattern).
 */
public class Logger {

    private static Logger instance;
    private LoggingStrategy strategy;

    private Logger() {
        this.strategy = new ConsoleLoggerStrategy(); // default
    }

    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void setStrategy(LoggingStrategy strategy) {
        this.strategy = strategy;
    }

    public LoggingStrategy getStrategy() {
        return strategy;
    }

    public void log(String action, String details) {
        LogEntry entry = new LogEntry(action, details);
        strategy.log(entry);
    }
}

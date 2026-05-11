package app.strategy;

import app.model.LogEntry;

/**
 * Strategy Pattern: interface for logging strategies.
 */
public interface LoggingStrategy {
    void log(LogEntry entry);
    String getStrategyName();
}

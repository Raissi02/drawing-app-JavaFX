package app.strategy;

import app.model.LogEntry;

/**
 * Strategy Pattern: logs to standard console output.
 */
public class ConsoleLoggerStrategy implements LoggingStrategy {

    @Override
    public void log(LogEntry entry) {
        System.out.println(entry.toString());
    }

    @Override
    public String getStrategyName() {
        return "Console";
    }
}

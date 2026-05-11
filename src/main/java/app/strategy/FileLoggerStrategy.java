package app.strategy;

import app.model.LogEntry;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Strategy Pattern: logs to a text file (drawing_app.log).
 */
public class FileLoggerStrategy implements LoggingStrategy {

    private static final String LOG_FILE = "drawing_app.log";

    @Override
    public void log(LogEntry entry) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(entry.toString());
        } catch (IOException e) {
            System.err.println("FileLogger error: " + e.getMessage());
        }
    }

    @Override
    public String getStrategyName() {
        return "File";
    }
}

package app.model;

import java.time.LocalDateTime;

public class LogEntry {
    private int id;
    private String action;
    private String details;
    private LocalDateTime timestamp;

    public LogEntry(String action, String details) {
        this.action = action;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAction() { return action; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + action + ": " + details;
    }
}

package app.command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Manages the history of executed commands for undo functionality.
 */
public class CommandHistory {

    private final Deque<DrawCommand> history = new ArrayDeque<>();

    public void execute(DrawCommand command) {
        command.execute();
        history.push(command);
    }

    public void undo() {
        if (!history.isEmpty()) {
            DrawCommand command = history.pop();
            command.undo();
        }
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }

    public String getLastDescription() {
        if (!history.isEmpty()) {
            return history.peek().getDescription();
        }
        return "Nothing to undo";
    }

    public void clear() {
        history.clear();
    }
}

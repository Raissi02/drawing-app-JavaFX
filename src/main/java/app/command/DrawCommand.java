package app.command;

/**
 * Command Pattern: base interface for all undoable drawing commands.
 */
public interface DrawCommand {
    void execute();
    void undo();
    String getDescription();
}

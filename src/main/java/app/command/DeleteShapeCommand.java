package app.command;

import app.model.DrawingModel;
import app.model.ShapeModel;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

/**
 * Command Pattern: removes a shape from the drawing model and the canvas.
 */
public class DeleteShapeCommand implements DrawCommand {

    private final DrawingModel drawingModel;
    private final ShapeModel shapeModel;
    private final Pane canvas;
    private final Shape jfxShape;

    public DeleteShapeCommand(DrawingModel drawingModel, ShapeModel shapeModel,
                              Pane canvas, Shape jfxShape) {
        this.drawingModel = drawingModel;
        this.shapeModel = shapeModel;
        this.canvas = canvas;
        this.jfxShape = jfxShape;
    }

    @Override
    public void execute() {
        drawingModel.removeShape(shapeModel);
        canvas.getChildren().remove(jfxShape);
    }

    @Override
    public void undo() {
        drawingModel.addShape(shapeModel);
        canvas.getChildren().add(jfxShape);
    }

    @Override
    public String getDescription() {
        return "Delete " + shapeModel.getType();
    }
}

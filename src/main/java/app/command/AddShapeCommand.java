package app.command;

import app.model.DrawingModel;
import app.model.ShapeModel;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

/**
 * Command Pattern: adds a shape to the drawing model and the canvas.
 */
public class AddShapeCommand implements DrawCommand {

    private final DrawingModel drawingModel;
    private final ShapeModel shapeModel;
    private final Pane canvas;
    private final Shape jfxShape;

    public AddShapeCommand(DrawingModel drawingModel, ShapeModel shapeModel,
                           Pane canvas, Shape jfxShape) {
        this.drawingModel = drawingModel;
        this.shapeModel = shapeModel;
        this.canvas = canvas;
        this.jfxShape = jfxShape;
    }

    @Override
    public void execute() {
        drawingModel.addShape(shapeModel);
        if (!canvas.getChildren().contains(jfxShape)) {
            canvas.getChildren().add(jfxShape);
        }
    }

    @Override
    public void undo() {
        drawingModel.removeShape(shapeModel);
        canvas.getChildren().remove(jfxShape);
    }

    @Override
    public String getDescription() {
        return "Add " + shapeModel.getType();
    }
}

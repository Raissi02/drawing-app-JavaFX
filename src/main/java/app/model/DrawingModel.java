package app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model representing the entire drawing (collection of shapes).
 */
public class DrawingModel {

    private int id;
    private String name;
    private List<ShapeModel> shapes;

    public DrawingModel() {
        this.shapes = new ArrayList<>();
    }

    public DrawingModel(String name) {
        this.name = name;
        this.shapes = new ArrayList<>();
    }

    public void addShape(ShapeModel shape) {
        shapes.add(shape);
    }

    public void removeShape(ShapeModel shape) {
        shapes.remove(shape);
    }

    public void clear() {
        shapes.clear();
    }

    public List<ShapeModel> getShapes() { return shapes; }
    public void setShapes(List<ShapeModel> shapes) { this.shapes = shapes; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

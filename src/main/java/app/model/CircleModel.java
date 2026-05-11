package app.model;

public class CircleModel extends ShapeModel {
    public CircleModel(double x, double y, double radius, String color) {
        super("CIRCLE", x, y, radius * 2, radius * 2, color);
    }

    public double getRadius() {
        return width / 2.0;
    }
}

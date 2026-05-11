package app.factory;

import app.model.*;

/**
 * Factory Pattern: creates ShapeModel instances based on type string.
 */
public class ShapeFactory {

    public static ShapeModel create(String type, double x, double y,
                                   double width, double height, String color) {
        return switch (type.toUpperCase()) {
            case "RECTANGLE" -> new RectangleModel(x, y, width, height, color);
            case "CIRCLE"    -> new CircleModel(x, y, Math.min(width, height) / 2.0, color);
            case "LINE"      -> new LineModel(x, y, x + width, y + height, color);
            default          -> throw new IllegalArgumentException("Unknown shape type: " + type);
        };
    }

    public static ShapeModel createLine(double startX, double startY,
                                        double endX, double endY, String color) {
        return new LineModel(startX, startY, endX, endY, color);
    }
}

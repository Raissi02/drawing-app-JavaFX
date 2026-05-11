package app.util;

import app.model.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

/**
 * Utility: converts ShapeModel to JavaFX Shape nodes for rendering.
 */
public class ShapeRenderer {

    public static Shape render(ShapeModel model) {
        Color color = parseColor(model.getColor());
        Shape shape = switch (model.getType()) {
            case "RECTANGLE" -> buildRectangle(model, color);
            case "CIRCLE"    -> buildCircle(model, color);
            case "LINE"      -> buildLine(model, color);
            default          -> throw new IllegalArgumentException("Unknown type: " + model.getType());
        };
        return shape;
    }

    private static Rectangle buildRectangle(ShapeModel m, Color color) {
        Rectangle r = new Rectangle(m.getX(), m.getY(), Math.abs(m.getWidth()), Math.abs(m.getHeight()));
        r.setFill(color.deriveColor(0, 1, 1, 0.5));
        r.setStroke(color);
        r.setStrokeWidth(2);
        return r;
    }

    private static Circle buildCircle(ShapeModel m, Color color) {
        double radius = Math.min(Math.abs(m.getWidth()), Math.abs(m.getHeight())) / 2.0;
        Circle c = new Circle(m.getX() + radius, m.getY() + radius, radius);
        c.setFill(color.deriveColor(0, 1, 1, 0.5));
        c.setStroke(color);
        c.setStrokeWidth(2);
        return c;
    }

    private static Line buildLine(ShapeModel m, Color color) {
        LineModel lm = (LineModel) m;
        Line l = new Line(lm.getX(), lm.getY(), lm.getEndX(), lm.getEndY());
        l.setStroke(color);
        l.setStrokeWidth(2.5);
        return l;
    }

    private static Color parseColor(String colorStr) {
        try {
            return Color.web(colorStr);
        } catch (Exception e) {
            return Color.DODGERBLUE;
        }
    }

    public static void highlight(Shape shape, boolean selected) {
        if (selected) {
            shape.setEffect(new javafx.scene.effect.DropShadow(8, Color.ORANGE));
            shape.setStroke(Color.ORANGE);
        } else {
            shape.setEffect(null);
            shape.setStroke(Color.web(getUserColor(shape)));
        }
    }

    private static String getUserColor(Shape shape) {
        // Fallback — stroke was set to orange; restore default
        return "#2196F3";
    }
}

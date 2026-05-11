package app.model;

/**
 * Abstract base model representing a geometric shape.
 * Follows MVC: this is the Model layer.
 */
public abstract class ShapeModel {

    protected int id;
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected String color;
    protected String type;

    public ShapeModel(String type, double x, double y, double width, double height, String color) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public String toString() {
        return type + " [x=" + x + ", y=" + y + ", w=" + width + ", h=" + height + "]";
    }
}

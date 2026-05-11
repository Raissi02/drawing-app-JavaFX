package app.model;

public class LineModel extends ShapeModel {
    private double endX;
    private double endY;

    public LineModel(double startX, double startY, double endX, double endY, String color) {
        super("LINE", startX, startY, 0, 0, color);
        this.endX = endX;
        this.endY = endY;
    }

    public double getEndX() { return endX; }
    public void setEndX(double endX) { this.endX = endX; }

    public double getEndY() { return endY; }
    public void setEndY(double endY) { this.endY = endY; }
}

package app.dao;

import app.factory.ShapeFactory;
import app.model.*;
import app.singleton.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO Pattern: handles persistence of drawings and shapes to SQLite.
 */
public class DrawingDAO {

    private final Connection connection;

    public DrawingDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public int saveDrawing(DrawingModel drawing) throws SQLException {
        String sql = "INSERT INTO drawings (name) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, drawing.getName() != null ? drawing.getName() : "Untitled");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int drawingId = rs.getInt(1);
                    saveShapes(drawingId, drawing.getShapes());
                    return drawingId;
                }
            }
        }
        return -1;
    }

    private void saveShapes(int drawingId, List<ShapeModel> shapes) throws SQLException {
        String sql = """
            INSERT INTO shapes (drawing_id, type, x, y, width, height, end_x, end_y, color)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (ShapeModel shape : shapes) {
                ps.setInt(1, drawingId);
                ps.setString(2, shape.getType());
                ps.setDouble(3, shape.getX());
                ps.setDouble(4, shape.getY());
                ps.setDouble(5, shape.getWidth());
                ps.setDouble(6, shape.getHeight());
                if (shape instanceof LineModel line) {
                    ps.setDouble(7, line.getEndX());
                    ps.setDouble(8, line.getEndY());
                } else {
                    ps.setNull(7, Types.REAL);
                    ps.setNull(8, Types.REAL);
                }
                ps.setString(9, shape.getColor());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public List<DrawingModel> getAllDrawings() throws SQLException {
        List<DrawingModel> drawings = new ArrayList<>();
        String sql = "SELECT id, name FROM drawings ORDER BY created_at DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                DrawingModel dm = new DrawingModel(rs.getString("name"));
                dm.setId(rs.getInt("id"));
                drawings.add(dm);
            }
        }
        return drawings;
    }

    public DrawingModel loadDrawing(int drawingId) throws SQLException {
        DrawingModel drawing = null;

        String drawingSQL = "SELECT id, name FROM drawings WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(drawingSQL)) {
            ps.setInt(1, drawingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    drawing = new DrawingModel(rs.getString("name"));
                    drawing.setId(rs.getInt("id"));
                }
            }
        }

        if (drawing == null) return null;

        String shapesSQL = "SELECT * FROM shapes WHERE drawing_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(shapesSQL)) {
            ps.setInt(1, drawingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("type");
                    double x = rs.getDouble("x");
                    double y = rs.getDouble("y");
                    double width = rs.getDouble("width");
                    double height = rs.getDouble("height");
                    String color = rs.getString("color");

                    ShapeModel shape;
                    if ("LINE".equals(type)) {
                        double endX = rs.getDouble("end_x");
                        double endY = rs.getDouble("end_y");
                        shape = ShapeFactory.createLine(x, y, endX, endY, color);
                    } else {
                        shape = ShapeFactory.create(type, x, y, width, height, color);
                    }
                    shape.setId(rs.getInt("id"));
                    drawing.addShape(shape);
                }
            }
        }

        return drawing;
    }
}

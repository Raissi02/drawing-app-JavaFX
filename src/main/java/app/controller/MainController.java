package app.controller;

import app.command.*;
import app.dao.DrawingDAO;
import app.factory.ShapeFactory;
import app.model.*;
import app.strategy.*;
import app.util.Logger;
import app.util.ShapeRenderer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

/**
 * MVC Controller: handles all user interactions and coordinates model updates.
 */
public class MainController implements Initializable {

    // ── FXML injections ──────────────────────────────────────────────────────
    @FXML private Pane drawingPane;
    @FXML private ToggleButton btnRectangle;
    @FXML private ToggleButton btnCircle;
    @FXML private ToggleButton btnLine;
    @FXML private Button btnUndo;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnOpen;
    @FXML private Button btnClear;
    @FXML private ColorPicker colorPicker;
    @FXML private ComboBox<String> logStrategyCombo;
    @FXML private Label statusLabel;
    @FXML private TextArea logArea;

    // ── State ─────────────────────────────────────────────────────────────────
    private final DrawingModel drawingModel = new DrawingModel("New Drawing");
    private final CommandHistory commandHistory = new CommandHistory();
    private final DrawingDAO drawingDAO = new DrawingDAO();
    private final Logger logger = Logger.getInstance();

    // Map JavaFX Shape → ShapeModel for selection/delete
    private final Map<Shape, ShapeModel> shapeMap = new LinkedHashMap<>();
    private Shape selectedJfxShape = null;
    private ShapeModel selectedModel = null;

    // Drawing state
    private double startX, startY;
    private Shape previewShape;
    private String currentShapeType = "RECTANGLE";

    // ── Initialization ────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colorPicker.setValue(Color.DODGERBLUE);

        // Logging strategy combo
        logStrategyCombo.getItems().addAll("Console", "File", "Database");
        logStrategyCombo.setValue("Console");
        logStrategyCombo.setOnAction(e -> changeLoggingStrategy(logStrategyCombo.getValue()));

        // Shape toggle group
        ToggleGroup shapeGroup = new ToggleGroup();
        btnRectangle.setToggleGroup(shapeGroup);
        btnCircle.setToggleGroup(shapeGroup);
        btnLine.setToggleGroup(shapeGroup);
        btnRectangle.setSelected(true);

        btnRectangle.setOnAction(e -> selectShapeType("RECTANGLE"));
        btnCircle.setOnAction(e -> selectShapeType("CIRCLE"));
        btnLine.setOnAction(e -> selectShapeType("LINE"));

        // Drawing pane events
        drawingPane.setOnMousePressed(this::onMousePressed);
        drawingPane.setOnMouseDragged(this::onMouseDragged);
        drawingPane.setOnMouseReleased(this::onMouseReleased);

        setStatus("Ready – select a shape and draw on the canvas.");
        logger.log("APP_START", "Application initialized");
    }

    // ── Shape Type Selection ──────────────────────────────────────────────────
    private void selectShapeType(String type) {
        currentShapeType = type;
        deselectCurrentShape();
        setStatus("Drawing mode: " + type);
        logger.log("SHAPE_SELECT", type);
        appendLog("Selected shape: " + type);
    }

    // ── Mouse Events ──────────────────────────────────────────────────────────
    private void onMousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();

        // Check if clicking an existing shape
        if (e.getTarget() instanceof Shape clicked && shapeMap.containsKey(clicked)) {
            selectShape(clicked);
            return;
        }

        deselectCurrentShape();
        createPreviewShape();
    }

    private void onMouseDragged(MouseEvent e) {
        if (previewShape == null) return;
        updatePreviewShape(e.getX(), e.getY());
    }

    private void onMouseReleased(MouseEvent e) {
        if (previewShape == null) return;

        double endX = e.getX();
        double endY = e.getY();
        double w = endX - startX;
        double h = endY - startY;

        // Discard too-small shapes
        if (Math.abs(w) < 5 && Math.abs(h) < 5 && !"LINE".equals(currentShapeType)) {
            drawingPane.getChildren().remove(previewShape);
            previewShape = null;
            return;
        }

        String color = toHexColor(colorPicker.getValue());
        ShapeModel model = buildModel(color, endX, endY, w, h);
        Shape finalShape = ShapeRenderer.render(model);

        drawingPane.getChildren().remove(previewShape);
        previewShape = null;

        AddShapeCommand cmd = new AddShapeCommand(drawingModel, model, drawingPane, finalShape);
        commandHistory.execute(cmd);

        // Make shape selectable
        finalShape.setOnMousePressed(ev -> {
            selectShape(finalShape);
            ev.consume();
        });
        shapeMap.put(finalShape, model);

        setStatus("Added: " + model);
        logger.log("SHAPE_ADD", model.toString());
        appendLog("Added: " + model);
    }

    // ── Preview Shapes ────────────────────────────────────────────────────────
    private void createPreviewShape() {
        String color = toHexColor(colorPicker.getValue());
        previewShape = switch (currentShapeType) {
            case "RECTANGLE" -> {
                javafx.scene.shape.Rectangle r = new javafx.scene.shape.Rectangle(startX, startY, 1, 1);
                r.setFill(Color.web(color, 0.2));
                r.setStroke(Color.web(color));
                r.setStrokeWidth(2);
                r.getStrokeDashArray().addAll(5.0, 5.0);
                yield r;
            }
            case "CIRCLE" -> {
                javafx.scene.shape.Circle c = new javafx.scene.shape.Circle(startX, startY, 1);
                c.setFill(Color.web(color, 0.2));
                c.setStroke(Color.web(color));
                c.setStrokeWidth(2);
                c.getStrokeDashArray().addAll(5.0, 5.0);
                yield c;
            }
            case "LINE" -> {
                javafx.scene.shape.Line l = new javafx.scene.shape.Line(startX, startY, startX, startY);
                l.setStroke(Color.web(color));
                l.setStrokeWidth(2);
                l.getStrokeDashArray().addAll(5.0, 5.0);
                yield l;
            }
            default -> null;
        };
        if (previewShape != null) {
            previewShape.setMouseTransparent(true);
            drawingPane.getChildren().add(previewShape);
        }
    }

    private void updatePreviewShape(double curX, double curY) {
        switch (currentShapeType) {
            case "RECTANGLE" -> {
                javafx.scene.shape.Rectangle r = (javafx.scene.shape.Rectangle) previewShape;
                r.setX(Math.min(startX, curX));
                r.setY(Math.min(startY, curY));
                r.setWidth(Math.abs(curX - startX));
                r.setHeight(Math.abs(curY - startY));
            }
            case "CIRCLE" -> {
                javafx.scene.shape.Circle c = (javafx.scene.shape.Circle) previewShape;
                double radius = Math.min(Math.abs(curX - startX), Math.abs(curY - startY)) / 2.0;
                c.setCenterX(startX + (curX > startX ? radius : -radius));
                c.setCenterY(startY + (curY > startY ? radius : -radius));
                c.setRadius(radius);
            }
            case "LINE" -> {
                javafx.scene.shape.Line l = (javafx.scene.shape.Line) previewShape;
                l.setEndX(curX);
                l.setEndY(curY);
            }
        }
    }

    // ── Model Builder ─────────────────────────────────────────────────────────
    private ShapeModel buildModel(String color, double endX, double endY, double w, double h) {
        return switch (currentShapeType) {
            case "RECTANGLE" -> ShapeFactory.create("RECTANGLE",
                    Math.min(startX, endX), Math.min(startY, endY),
                    Math.abs(w), Math.abs(h), color);
            case "CIRCLE" -> {
                double radius = Math.min(Math.abs(w), Math.abs(h)) / 2.0;
                yield ShapeFactory.create("CIRCLE",
                        Math.min(startX, endX), Math.min(startY, endY),
                        radius * 2, radius * 2, color);
            }
            case "LINE" -> ShapeFactory.createLine(startX, startY, endX, endY, color);
            default -> throw new IllegalStateException("Unknown shape: " + currentShapeType);
        };
    }

    // ── Selection ─────────────────────────────────────────────────────────────
    private void selectShape(Shape shape) {
        deselectCurrentShape();
        selectedJfxShape = shape;
        selectedModel = shapeMap.get(shape);
        shape.setEffect(new javafx.scene.effect.DropShadow(10, Color.ORANGERED));
        setStatus("Selected: " + (selectedModel != null ? selectedModel : "unknown"));
    }

    private void deselectCurrentShape() {
        if (selectedJfxShape != null) {
            selectedJfxShape.setEffect(null);
            selectedJfxShape = null;
            selectedModel = null;
        }
    }

    // ── Toolbar Actions ───────────────────────────────────────────────────────
    @FXML
    private void onUndo() {
        if (commandHistory.canUndo()) {
            String desc = commandHistory.getLastDescription();
            commandHistory.undo();
            // Remove from shapeMap if shape was removed from canvas
            shapeMap.entrySet().removeIf(entry -> !drawingPane.getChildren().contains(entry.getKey()));
            selectedJfxShape = null;
            selectedModel = null;
            setStatus("Undone: " + desc);
            logger.log("UNDO", desc);
            appendLog("Undo: " + desc);
        } else {
            setStatus("Nothing to undo.");
        }
    }

    @FXML
    private void onDelete() {
        if (selectedJfxShape == null || selectedModel == null) {
            setStatus("No shape selected.");
            return;
        }
        DeleteShapeCommand cmd = new DeleteShapeCommand(
                drawingModel, selectedModel, drawingPane, selectedJfxShape);
        commandHistory.execute(cmd);
        shapeMap.remove(selectedJfxShape);
        String desc = "Deleted " + selectedModel.getType();
        logger.log("SHAPE_DELETE", selectedModel.toString());
        appendLog(desc);
        selectedJfxShape = null;
        selectedModel = null;
        setStatus(desc);
    }

    @FXML
    private void onSave() {
        try {
            int id = drawingDAO.saveDrawing(drawingModel);
            setStatus("Drawing saved (ID=" + id + ")");
            logger.log("SAVE", "Drawing saved, ID=" + id + ", shapes=" + drawingModel.getShapes().size());
            appendLog("Saved drawing ID=" + id);
        } catch (SQLException e) {
            showError("Save failed: " + e.getMessage());
        }
    }

    @FXML
    private void onOpen() {
        try {
            List<DrawingModel> drawings = drawingDAO.getAllDrawings();
            if (drawings.isEmpty()) {
                showInfo("No saved drawings found.");
                return;
            }

            // Build a choice dialog
            List<String> choices = drawings.stream()
                    .map(d -> "[" + d.getId() + "] " + d.getName())
                    .toList();

            ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
            dialog.setTitle("Open Drawing");
            dialog.setHeaderText("Select a drawing to load:");
            dialog.setContentText("Drawing:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(choice -> {
                int id = Integer.parseInt(choice.substring(1, choice.indexOf(']')));
                loadDrawing(id);
            });
        } catch (SQLException e) {
            showError("Open failed: " + e.getMessage());
        }
    }

    private void loadDrawing(int id) {
        try {
            DrawingModel loaded = drawingDAO.loadDrawing(id);
            if (loaded == null) {
                showError("Drawing not found.");
                return;
            }

            // Clear current state
            drawingPane.getChildren().clear();
            drawingModel.clear();
            shapeMap.clear();
            commandHistory.clear();
            selectedJfxShape = null;
            selectedModel = null;

            for (ShapeModel model : loaded.getShapes()) {
                drawingModel.addShape(model);
                Shape jfx = ShapeRenderer.render(model);
                jfx.setOnMousePressed(ev -> {
                    selectShape(jfx);
                    ev.consume();
                });
                drawingPane.getChildren().add(jfx);
                shapeMap.put(jfx, model);
            }

            setStatus("Loaded: " + loaded.getName() + " (" + loaded.getShapes().size() + " shapes)");
            logger.log("OPEN", "Loaded drawing ID=" + id);
            appendLog("Opened drawing ID=" + id);
        } catch (SQLException e) {
            showError("Load failed: " + e.getMessage());
        }
    }

    @FXML
    private void onClear() {
        drawingPane.getChildren().clear();
        drawingModel.clear();
        shapeMap.clear();
        commandHistory.clear();
        selectedJfxShape = null;
        selectedModel = null;
        setStatus("Canvas cleared.");
        logger.log("CLEAR", "Canvas cleared");
        appendLog("Canvas cleared");
    }

    // ── Logging Strategy ──────────────────────────────────────────────────────
    private void changeLoggingStrategy(String strategy) {
        LoggingStrategy s = switch (strategy) {
            case "Console"  -> new ConsoleLoggerStrategy();
            case "File"     -> new FileLoggerStrategy();
            case "Database" -> new DatabaseLoggerStrategy();
            default         -> new ConsoleLoggerStrategy();
        };
        logger.setStrategy(s);
        setStatus("Logging strategy: " + strategy);
        logger.log("STRATEGY_CHANGE", "Switched to " + strategy + " logging");
        appendLog("Logging → " + strategy);
    }

    // ── UI Helpers ────────────────────────────────────────────────────────────
    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }

    private void appendLog(String msg) {
        logArea.appendText(msg + "\n");
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setTitle("Error");
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle("Info");
        alert.showAndWait();
    }

    private String toHexColor(Color c) {
        return String.format("#%02X%02X%02X",
                (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue() * 255));
    }
}

package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Point2D;
import model.Grid;
import model.Ship;
import model.ShipFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Ship placement screen with drag and drop.
 */
public class PlacementScreen {

    private final BorderPane root;
    private final Stage stage;
    private final Grid playerGrid;
    private final GridBoard gridBoard;
    private final List<Ship> fleet;
    private final List<ShipTile> tiles = new ArrayList<>();
    private final VBox shipPanel;
    private final Label statusLabel;

    public PlacementScreen(Stage stage) {
        this.stage = stage;
        this.playerGrid = new Grid();
        this.fleet = ShipFactory.createFleet();
        this.gridBoard = new GridBoard(playerGrid, false);
        this.shipPanel = new VBox(12);
        this.statusLabel = new Label("Drag ships onto your grid. Right-click a ship to rotate it.");
        root = new BorderPane();
        root.setStyle("-fx-background-color: #060f1e;");
        buildUI();
    }

    private void buildUI() {
        // ── Title ─────────────────────────────────────────────────────────────
        Text title = new Text("⚓  BATTLESHIP");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-fill: #5bc0de;");
        Text sub = new Text("Place your fleet");
        sub.setStyle("-fx-font-size: 16px; -fx-fill: #a0c8e0;");
        VBox titleBox = new VBox(6, title, sub);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(28, 0, 16, 0));
        root.setTop(titleBox);

        // ── Center: Grid ──────────────────────────────────────────────────────
        StackPane gridWrapper = new StackPane(gridBoard);
        gridWrapper.setAlignment(Pos.CENTER);
        gridWrapper.setPadding(new Insets(10, 10, 10, 60));

        // ── Right: Ship sidebar ───────────────────────────────────────────────
        Label panelTitle = new Label("YOUR FLEET");
        panelTitle.setStyle("-fx-text-fill: #5bc0de; -fx-font-size: 14px; -fx-font-weight: bold;");

        shipPanel.setAlignment(Pos.TOP_LEFT);
        shipPanel.setPadding(new Insets(20));
        shipPanel.setStyle("-fx-background-color: #0a1628; -fx-background-radius: 12;");
        shipPanel.setPrefWidth(250);
        shipPanel.setMinWidth(250);
        shipPanel.getChildren().add(panelTitle);

        for (Ship ship : fleet) {
            ShipTile tile = new ShipTile(ship);
            tiles.add(tile);
            shipPanel.getChildren().add(tile);
            setupDrag(tile);
        }

        Label hint = new Label("💡 Right-click to rotate");
        hint.setStyle("-fx-text-fill: #5f8aaa; -fx-font-size: 12px;");
        shipPanel.getChildren().add(hint);

        ScrollablePanel right = new ScrollablePanel(shipPanel);
        right.setPadding(new Insets(20, 40, 20, 10));

        // Center layout: grid + right panel side by side
        HBox centerLayout = new HBox(30, gridWrapper, shipPanel);
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.setPadding(new Insets(10, 40, 10, 40));
        HBox.setHgrow(gridWrapper, Priority.ALWAYS);
        root.setCenter(centerLayout);

        // ── Bottom ────────────────────────────────────────────────────────────
        statusLabel.setStyle("-fx-text-fill: #a0c8e0; -fx-font-size: 13px;");

        Button startBtn = new Button("START BATTLE ▶");
        startBtn.setStyle(
            "-fx-background-color: #1a6496; -fx-text-fill: white; " +
            "-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 12 30; " +
            "-fx-background-radius: 8; -fx-cursor: hand;"
        );
        startBtn.setOnMouseEntered(e -> startBtn.setStyle(startBtn.getStyle().replace("#1a6496", "#2a8acc")));
        startBtn.setOnMouseExited(e -> startBtn.setStyle(startBtn.getStyle().replace("#2a8acc", "#1a6496")));
        startBtn.setOnAction(e -> handleStart());

        HBox bottom = new HBox(20, statusLabel, startBtn);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(14, 0, 24, 0));
        root.setBottom(bottom);
    }

    // ── Drag & Drop ───────────────────────────────────────────────────────────
    private void setupDrag(ShipTile tile) {
        int cs = BattleshipApp.CELL_SIZE;

        tile.setOnContextMenuRequested(e -> {
            tile.toggleOrientation();
            e.consume();
        });

        tile.setOnMousePressed(e -> {
            tile.setDragOffset(e.getX(), e.getY());
            tile.toFront();
        });

        tile.setOnMouseDragged(e -> {
            Point2D gridPos = gridBoard.sceneToLocal(e.getSceneX(), e.getSceneY());
            int col = (int)((gridPos.getX() - 24) / (cs + 2));
            int row = (int)((gridPos.getY() - 24) / (cs + 2));
            boolean valid = playerGrid.canPlace(tile.getShip(), row, col,
                tile.isHorizontal() ? Ship.Orientation.HORIZONTAL : Ship.Orientation.VERTICAL);
            gridBoard.highlightCells(row, col, tile.getShip().getSize(), tile.isHorizontal(), valid);
        });

        tile.setOnMouseReleased(e -> {
            Point2D gridPos = gridBoard.sceneToLocal(e.getSceneX(), e.getSceneY());
            int col = (int)((gridPos.getX() - 24) / (cs + 2));
            int row = (int)((gridPos.getY() - 24) / (cs + 2));
            Ship.Orientation ori = tile.isHorizontal()
                ? Ship.Orientation.HORIZONTAL : Ship.Orientation.VERTICAL;

            boolean placed = playerGrid.placeShip(tile.getShip(), row, col, ori);
            if (placed) {
                shipPanel.getChildren().remove(tile);
                tiles.remove(tile);
                gridBoard.refreshAll();
                statusLabel.setText("✅ " + tile.getShip().getName() + " placed!");
            } else {
                gridBoard.refreshAll();
                statusLabel.setText("❌ Can't place there. Try again.");
            }
        });
    }

    private void handleStart() {
        if (!tiles.isEmpty()) {
            statusLabel.setText("⚠️ Place all " + tiles.size() + " remaining ship(s) first!");
            return;
        }
        GameScreen gameScreen = new GameScreen(stage, playerGrid);
        stage.getScene().setRoot(gameScreen.getRoot());
    }

    // Simple wrapper — no actual scrolling needed, just padding
    static class ScrollablePanel extends StackPane {
        ScrollablePanel(javafx.scene.Node content) {
            getChildren().add(content);
        }
    }

    public BorderPane getRoot() { return root; }
}
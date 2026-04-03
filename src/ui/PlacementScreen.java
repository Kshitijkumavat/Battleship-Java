package ui;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Grid;
import model.Ship;
import model.ShipFactory;

import java.util.ArrayList;
import java.util.List;

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
        this.shipPanel = new VBox(14);
        this.statusLabel = new Label("Drag ships onto your grid  •  Right-click to rotate");
        root = new BorderPane();
        root.setStyle("-fx-background-color: #060f1e;");
        buildUI();
    }

    private void buildUI() {
        // ── Header ────────────────────────────────────────────────────────────
        Text title = new Text("BATTLESHIP");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 40));
        title.setFill(Color.web("#5bc0de"));

        Text sub = new Text("NAVAL WARFARE EDITION");
        sub.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        sub.setFill(Color.web("#2a6a8a"));

        Line divider = new Line(0, 0, 320, 0);
        divider.setStroke(Color.web("#1a3a5c"));
        divider.setStrokeWidth(1);

        Text phase = new Text("PHASE 1  —  DEPLOY YOUR FLEET");
        phase.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        phase.setFill(Color.web("#3a8aaa"));

        VBox header = new VBox(4, title, sub, divider, phase);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(30, 0, 20, 0));
        root.setTop(header);

        // ── Grid area ─────────────────────────────────────────────────────────
        StackPane gridWrapper = new StackPane(gridBoard);
        gridWrapper.setAlignment(Pos.CENTER);
        gridWrapper.setPadding(new Insets(10, 20, 10, 60));

        // ── Ship sidebar ──────────────────────────────────────────────────────
        Text fleetTitle = new Text("YOUR FLEET");
        fleetTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        fleetTitle.setFill(Color.web("#5bc0de"));

        Line fleetDivider = new Line(0, 0, 200, 0);
        fleetDivider.setStroke(Color.web("#1a3a5c"));

        shipPanel.setAlignment(Pos.TOP_LEFT);
        shipPanel.setPadding(new Insets(20));
        shipPanel.setStyle(
            "-fx-background-color: #08162a;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #1a3a5c;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;"
        );
        shipPanel.setPrefWidth(260);
        shipPanel.setMinWidth(260);

        Text hint = new Text("Right-click to rotate  •  Drag to place");
        hint.setFont(Font.font("Segoe UI", 11));
        hint.setFill(Color.web("#2a5a7a"));

        shipPanel.getChildren().addAll(fleetTitle, fleetDivider);
        for (Ship ship : fleet) {
            ShipTile tile = new ShipTile(ship);
            tiles.add(tile);
            shipPanel.getChildren().add(tile);
            setupDrag(tile);
        }
        shipPanel.getChildren().add(hint);

        VBox sidebarWrapper = new VBox(shipPanel);
        sidebarWrapper.setAlignment(Pos.CENTER_LEFT);
        sidebarWrapper.setPadding(new Insets(10, 50, 10, 20));

        HBox center = new HBox(30, gridWrapper, sidebarWrapper);
        center.setAlignment(Pos.CENTER);
        HBox.setHgrow(gridWrapper, Priority.ALWAYS);
        root.setCenter(center);

        // ── Footer ────────────────────────────────────────────────────────────
        statusLabel.setFont(Font.font("Segoe UI", 13));
        statusLabel.setTextFill(Color.web("#4a8fa8"));

        Button startBtn = buildButton("LAUNCH ATTACK  ▶", "#cc5500", "#ff7700");
        startBtn.setOnAction(e -> handleStart());

        Button randomBtn = buildButton("RANDOM PLACEMENT  ⟳", "#1a4a6a", "#2a6a9a");
        randomBtn.setOnAction(e -> randomPlacement());

        HBox footer = new HBox(16, statusLabel, new Region(), randomBtn, startBtn);
        HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(14, 50, 24, 60));
        footer.setStyle("-fx-background-color: #08162a; -fx-border-color: #1a3a5c; -fx-border-width: 1 0 0 0;");
        root.setBottom(footer);
    }

    private Button buildButton(String text, String normalColor, String hoverColor) {
        Button btn = new Button(text);
        String base = "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 13px; " +
                      "-fx-text-fill: white; -fx-padding: 10 24; -fx-background-radius: 6; -fx-cursor: hand; ";
        btn.setStyle(base + "-fx-background-color: " + normalColor + ";");
        btn.setOnMouseEntered(e -> btn.setStyle(base + "-fx-background-color: " + hoverColor + ";"));
        btn.setOnMouseExited(e -> btn.setStyle(base + "-fx-background-color: " + normalColor + ";"));
        return btn;
    }

    private void randomPlacement() {
        // Clear existing placements
        for (ShipTile tile : new ArrayList<>(tiles)) {
            shipPanel.getChildren().remove(tile);
        }
        tiles.clear();

        Grid fresh = new Grid();
        java.util.Random rng = new java.util.Random();
        for (Ship ship : fleet) {
            boolean placed = false;
            while (!placed) {
                int row = rng.nextInt(Grid.SIZE);
                int col = rng.nextInt(Grid.SIZE);
                Ship.Orientation ori = rng.nextBoolean()
                    ? Ship.Orientation.HORIZONTAL : Ship.Orientation.VERTICAL;
                placed = playerGrid.placeShip(ship, row, col, ori);
            }
        }
        gridBoard.refreshAll();
        statusLabel.setText("✔  Fleet deployed randomly. Click LAUNCH ATTACK to begin!");
        statusLabel.setTextFill(Color.web("#00cc66"));
    }

    private void setupDrag(ShipTile tile) {
        int cs = BattleshipApp.CELL_SIZE;
        int gap = 3;

        tile.setOnContextMenuRequested(e -> { tile.toggleOrientation(); e.consume(); });
        tile.setOnMousePressed(e -> { tile.setDragOffset(e.getX(), e.getY()); tile.toFront(); });

        tile.setOnMouseDragged(e -> {
            Point2D gp = gridBoard.sceneToLocal(e.getSceneX(), e.getSceneY());
            int col = (int)((gp.getX() - 22) / (cs + gap));
            int row = (int)((gp.getY() - 22) / (cs + gap));
            Ship.Orientation ori = tile.isHorizontal()
                ? Ship.Orientation.HORIZONTAL : Ship.Orientation.VERTICAL;
            boolean valid = playerGrid.canPlace(tile.getShip(), row, col, ori);
            gridBoard.highlightCells(row, col, tile.getShip().getSize(), tile.isHorizontal(), valid);
        });

        tile.setOnMouseReleased(e -> {
            Point2D gp = gridBoard.sceneToLocal(e.getSceneX(), e.getSceneY());
            int col = (int)((gp.getX() - 22) / (cs + gap));
            int row = (int)((gp.getY() - 22) / (cs + gap));
            Ship.Orientation ori = tile.isHorizontal()
                ? Ship.Orientation.HORIZONTAL : Ship.Orientation.VERTICAL;
            boolean placed = playerGrid.placeShip(tile.getShip(), row, col, ori);
            if (placed) {
                shipPanel.getChildren().remove(tile);
                tiles.remove(tile);
                gridBoard.refreshAll();
                int remaining = tiles.size();
                statusLabel.setText(remaining == 0
                    ? "✔  All ships deployed! Click LAUNCH ATTACK."
                    : "✔  " + tile.getShip().getName() + " placed  —  " + remaining + " remaining");
                statusLabel.setTextFill(remaining == 0
                    ? Color.web("#00cc66") : Color.web("#5bc0de"));
            } else {
                gridBoard.refreshAll();
                statusLabel.setText("✖  Invalid position — try again");
                statusLabel.setTextFill(Color.web("#cc3300"));
            }
        });
    }

    private void handleStart() {
        if (!tiles.isEmpty()) {
            statusLabel.setText("⚠  Place all " + tiles.size() + " remaining ship(s) first");
            statusLabel.setTextFill(Color.web("#cc7700"));
            return;
        }
        GameScreen gameScreen = new GameScreen(stage, playerGrid);
        stage.getScene().setRoot(gameScreen.getRoot());
    }

    public BorderPane getRoot() { return root; }
}
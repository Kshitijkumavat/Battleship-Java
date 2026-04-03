package ui;

import ai.HuntTargetStrategy;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Grid;
import model.Ship;
import player.AIPlayer;

public class GameScreen {

    private final BorderPane root;
    private final Stage stage;
    private final Grid playerGrid;
    private final GridBoard playerBoard;
    private final GridBoard aiBoard;
    private final AIPlayer aiPlayer;
    private final HuntTargetStrategy aiStrategy;

    private final Label statusLabel;
    private final Label playerHitsLabel;
    private final Label aiHitsLabel;
    private final Label playerShipsLabel;
    private final Label aiShipsLabel;
    private final Label turnLabel;

    private boolean playerTurn = true;

    public GameScreen(Stage stage, Grid playerGrid) {
        this.stage = stage;
        this.playerGrid = playerGrid;
        this.aiPlayer = new AIPlayer("Admiral AI");
        this.aiStrategy = new HuntTargetStrategy();
        this.aiPlayer.placeShips();
        this.playerBoard = new GridBoard(playerGrid, false);
        this.aiBoard = new GridBoard(aiPlayer.getMyGrid(), true);
        this.statusLabel = new Label("Your turn — click a cell on the enemy grid");
        this.playerHitsLabel = new Label("0");
        this.aiHitsLabel = new Label("0");
        this.playerShipsLabel = new Label("4");
        this.aiShipsLabel = new Label("4");
        this.turnLabel = new Label("YOUR TURN");
        root = new BorderPane();
        root.setStyle("-fx-background-color: #060f1e;");
        buildUI();
        setupAttackHandler();
    }

    private void buildUI() {
        // ── Header ────────────────────────────────────────────────────────────
        Text title = new Text("BATTLESHIP");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setFill(Color.web("#5bc0de"));

        turnLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        turnLabel.setTextFill(Color.web("#00cc66"));
        turnLabel.setStyle("-fx-background-color: #002a14; -fx-background-radius: 4; -fx-padding: 4 12;");

        HBox titleRow = new HBox(16, title, turnLabel);
        titleRow.setAlignment(Pos.CENTER);
        titleRow.setPadding(new Insets(20, 0, 6, 0));

        Line divider = new Line(0, 0, 900, 0);
        divider.setStroke(Color.web("#1a3a5c"));

        VBox header = new VBox(6, titleRow, divider);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 10, 0));
        root.setTop(header);

        // ── Stats bar (left) ──────────────────────────────────────────────────
        VBox playerStats = buildStatsPanel("YOU", playerHitsLabel, playerShipsLabel, "#5bc0de", "#003a5a");
        VBox aiStats = buildStatsPanel("AI", aiHitsLabel, aiShipsLabel, "#e05050", "#3a0a0a");

        // ── Grid panels ───────────────────────────────────────────────────────
        Label yourLabel = makeGridLabel("YOUR WATERS", "#4a8fa8");
        Label enemyLabel = makeGridLabel("ENEMY WATERS", "#a84a4a");

        VBox leftPanel = new VBox(10, playerStats, yourLabel, playerBoard);
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setPadding(new Insets(0, 20, 0, 50));

        VBox rightPanel = new VBox(10, aiStats, enemyLabel, aiBoard);
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPadding(new Insets(0, 50, 0, 20));

        // Vertical divider
        VBox dividerBox = new VBox();
        dividerBox.setPrefWidth(1);
        dividerBox.setStyle("-fx-background-color: #1a3a5c;");
        dividerBox.setMinHeight(500);

        HBox boards = new HBox(40, leftPanel, dividerBox, rightPanel);
        boards.setAlignment(Pos.CENTER);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);
        root.setCenter(boards);

        // ── Footer ────────────────────────────────────────────────────────────
        statusLabel.setFont(Font.font("Segoe UI", 14));
        statusLabel.setTextFill(Color.web("#4a8fa8"));

        HBox footer = new HBox(statusLabel);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(14, 0, 24, 0));
        footer.setStyle("-fx-background-color: #08162a; -fx-border-color: #1a3a5c; -fx-border-width: 1 0 0 0;");
        root.setBottom(footer);
    }

    private VBox buildStatsPanel(String who, Label hitsLabel, Label shipsLabel, String color, String bg) {
        hitsLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        hitsLabel.setTextFill(Color.web(color));

        shipsLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        shipsLabel.setTextFill(Color.web(color));

        Label hTitle = new Label("HITS");
        hTitle.setFont(Font.font("Segoe UI", 10));
        hTitle.setTextFill(Color.web("#3a6a8a"));

        Label sTitle = new Label("SHIPS LEFT");
        sTitle.setFont(Font.font("Segoe UI", 10));
        sTitle.setTextFill(Color.web("#3a6a8a"));

        VBox hBox = new VBox(2, hitsLabel, hTitle);
        hBox.setAlignment(Pos.CENTER);

        VBox sBox = new VBox(2, shipsLabel, sTitle);
        sBox.setAlignment(Pos.CENTER);

        HBox stats = new HBox(24, hBox, sBox);
        stats.setAlignment(Pos.CENTER);
        stats.setPadding(new Insets(8, 16, 8, 16));
        stats.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 8; " +
                       "-fx-border-color: " + color + "44; -fx-border-width: 1; -fx-border-radius: 8;");

        VBox panel = new VBox(stats);
        panel.setAlignment(Pos.CENTER);
        return panel;
    }

    private Label makeGridLabel(String text, String color) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lbl.setTextFill(Color.web(color));
        return lbl;
    }

    private void setupAttackHandler() {
        aiBoard.setClickHandler((row, col) -> {
            if (!playerTurn) return;
            if (aiPlayer.getMyGrid().isAlreadyAttacked(row, col)) {
                flash("Already attacked that position!", "#cc7700");
                return;
            }
            boolean hit = aiPlayer.getMyGrid().receiveAttack(row, col);
            aiBoard.refreshAll();
            updateStats();
            String coord = (char)('A' + row) + "" + (col + 1);
            if (hit) {
                flash("HIT at " + coord + "!", "#ff4422");
                checkSunk(aiPlayer.getMyGrid(), row, col);
            } else {
                flash("Miss at " + coord + " — AI is responding...", "#4a8fa8");
            }
            if (aiPlayer.getMyGrid().allShipsSunk()) { endGame(true); return; }
            playerTurn = false;
            setTurnIndicator(false);
            PauseTransition pause = new PauseTransition(Duration.millis(1000));
            pause.setOnFinished(e -> aiTurn());
            pause.play();
        });
    }

    private void aiTurn() {
        int[] attack = aiStrategy.chooseAttack(playerGrid);
        boolean hit = playerGrid.receiveAttack(attack[0], attack[1]);
        aiStrategy.registerResult(attack[0], attack[1], hit, playerGrid);
        playerBoard.refreshAll();
        updateStats();
        String coord = (char)('A' + attack[0]) + "" + (attack[1] + 1);
        if (hit) {
            flash("AI hit your ship at " + coord + "! Your turn.", "#e05050");
            checkSunk(playerGrid, attack[0], attack[1]);
        } else {
            flash("AI missed at " + coord + ". Your turn!", "#00cc66");
        }
        if (playerGrid.allShipsSunk()) { endGame(false); return; }
        playerTurn = true;
        setTurnIndicator(true);
    }

    private void setTurnIndicator(boolean isPlayer) {
        if (isPlayer) {
            turnLabel.setText("YOUR TURN");
            turnLabel.setTextFill(Color.web("#00cc66"));
            turnLabel.setStyle("-fx-background-color: #002a14; -fx-background-radius: 4; -fx-padding: 4 12;");
        } else {
            turnLabel.setText("AI THINKING...");
            turnLabel.setTextFill(Color.web("#e05050"));
            turnLabel.setStyle("-fx-background-color: #2a0a0a; -fx-background-radius: 4; -fx-padding: 4 12;");
        }
    }

    private void flash(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setTextFill(Color.web(color));
        FadeTransition ft = new FadeTransition(Duration.millis(150), statusLabel);
        ft.setFromValue(0.3);
        ft.setToValue(1.0);
        ft.play();
    }

    private void checkSunk(Grid grid, int row, int col) {
        grid.getShips().stream()
            .filter(s -> s.isSunk() && s.isPlaced())
            .filter(s -> {
                for (int i = 0; i < s.getSize(); i++) {
                    int r = s.getStartRow() + (s.getOrientation() == Ship.Orientation.VERTICAL ? i : 0);
                    int c = s.getStartCol() + (s.getOrientation() == Ship.Orientation.HORIZONTAL ? i : 0);
                    if (r == row && c == col) return true;
                }
                return false;
            })
            .findFirst()
            .ifPresent(s -> flash(s.getName().toUpperCase() + " SUNK!", "#ff8800"));
    }

    private void updateStats() {
        long pHits = countHits(aiPlayer.getMyGrid());
        long aHits = countHits(playerGrid);
        long pShips = playerGrid.getShips().stream().filter(s -> !s.isSunk()).count();
        long aShips = aiPlayer.getMyGrid().getShips().stream().filter(s -> !s.isSunk()).count();
        playerHitsLabel.setText(String.valueOf(pHits));
        aiHitsLabel.setText(String.valueOf(aHits));
        playerShipsLabel.setText(String.valueOf(pShips));
        aiShipsLabel.setText(String.valueOf(aShips));
    }

    private long countHits(Grid grid) {
        long count = 0;
        for (int r = 0; r < Grid.SIZE; r++)
            for (int c = 0; c < Grid.SIZE; c++)
                if (grid.getCell(r, c).isHit()) count++;
        return count;
    }

    private void endGame(boolean playerWon) {
        playerTurn = false;
        aiBoard.setClickHandler(null);
        if (playerWon) {
            aiBoard.refreshAll();
            turnLabel.setText("VICTORY");
            turnLabel.setTextFill(Color.web("#00cc66"));
            turnLabel.setStyle("-fx-background-color: #002a14; -fx-background-radius: 4; -fx-padding: 4 12;");
            flash("VICTORY! You sunk the entire enemy fleet!", "#00cc66");
        } else {
            turnLabel.setText("DEFEATED");
            turnLabel.setTextFill(Color.web("#e05050"));
            turnLabel.setStyle("-fx-background-color: #2a0a0a; -fx-background-radius: 4; -fx-padding: 4 12;");
            flash("GAME OVER — The AI destroyed your fleet!", "#e05050");
        }
    }

    public BorderPane getRoot() { return root; }
}
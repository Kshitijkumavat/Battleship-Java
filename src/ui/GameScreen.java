package ui;

import ai.HuntTargetStrategy;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Grid;
import model.Ship;
import player.AIPlayer;

/**
 * Main battle screen — two grids side by side, full screen friendly.
 */
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

    private boolean playerTurn = true;

    public GameScreen(Stage stage, Grid playerGrid) {
        this.stage = stage;
        this.playerGrid = playerGrid;

        this.aiPlayer = new AIPlayer("Admiral AI");
        this.aiStrategy = new HuntTargetStrategy();
        this.aiPlayer.placeShips();

        this.playerBoard = new GridBoard(playerGrid, false);
        this.aiBoard = new GridBoard(aiPlayer.getMyGrid(), true);

        this.statusLabel = new Label("🎯 Your turn! Click on the enemy grid to fire.");
        this.playerHitsLabel = new Label("Your hits: 0");
        this.aiHitsLabel = new Label("AI hits: 0");

        root = new BorderPane();
        root.setStyle("-fx-background-color: #060f1e;");

        buildUI();
        setupAttackHandler();
    }

    private void buildUI() {
        // ── Title ─────────────────────────────────────────────────────────────
        Text title = new Text("⚓  BATTLESHIP");
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-fill: #5bc0de;");
        VBox titleBox = new VBox(title);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(20, 0, 10, 0));
        root.setTop(titleBox);

        // ── Labels ────────────────────────────────────────────────────────────
        Label yourLabel = new Label("YOUR FLEET");
        yourLabel.setStyle("-fx-text-fill: #5bc0de; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label enemyLabel = new Label("ENEMY WATERS");
        enemyLabel.setStyle("-fx-text-fill: #e05050; -fx-font-size: 14px; -fx-font-weight: bold;");

        VBox leftBox = new VBox(10, yourLabel, playerBoard);
        leftBox.setAlignment(Pos.CENTER);

        VBox rightBox = new VBox(10, enemyLabel, aiBoard);
        rightBox.setAlignment(Pos.CENTER);

        // Vertical divider
        Region sep = new Region();
        sep.setPrefWidth(3);
        sep.setMinHeight(500);
        sep.setStyle("-fx-background-color: #1e3a5f;");

        HBox boards = new HBox(60, leftBox, sep, rightBox);
        boards.setAlignment(Pos.CENTER);
        boards.setPadding(new Insets(10, 60, 10, 60));
        HBox.setHgrow(leftBox, Priority.ALWAYS);
        HBox.setHgrow(rightBox, Priority.ALWAYS);
        leftBox.setAlignment(Pos.CENTER);
        rightBox.setAlignment(Pos.CENTER);

        root.setCenter(boards);

        // ── Bottom ────────────────────────────────────────────────────────────
        statusLabel.setStyle("-fx-text-fill: #a0c8e0; -fx-font-size: 15px;");
        playerHitsLabel.setStyle("-fx-text-fill: #00cc66; -fx-font-size: 13px; -fx-font-weight: bold;");
        aiHitsLabel.setStyle("-fx-text-fill: #e05050; -fx-font-size: 13px; -fx-font-weight: bold;");

        HBox stats = new HBox(40, playerHitsLabel, aiHitsLabel);
        stats.setAlignment(Pos.CENTER);

        VBox bottom = new VBox(8, statusLabel, stats);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(12, 0, 24, 0));
        root.setBottom(bottom);
    }

    private void setupAttackHandler() {
        aiBoard.setClickHandler((row, col) -> {
            if (!playerTurn) return;
            if (aiPlayer.getMyGrid().isAlreadyAttacked(row, col)) {
                statusLabel.setText("⚠️ Already attacked that cell! Try another.");
                return;
            }

            boolean hit = aiPlayer.getMyGrid().receiveAttack(row, col);
            aiBoard.refreshAll();
            updateStats();

            String coord = (char)('A' + row) + "" + (col + 1);
            if (hit) {
                statusLabel.setText("💥 HIT at " + coord + "!");
                checkSunk(aiPlayer.getMyGrid(), row, col);
            } else {
                statusLabel.setText("🌊 Miss at " + coord + ". AI is thinking...");
            }

            if (aiPlayer.getMyGrid().allShipsSunk()) {
                endGame(true);
                return;
            }

            playerTurn = false;
            PauseTransition pause = new PauseTransition(Duration.millis(900));
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
            statusLabel.setText("🤖 AI hit YOUR ship at " + coord + "! 💥 Your turn.");
        } else {
            statusLabel.setText("🤖 AI missed at " + coord + ". Your turn!");
        }

        if (playerGrid.allShipsSunk()) {
            endGame(false);
            return;
        }
        playerTurn = true;
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
            .ifPresent(s -> statusLabel.setText("🚢 " + s.getName() + " SUNK! Keep firing!"));
    }

    private void updateStats() {
        long playerHits = countHits(aiPlayer.getMyGrid());
        long aiHits = countHits(playerGrid);
        playerHitsLabel.setText("🎯 Your hits: " + playerHits);
        aiHitsLabel.setText("🤖 AI hits: " + aiHits);
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
            statusLabel.setStyle("-fx-text-fill: #00cc66; -fx-font-size: 18px; -fx-font-weight: bold;");
            statusLabel.setText("🏆 YOU WIN! All enemy ships have been sunk!");
        } else {
            statusLabel.setStyle("-fx-text-fill: #e05050; -fx-font-size: 18px; -fx-font-weight: bold;");
            statusLabel.setText("💀 GAME OVER! The AI sunk your entire fleet!");
        }
    }

    public BorderPane getRoot() { return root; }
}
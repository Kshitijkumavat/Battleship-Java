package ui;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import model.Cell;
import model.Grid;

/**
 * Renders a 10x10 game grid with row/column labels.
 */
public class GridBoard extends GridPane {

    private final Rectangle[][] rects;
    private final Grid grid;
    private final boolean hideShips;

    // Callback when a cell is clicked (for attack phase)
    private CellClickHandler clickHandler;

    public interface CellClickHandler {
        void onCellClicked(int row, int col);
    }

    public GridBoard(Grid grid, boolean hideShips) {
        this.grid = grid;
        this.hideShips = hideShips;
        this.rects = new Rectangle[Grid.SIZE][Grid.SIZE];
        this.setHgap(2);
        this.setVgap(2);
        buildGrid();
    }

    private void buildGrid() {
        int cs = BattleshipApp.CELL_SIZE;

        // Column headers (1–10)
        for (int c = 0; c < Grid.SIZE; c++) {
            Text label = new Text(String.valueOf(c + 1));
            label.setFill(Color.web("#a0c8e0"));
            label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
            StackPane sp = new StackPane(label);
            sp.setPrefSize(cs, 22);
            add(sp, c + 1, 0);
        }

        // Row headers (A–J) + cells
        for (int r = 0; r < Grid.SIZE; r++) {
            Text rowLabel = new Text(String.valueOf((char)('A' + r)));
            rowLabel.setFill(Color.web("#a0c8e0"));
            rowLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
            StackPane sp = new StackPane(rowLabel);
            sp.setPrefSize(22, cs);
            add(sp, 0, r + 1);

            for (int c = 0; c < Grid.SIZE; c++) {
                Rectangle rect = new Rectangle(cs, cs);
                rect.setArcWidth(4);
                rect.setArcHeight(4);
                rect.setFill(Color.web("#0a1628"));
                rect.setStroke(Color.web("#1e3a5f"));
                rect.setStrokeWidth(1);
                rects[r][c] = rect;

                final int row = r, col = c;
                rect.setOnMouseEntered(e -> {
                    if (clickHandler != null && !grid.isAlreadyAttacked(row, col))
                        rect.setFill(Color.web("#1a4a7a"));
                });
                rect.setOnMouseExited(e -> refresh(row, col));
                rect.setOnMouseClicked(e -> {
                    if (clickHandler != null) clickHandler.onCellClicked(row, col);
                });

                add(rect, c + 1, r + 1);
            }
        }
    }

    /** Refresh a single cell's color based on its state */
    private void refresh(int r, int c) {
        Cell cell = grid.getCell(r, c);
        Rectangle rect = rects[r][c];
        switch (cell.getState()) {
            case HIT   -> rect.setFill(Color.web("#cc2200"));
            case MISS  -> rect.setFill(Color.web("#1a3a5a"));
            case SHIP  -> rect.setFill(hideShips ? Color.web("#0a1628") : Color.web("#2a6496"));
            default    -> rect.setFill(Color.web("#0a1628"));
        }
    }

    /** Refresh the entire board */
    public void refreshAll() {
        for (int r = 0; r < Grid.SIZE; r++)
            for (int c = 0; c < Grid.SIZE; c++)
                refresh(r, c);
    }

    /** Highlight cells for ship preview during drag */
    public void highlightCells(int row, int col, int size, boolean horizontal, boolean valid) {
        refreshAll();
        Color color = valid ? Color.web("#00cc66", 0.6) : Color.web("#cc2200", 0.6);
        for (int i = 0; i < size; i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            if (r >= 0 && r < Grid.SIZE && c >= 0 && c < Grid.SIZE)
                rects[r][c].setFill(color);
        }
    }

    public void setClickHandler(CellClickHandler handler) {
        this.clickHandler = handler;
    }

    public Rectangle getRect(int r, int c) { return rects[r][c]; }
}
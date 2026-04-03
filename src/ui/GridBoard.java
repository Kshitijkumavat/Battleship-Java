package ui;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import model.Cell;
import model.Grid;

public class GridBoard extends GridPane {

    private final Rectangle[][] rects;
    private final Circle[][] markers;
    private final Grid grid;
    private final boolean hideShips;
    private CellClickHandler clickHandler;

    public interface CellClickHandler {
        void onCellClicked(int row, int col);
    }

    public GridBoard(Grid grid, boolean hideShips) {
        this.grid = grid;
        this.hideShips = hideShips;
        this.rects = new Rectangle[Grid.SIZE][Grid.SIZE];
        this.markers = new Circle[Grid.SIZE][Grid.SIZE];
        setHgap(3);
        setVgap(3);
        buildGrid();
    }

    private void buildGrid() {
        int cs = BattleshipApp.CELL_SIZE;

        for (int c = 0; c < Grid.SIZE; c++) {
            Text label = new Text(String.valueOf(c + 1));
            label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            label.setFill(Color.web("#4a8fa8"));
            StackPane sp = new StackPane(label);
            sp.setPrefSize(cs, 20);
            add(sp, c + 1, 0);
        }

        for (int r = 0; r < Grid.SIZE; r++) {
            Text rowLabel = new Text(String.valueOf((char)('A' + r)));
            rowLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            rowLabel.setFill(Color.web("#4a8fa8"));
            StackPane sp = new StackPane(rowLabel);
            sp.setPrefSize(20, cs);
            add(sp, 0, r + 1);

            for (int c = 0; c < Grid.SIZE; c++) {
                Rectangle rect = new Rectangle(cs, cs);
                rect.setArcWidth(6);
                rect.setArcHeight(6);
                rect.setFill(Color.web("#0d1f38"));
                rect.setStroke(Color.web("#1a3a5c"));
                rect.setStrokeWidth(1);
                rects[r][c] = rect;

                Circle marker = new Circle(cs * 0.22);
                marker.setVisible(false);
                markers[r][c] = marker;

                StackPane cell = new StackPane(rect, marker);
                cell.setPrefSize(cs, cs);

                final int row = r, col = c;
                cell.setOnMouseEntered(e -> onHover(row, col, true));
                cell.setOnMouseExited(e -> onHover(row, col, false));
                cell.setOnMouseClicked(e -> { if (clickHandler != null) clickHandler.onCellClicked(row, col); });

                add(cell, c + 1, r + 1);
            }
        }
    }

    private void onHover(int r, int c, boolean entering) {
        if (clickHandler == null) return;
        if (grid.isAlreadyAttacked(r, c)) return;
        rects[r][c].setFill(entering ? Color.web("#1e4a7a") : Color.web("#0d1f38"));
        rects[r][c].setStroke(entering ? Color.web("#5bc0de") : Color.web("#1a3a5c"));
    }

    public void refresh(int r, int c) {
        Cell cell = grid.getCell(r, c);
        Rectangle rect = rects[r][c];
        Circle marker = markers[r][c];

        switch (cell.getState()) {
            case HIT -> {
                rect.setFill(Color.web("#3a0a0a"));
                rect.setStroke(Color.web("#cc3300"));
                marker.setFill(Color.web("#ff4422"));
                marker.setVisible(true);
            }
            case MISS -> {
                rect.setFill(Color.web("#0a1a2e"));
                rect.setStroke(Color.web("#1a3a5c"));
                marker.setFill(Color.web("#2a5a8a"));
                marker.setVisible(true);
            }
            case SHIP -> {
                if (hideShips) {
                    rect.setFill(Color.web("#0d1f38"));
                    rect.setStroke(Color.web("#1a3a5c"));
                } else {
                    rect.setFill(Color.web("#0d3a5a"));
                    rect.setStroke(Color.web("#2a7ab0"));
                }
                marker.setVisible(false);
            }
            default -> {
                rect.setFill(Color.web("#0d1f38"));
                rect.setStroke(Color.web("#1a3a5c"));
                marker.setVisible(false);
            }
        }
    }

    public void refreshAll() {
        for (int r = 0; r < Grid.SIZE; r++)
            for (int c = 0; c < Grid.SIZE; c++)
                refresh(r, c);
    }

    public void highlightCells(int row, int col, int size, boolean horizontal, boolean valid) {
        refreshAll();
        Color fill  = valid ? Color.web("#003322") : Color.web("#330a00");
        Color stroke = valid ? Color.web("#00cc66") : Color.web("#cc3300");
        for (int i = 0; i < size; i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            if (r >= 0 && r < Grid.SIZE && c >= 0 && c < Grid.SIZE) {
                rects[r][c].setFill(fill);
                rects[r][c].setStroke(stroke);
            }
        }
    }

    public void setClickHandler(CellClickHandler handler) { this.clickHandler = handler; }
    public Rectangle getRect(int r, int c) { return rects[r][c]; }
}
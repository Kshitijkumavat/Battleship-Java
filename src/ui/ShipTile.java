package ui;

import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import model.Ship;

/**
 * A draggable visual tile representing a ship in the placement panel.
 */
public class ShipTile extends StackPane {

    private final Ship ship;
    private boolean horizontal = true;

    private double dragOffsetX, dragOffsetY;

    public ShipTile(Ship ship) {
        this.ship = ship;
        int cs = BattleshipApp.CELL_SIZE;
        int w = ship.getSize() * cs - 4;
        int h = cs - 4;

        Rectangle body = new Rectangle(w, h);
        body.setArcWidth(8);
        body.setArcHeight(8);
        body.setFill(Color.web("#2a6496"));
        body.setStroke(Color.web("#5bc0de"));
        body.setStrokeWidth(2);

        Text label = new Text(ship.getName() + " (" + ship.getSize() + ")");
        label.setFill(Color.WHITE);
        label.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");

        getChildren().addAll(body, label);
        setCursor(Cursor.HAND);
        setPrefSize(w, h);
    }

    public Ship getShip() { return ship; }
    public boolean isHorizontal() { return horizontal; }
    public void setHorizontal(boolean h) { this.horizontal = h; }
    public void toggleOrientation() { horizontal = !horizontal; rebuild(); }

    private void rebuild() {
        int cs = BattleshipApp.CELL_SIZE;
        int w = horizontal ? ship.getSize() * cs - 4 : cs - 4;
        int h = horizontal ? cs - 4 : ship.getSize() * cs - 4;

        Rectangle body = (Rectangle) getChildren().get(0);
        body.setWidth(w);
        body.setHeight(h);
        setPrefSize(w, h);
    }

    public void setDragOffset(double x, double y) {
        this.dragOffsetX = x;
        this.dragOffsetY = y;
    }

    public double getDragOffsetX() { return dragOffsetX; }
    public double getDragOffsetY() { return dragOffsetY; }
}
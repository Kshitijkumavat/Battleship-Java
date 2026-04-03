package ui;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import model.Ship;

public class ShipTile extends StackPane {

    private final Ship ship;
    private boolean horizontal = true;
    private double dragOffsetX, dragOffsetY;

    public ShipTile(Ship ship) {
        this.ship = ship;
        setCursor(Cursor.HAND);
        build();
    }

    private void build() {
        getChildren().clear();
        int cs = BattleshipApp.CELL_SIZE;
        int gap = 3;
        int w = horizontal ? ship.getSize() * (cs + gap) - gap : cs;
        int h = horizontal ? cs : ship.getSize() * (cs + gap) - gap;

        Rectangle body = new Rectangle(w, h);
        body.setArcWidth(8);
        body.setArcHeight(8);
        body.setFill(Color.web("#0d3a5a"));
        body.setStroke(Color.web("#5bc0de"));
        body.setStrokeWidth(1.5);

        Text label = new Text(ship.getName());
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        label.setFill(Color.web("#a0d8ef"));

        Text sizeLabel = new Text("[" + ship.getSize() + "]");
        sizeLabel.setFont(Font.font("Segoe UI", 10));
        sizeLabel.setFill(Color.web("#5bc0de"));

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(1, label, sizeLabel);
        content.setAlignment(Pos.CENTER);

        getChildren().addAll(body, content);
        setPrefSize(w, h);
        setMinSize(w, h);
        setMaxSize(w, h);
        setAlignment(Pos.CENTER);

        setOnMouseEntered(e -> body.setFill(Color.web("#1a5a8a")));
        setOnMouseExited(e -> body.setFill(Color.web("#0d3a5a")));
    }

    public void toggleOrientation() { horizontal = !horizontal; build(); }
    public boolean isHorizontal() { return horizontal; }
    public Ship getShip() { return ship; }
    public void setDragOffset(double x, double y) { dragOffsetX = x; dragOffsetY = y; }
    public double getDragOffsetX() { return dragOffsetX; }
    public double getDragOffsetY() { return dragOffsetY; }
}
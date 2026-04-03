package ui;

import javafx.application.Application;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BattleshipApp extends Application {

    public static final int CELL_SIZE = 44;

    @Override
    public void start(Stage primaryStage) {
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        PlacementScreen placement = new PlacementScreen(primaryStage);
        Scene scene = new Scene(placement.getRoot(), screen.getWidth(), screen.getHeight());
        primaryStage.setTitle("⚓ Battleship — Naval Edition");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
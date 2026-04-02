package ui;

import javafx.application.Application;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX entry point for the Battleship game.
 */
public class BattleshipApp extends Application {

    public static final int CELL_SIZE = 42;

    @Override
    public void start(Stage primaryStage) {
        // Get actual screen size and use it
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        PlacementScreen placement = new PlacementScreen(primaryStage);
        Scene scene = new Scene(placement.getRoot(), screenBounds.getWidth(), screenBounds.getHeight());
        scene.getRoot().setStyle("-fx-background-color: #060f1e; -fx-font-family: 'Segoe UI';");

        primaryStage.setTitle("⚓ Battleship — Naval Edition");
        primaryStage.setScene(scene);
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setMaximized(true);   // start fullscreen
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
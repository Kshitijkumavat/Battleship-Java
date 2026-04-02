import game.GameEngine;
import player.AIPlayer;
import player.HumanPlayer;
import java.util.Scanner;

/**
 * Entry point for the Battleship game.
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = "Player";

        HumanPlayer human = new HumanPlayer(name, scanner);
        AIPlayer ai = new AIPlayer("Admiral AI");

        GameEngine engine = new GameEngine(human, ai);
        engine.start();

        scanner.close();
    }
}
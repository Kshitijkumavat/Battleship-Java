package player;

import model.Grid;
import model.Ship;
import java.util.Scanner;

/**
 * Human-controlled player.
 * Handles console input for ship placement and attacks.
 */
public class HumanPlayer extends Player {

    private final Scanner scanner;

    public HumanPlayer(String name, Scanner scanner) {
        super(name);
        this.scanner = scanner;
    }

    @Override
    public void placeShips() {
        System.out.println("\n=== " + getName() + ", place your ships ===");

        for (Ship ship : getFleet()) {
            boolean placed = false;
            while (!placed) {
                displayOwnGrid();
                System.out.println("\nPlacing: " + ship.getName() + " (size " + ship.getSize() + ")");
                System.out.print("Enter row (A-J): ");
                char rowChar = scanner.next().toUpperCase().charAt(0);
                System.out.print("Enter col (1-10): ");
                int col = scanner.nextInt() - 1;
                System.out.print("Orientation (H/V): ");
                char oriChar = scanner.next().toUpperCase().charAt(0);

                int row = rowChar - 'A';
                Ship.Orientation orientation = (oriChar == 'H')
                        ? Ship.Orientation.HORIZONTAL
                        : Ship.Orientation.VERTICAL;

                if (row < 0 || row >= Grid.SIZE || col < 0 || col >= Grid.SIZE) {
                    System.out.println("❌ Invalid position. Try again.");
                    continue;
                }

                placed = getMyGrid().placeShip(ship, row, col, orientation);
                if (!placed) System.out.println("❌ Cannot place there. Try again.");
                else System.out.println("✅ " + ship.getName() + " placed!");
            }
        }
        displayOwnGrid();
    }

    @Override
    public int[] chooseAttack() {
        while (true) {
            System.out.print("\nEnter attack row (A-J): ");
            char rowChar = scanner.next().toUpperCase().charAt(0);
            System.out.print("Enter attack col (1-10): ");
            int col = scanner.nextInt() - 1;

            int row = rowChar - 'A';

            if (row < 0 || row >= Grid.SIZE || col < 0 || col >= Grid.SIZE) {
                System.out.println("❌ Out of bounds. Try again.");
                continue;
            }
            // Note: duplicate-attack check is in GameEngine
            return new int[]{ row, col };
        }
    }

    private void displayOwnGrid() {
        System.out.println("\nYour board:");
        getMyGrid().display(false);
    }
}
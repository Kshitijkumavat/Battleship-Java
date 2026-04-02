package player;

import ai.HuntTargetStrategy;
import model.Grid;
import model.Ship;
import java.util.Random;

/**
 * AI-controlled player using the Hunt & Target strategy.
 * Demonstrates the Strategy design pattern in action.
 */
public class AIPlayer extends Player {

    private final HuntTargetStrategy strategy;
    private final Random random = new Random();

    public AIPlayer(String name) {
        super(name);
        this.strategy = new HuntTargetStrategy();
    }

    @Override
    public void placeShips() {
        System.out.println("\n🤖 " + getName() + " is placing ships...");
        for (Ship ship : getFleet()) {
            boolean placed = false;
            while (!placed) {
                int row = random.nextInt(Grid.SIZE);
                int col = random.nextInt(Grid.SIZE);
                Ship.Orientation orientation = random.nextBoolean()
                        ? Ship.Orientation.HORIZONTAL
                        : Ship.Orientation.VERTICAL;
                placed = getMyGrid().placeShip(ship, row, col, orientation);
            }
        }
        System.out.println("🤖 " + getName() + " is ready!");
    }

    @Override
    public int[] chooseAttack() {
        return strategy.chooseAttack(getMyGrid()); // uses opponent's grid passed at attack time
    }

    /**
     * Chooses attack against the opponent's actual grid (for AI logic).
     */
    public int[] chooseAttack(Grid opponentGrid) {
        return strategy.chooseAttack(opponentGrid);
    }

    @Override
    public void processAttackResult(int row, int col, boolean hit) {
        // Tell the strategy what happened so it can switch modes
        // We pass null here; the strategy only needs the hit result for queue logic
        strategy.registerResult(row, col, hit, null);
    }

    /**
     * Full version with opponent grid reference.
     */
    public void processAttackResult(int row, int col, boolean hit, Grid opponentGrid) {
        strategy.registerResult(row, col, hit, opponentGrid);
    }
}
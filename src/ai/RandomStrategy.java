package ai;

import model.Grid;
import java.util.Random;

/**
 * Simple random attack strategy.
 * Picks a random cell that hasn't been attacked yet.
 */
public class RandomStrategy implements AttackStrategy {

    private final Random random = new Random();

    @Override
    public int[] chooseAttack(Grid opponentGrid) {
        int row, col;
        do {
            row = random.nextInt(Grid.SIZE);
            col = random.nextInt(Grid.SIZE);
        } while (opponentGrid.isAlreadyAttacked(row, col));
        return new int[]{ row, col };
    }
}
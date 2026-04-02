package ai;

import model.Grid;
import java.util.*;

/**
 * Hunt & Target strategy — the smart AI.
 *
 * HUNT mode  : attacks randomly until a ship is hit
 * TARGET mode: once a hit is found, attacks adjacent cells to sink the ship
 *
 * Demonstrates advanced use of the Strategy pattern.
 */
public class HuntTargetStrategy implements AttackStrategy {

    private enum Mode { HUNT, TARGET }

    private Mode mode = Mode.HUNT;
    private final Queue<int[]> targetQueue = new LinkedList<>();
    private final Random random = new Random();

    // Directions: up, down, left, right
    private static final int[][] DIRECTIONS = { {-1,0}, {1,0}, {0,-1}, {0,1} };

    @Override
    public int[] chooseAttack(Grid opponentGrid) {
        if (mode == Mode.TARGET && !targetQueue.isEmpty()) {
            return getNextTarget(opponentGrid);
        }

        // Switch back to HUNT if target queue is empty
        mode = Mode.HUNT;
        return huntAttack(opponentGrid);
    }

    /**
     * Called after an attack to update internal state.
     */
    public void registerResult(int row, int col, boolean wasHit, Grid opponentGrid) {
        if (wasHit) {
            mode = Mode.TARGET;
            addAdjacentTargets(row, col, opponentGrid);
        }
    }

    private int[] huntAttack(Grid opponentGrid) {
        int row, col;
        do {
            row = random.nextInt(Grid.SIZE);
            col = random.nextInt(Grid.SIZE);
        } while (opponentGrid.isAlreadyAttacked(row, col));
        return new int[]{ row, col };
    }

    private int[] getNextTarget(Grid opponentGrid) {
        while (!targetQueue.isEmpty()) {
            int[] next = targetQueue.poll();
            int r = next[0], c = next[1];
            if (r >= 0 && r < Grid.SIZE && c >= 0 && c < Grid.SIZE
                    && !opponentGrid.isAlreadyAttacked(r, c)) {
                return new int[]{ r, c };
            }
        }
        // Queue exhausted — go back to hunting
        mode = Mode.HUNT;
        return huntAttack(opponentGrid);
    }

    private void addAdjacentTargets(int row, int col, Grid opponentGrid) {
        for (int[] dir : DIRECTIONS) {
            int r = row + dir[0];
            int c = col + dir[1];
            if (r >= 0 && r < Grid.SIZE && c >= 0 && c < Grid.SIZE
                    && !opponentGrid.isAlreadyAttacked(r, c)) {
                targetQueue.add(new int[]{ r, c });
            }
        }
    }
}
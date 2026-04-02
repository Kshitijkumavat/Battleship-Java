package ai;

import model.Grid;

/**
 * Strategy interface for AI attack behavior.
 * Demonstrates the Strategy design pattern.
 */
public interface AttackStrategy {

    /**
     * Decides which cell to attack next on the opponent's grid.
     * Returns an int[] { row, col }.
     */
    int[] chooseAttack(Grid opponentGrid);
}
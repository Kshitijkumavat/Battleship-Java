package player;

import model.Grid;
import model.Ship;
import model.ShipFactory;
import java.util.List;

/**
 * Abstract base class for all players.
 * Each player owns a grid and a fleet of ships.
 * Demonstrates abstraction and polymorphism.
 */
public abstract class Player {

    private final String name;
    private final Grid myGrid;       // own board (ships placed here)
    private final Grid attackGrid;   // tracking board (attacks made here)
    private final List<Ship> fleet;

    public Player(String name) {
        this.name = name;
        this.myGrid = new Grid();
        this.attackGrid = new Grid();
        this.fleet = ShipFactory.createFleet();
    }

    public String getName()       { return name; }
    public Grid getMyGrid()       { return myGrid; }
    public Grid getAttackGrid()   { return attackGrid; }
    public List<Ship> getFleet()  { return fleet; }

    /**
     * Each subclass defines how ships are placed (manually or automatically).
     */
    public abstract void placeShips();

    /**
     * Each subclass defines how an attack is chosen.
     * Returns int[] { row, col }.
     */
    public abstract int[] chooseAttack();

    /**
     * Processes the result of an attack this player made.
     */
    public void processAttackResult(int row, int col, boolean hit) {
        // Subclasses can override to update AI state etc.
    }

    public boolean hasLost() {
        return myGrid.allShipsSunk();
    }
}
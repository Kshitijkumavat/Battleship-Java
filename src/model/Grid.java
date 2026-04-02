package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a 10x10 Battleship grid.
 * Encapsulates all board logic: ship placement, attacks, display.
 */
public class Grid {

    public static final int SIZE = 10;
    private final Cell[][] cells;
    private final List<Ship> ships;

    public Grid() {
        cells = new Cell[SIZE][SIZE];
        ships = new ArrayList<>();
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                cells[r][c] = new Cell(r, c);
    }

    // ── Ship Placement ───────────────────────────────────────────────────────

    /**
     * Places a ship on the grid. Returns true if successful.
     */
    public boolean placeShip(Ship ship, int row, int col, Ship.Orientation orientation) {
        if (!canPlace(ship, row, col, orientation)) return false;

        ship.place(row, col, orientation);
        for (int i = 0; i < ship.getSize(); i++) {
            int r = row + (orientation == Ship.Orientation.VERTICAL ? i : 0);
            int c = col + (orientation == Ship.Orientation.HORIZONTAL ? i : 0);
            cells[r][c].setState(Cell.State.SHIP);
        }
        ships.add(ship);
        return true;
    }

    /**
     * Checks if a ship can be legally placed at the given position.
     */
    public boolean canPlace(Ship ship, int row, int col, Ship.Orientation orientation) {
        for (int i = 0; i < ship.getSize(); i++) {
            int r = row + (orientation == Ship.Orientation.VERTICAL ? i : 0);
            int c = col + (orientation == Ship.Orientation.HORIZONTAL ? i : 0);
            if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) return false;
            if (!cells[r][c].isEmpty()) return false;
        }
        return true;
    }

    // ── Attack Logic ─────────────────────────────────────────────────────────

    /**
     * Processes an attack on the given cell.
     * Returns true if it was a hit.
     */
    public boolean receiveAttack(int row, int col) {
        Cell cell = cells[row][col];
        if (cell.hasShip()) {
            cell.setState(Cell.State.HIT);
            registerHitOnShip(row, col);
            return true;
        } else {
            cell.setState(Cell.State.MISS);
            return false;
        }
    }

    /**
     * Checks if the cell has already been attacked.
     */
    public boolean isAlreadyAttacked(int row, int col) {
        Cell.State s = cells[row][col].getState();
        return s == Cell.State.HIT || s == Cell.State.MISS;
    }

    /**
     * Finds and registers a hit on the ship occupying the given cell.
     */
    private void registerHitOnShip(int row, int col) {
        for (Ship ship : ships) {
            if (!ship.isPlaced()) continue;
            for (int i = 0; i < ship.getSize(); i++) {
                int r = ship.getStartRow() + (ship.getOrientation() == Ship.Orientation.VERTICAL ? i : 0);
                int c = ship.getStartCol() + (ship.getOrientation() == Ship.Orientation.HORIZONTAL ? i : 0);
                if (r == row && c == col) {
                    ship.registerHit();
                    return;
                }
            }
        }
    }

    // ── Status ───────────────────────────────────────────────────────────────

    /**
     * Returns true if all ships have been sunk.
     */
    public boolean allShipsSunk() {
        return ships.stream().allMatch(Ship::isSunk);
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public List<Ship> getShips() {
        return ships;
    }

    // ── Display ──────────────────────────────────────────────────────────────

    /**
     * Prints the grid. If hideShips is true, ships are shown as dots (opponent view).
     */
    public void display(boolean hideShips) {
        System.out.print("   ");
        for (int c = 0; c < SIZE; c++) System.out.printf("%2d", c + 1);
        System.out.println();

        for (int r = 0; r < SIZE; r++) {
            System.out.printf(" %c ", 'A' + r);
            for (int c = 0; c < SIZE; c++) {
                Cell cell = cells[r][c];
                if (hideShips && cell.hasShip()) System.out.print(" .");
                else System.out.print(" " + cell);
            }
            System.out.println();
        }
    }
}
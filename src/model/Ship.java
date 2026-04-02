package model;

/**
 * Abstract base class for all ships.
 * Demonstrates abstraction and inheritance.
 */
public abstract class Ship {

    private final String name;
    private final int size;
    private int hits;
    private boolean placed;

    // Orientation of the ship on the grid
    public enum Orientation { HORIZONTAL, VERTICAL }
    private Orientation orientation;

    // Top-left starting position
    private int startRow;
    private int startCol;

    public Ship(String name, int size) {
        this.name = name;
        this.size = size;
        this.hits = 0;
        this.placed = false;
    }

    public String getName()        { return name; }
    public int getSize()           { return size; }
    public int getHits()           { return hits; }
    public boolean isPlaced()      { return placed; }
    public Orientation getOrientation() { return orientation; }
    public int getStartRow()       { return startRow; }
    public int getStartCol()       { return startCol; }

    public void place(int row, int col, Orientation orientation) {
        this.startRow = row;
        this.startCol = col;
        this.orientation = orientation;
        this.placed = true;
    }

    public void registerHit() {
        hits++;
    }

    public boolean isSunk() {
        return hits >= size;
    }

    @Override
    public String toString() {
        return name + " (size=" + size + ", hits=" + hits + ", sunk=" + isSunk() + ")";
    }
}
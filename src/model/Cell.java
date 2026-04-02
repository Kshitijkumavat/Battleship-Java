package model;

/**
 * Represents a single cell on the Battleship grid.
 * Encapsulates the state of each position.
 */
public class Cell {

    public enum State {
        EMPTY, SHIP, HIT, MISS
    }

    private final int row;
    private final int col;
    private State state;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.state = State.EMPTY;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public State getState() { return state; }

    public void setState(State state) { this.state = state; }

    public boolean isHit()   { return state == State.HIT; }
    public boolean isMiss()  { return state == State.MISS; }
    public boolean hasShip() { return state == State.SHIP; }
    public boolean isEmpty() { return state == State.EMPTY; }

    @Override
    public String toString() {
        return switch (state) {
            case EMPTY -> ".";
            case SHIP  -> "S";
            case HIT   -> "X";
            case MISS  -> "O";
        };
    }
}
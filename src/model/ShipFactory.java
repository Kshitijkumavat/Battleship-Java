package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class to create a standard fleet of ships.
 * Demonstrates the Factory design pattern.
 */
public class ShipFactory {

    public static List<Ship> createFleet() {
        List<Ship> fleet = new ArrayList<>();
        fleet.add(new Carrier());
        fleet.add(new Battleship());
        fleet.add(new Submarine());
        fleet.add(new Destroyer());
        return fleet;
    }
}
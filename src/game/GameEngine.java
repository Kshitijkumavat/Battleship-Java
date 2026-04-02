package game;

import player.AIPlayer;
import player.HumanPlayer;
import player.Player;

/**
 * Controls the main game loop and turn management.
 * Ties together all components of the game.
 */
public class GameEngine {

    private final HumanPlayer human;
    private final AIPlayer ai;
    private GameState state;

    public GameEngine(HumanPlayer human, AIPlayer ai) {
        this.human = human;
        this.ai = ai;
        this.state = GameState.ONGOING;
    }

    public void start() {
        printBanner();

        // Phase 1: Ship Placement
        human.placeShips();
        ai.placeShips();

        // Phase 2: Game Loop
        System.out.println("\n⚓ Battle begins!\n");

        while (state == GameState.ONGOING) {
            humanTurn();
            if (state != GameState.ONGOING) break;
            aiTurn();
        }

        printResult();
    }

    // ── Human Turn ───────────────────────────────────────────────────────────

    private void humanTurn() {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🎯 Your turn, " + human.getName() + "!");
        printBoards();

        int[] attack;
        while (true) {
            attack = human.chooseAttack();
            if (!ai.getMyGrid().isAlreadyAttacked(attack[0], attack[1])) break;
            System.out.println("❌ Already attacked that cell! Try again.");
        }

        boolean hit = ai.getMyGrid().receiveAttack(attack[0], attack[1]);
        String coord = (char)('A' + attack[0]) + "" + (attack[1] + 1);

        if (hit) {
            System.out.println("💥 HIT at " + coord + "!");
            checkSunk(ai, attack[0], attack[1]);
        } else {
            System.out.println("🌊 MISS at " + coord + ".");
        }

        if (ai.hasLost()) state = GameState.PLAYER_ONE_WINS;
    }

    // ── AI Turn ──────────────────────────────────────────────────────────────

    private void aiTurn() {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🤖 " + ai.getName() + "'s turn...");

        int[] attack = ai.chooseAttack(human.getMyGrid());
        boolean hit = human.getMyGrid().receiveAttack(attack[0], attack[1]);
        ai.processAttackResult(attack[0], attack[1], hit, human.getMyGrid());

        String coord = (char)('A' + attack[0]) + "" + (attack[1] + 1);

        if (hit) {
            System.out.println("🤖 AI hit at " + coord + "! 💥");
            checkSunk(human, attack[0], attack[1]);
        } else {
            System.out.println("🤖 AI missed at " + coord + ". 🌊");
        }

        if (human.hasLost()) state = GameState.PLAYER_TWO_WINS;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void checkSunk(Player defender, int row, int col) {
        defender.getFleet().stream()
                .filter(s -> s.isSunk() && s.isPlaced())
                .filter(s -> {
                    // Check if this ship was just sunk this turn
                    for (int i = 0; i < s.getSize(); i++) {
                        int r = s.getStartRow() + (s.getOrientation() == model.Ship.Orientation.VERTICAL ? i : 0);
                        int c = s.getStartCol() + (s.getOrientation() == model.Ship.Orientation.HORIZONTAL ? i : 0);
                        if (r == row && c == col) return true;
                    }
                    return false;
                })
                .findFirst()
                .ifPresent(s -> System.out.println("🚢 " + s.getName() + " has been SUNK!"));
    }

    private void printBoards() {
        System.out.println("\n📍 Your board:              🎯 Attack board:");
        String[] yourLines = gridLines(human.getMyGrid(), false);
        String[] attackLines = gridLines(ai.getMyGrid(), true);
        for (int i = 0; i < yourLines.length; i++) {
            System.out.printf("%-28s%s%n", yourLines[i], attackLines[i]);
        }
    }

    private String[] gridLines(model.Grid grid, boolean hideShips) {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream ps = new java.io.PrintStream(baos);
        java.io.PrintStream old = System.out;
        System.setOut(ps);
        grid.display(hideShips);
        System.out.flush();
        System.setOut(old);
        return baos.toString().split("\n");
    }

    private void printResult() {
        System.out.println("\n╔══════════════════════════════════╗");
        if (state == GameState.PLAYER_ONE_WINS) {
            System.out.println("║   🏆 " + human.getName() + " WINS! Congratulations!  ║");
        } else {
            System.out.println("║   🤖 AI WINS! Better luck next time! ║");
        }
        System.out.println("╚══════════════════════════════════╝");
    }

    private void printBanner() {
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║        🚢 BATTLESHIP 🚢          ║");
        System.out.println("║    Java OOP Edition              ║");
        System.out.println("╚══════════════════════════════════╝");
    }
}
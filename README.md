# ⚓ Battleship — Java OOP Mini Project

A fully playable **Battleship game** built in Java, demonstrating all four pillars of Object-Oriented Programming with a JavaFX naval-themed UI.

---

## 📸 Features

- 🎮 **10×10 grid** — classic Battleship rules
- 🖱️ **Drag & drop ship placement** with right-click to rotate
- 🤖 **Smart AI** using the Hunt & Target strategy
- 💥 **Hit / Miss / Sunk** visual feedback on the grid
- 📊 **Live stats** — hits and ships remaining for both sides
- 🔄 **Restart / Play Again** after the game ends
- 🎨 **Naval dark theme** built with JavaFX

---

## 🧱 OOP Concepts Demonstrated

| Concept | Where Used |
|---|---|
| **Abstraction** | `Ship` (abstract class), `Player` (abstract class), `AttackStrategy` (interface) |
| **Inheritance** | `Carrier`, `Battleship`, `Submarine`, `Destroyer` extend `Ship`; `HumanPlayer`, `AIPlayer` extend `Player` |
| **Polymorphism** | `player.chooseAttack()` behaves differently for `HumanPlayer` vs `AIPlayer` |
| **Encapsulation** | `Grid` hides internal cell state; `Ship` tracks its own hit count |
| **Strategy Pattern** | `HuntTargetStrategy` swaps AI behavior at runtime (Hunt → Target → Hunt) |
| **Factory Pattern** | `ShipFactory.createFleet()` creates the standard fleet |

---

## 📁 Project Structure

```
src/
├── Main.java                     ← Terminal entry point
├── model/
│   ├── Cell.java                 ← Single grid cell (state: EMPTY/SHIP/HIT/MISS)
│   ├── Ship.java                 ← Abstract base class for all ships
│   ├── Carrier.java              ← Size 5
│   ├── Battleship.java           ← Size 4
│   ├── Submarine.java            ← Size 3
│   ├── Destroyer.java            ← Size 2
│   ├── Grid.java                 ← 10×10 board logic
│   └── ShipFactory.java          ← Factory: creates a standard fleet
├── ai/
│   ├── AttackStrategy.java       ← Interface for AI strategies
│   ├── RandomStrategy.java       ← Picks random untried cells
│   └── HuntTargetStrategy.java   ← Smart AI: hunts after a hit
├── player/
│   ├── Player.java               ← Abstract base class
│   ├── HumanPlayer.java          ← Console input
│   └── AIPlayer.java             ← Uses AttackStrategy
├── game/
│   ├── GameEngine.java           ← Main game loop (terminal)
│   └── GameState.java            ← Enum: ONGOING / PLAYER_ONE_WINS / PLAYER_TWO_WINS
└── ui/
    ├── BattleshipApp.java        ← JavaFX entry point
    ├── PlacementScreen.java      ← Drag & drop ship placement
    ├── GameScreen.java           ← Battle screen with two grids
    ├── GridBoard.java            ← Reusable 10×10 grid component
    ├── ShipTile.java             ← Draggable ship tile widget
    └── style.css                 ← Naval dark theme stylesheet
```

---

## 🚀 How to Run

### Prerequisites
- Java 17 or later — [Download](https://adoptium.net/)
- JavaFX SDK 21+ — [Download](https://gluonhq.com/products/javafx/)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/Battleship-Java.git
   cd Battleship-Java
   ```

2. **Download JavaFX SDK** and place it at `C:\javafx-sdk-26\` (Windows)

3. **Compile** (from the `src/` folder)

   **Windows (PowerShell):**
   ```powershell
   $fx = "C:\javafx-sdk-26\lib"
   $files = Get-ChildItem -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName
   javac --module-path $fx --add-modules javafx.controls,javafx.fxml -d ..\out $files
   ```

4. **Run**

   **Windows:**
   ```powershell
   java --module-path $fx --add-modules javafx.controls,javafx.fxml -cp ..\out ui.BattleshipApp
   ```

### Terminal version (no JavaFX needed)
```powershell
# Windows
javac -d ..\out (Get-ChildItem -Recurse -Filter "*.java" | Where-Object { $_.FullName -notlike "*\ui\*" }).FullName
java -cp ..\out Main
```

---

## 🎮 How to Play

### Ship Placement
1. **Drag** ships from the sidebar onto your grid
2. **Right-click** a ship tile to rotate it (Horizontal ↔ Vertical)
3. Green highlight = valid position, Red = invalid
4. Click **RANDOM PLACEMENT** to auto-place all ships instantly
5. Click **LAUNCH ATTACK** when all ships are placed

### Battle
- Click any cell on the **ENEMY WATERS** grid to fire
- 🔴 Red circle = Hit
- 🔵 Blue circle = Miss
- AI responds automatically after ~1 second
- Game ends when all ships of one side are sunk

### After the Game
- **NEW GAME ↺** — go back to placement screen and start fresh
- **PLAY AGAIN ▶** — instant rematch with same ship positions

---

## 🤖 AI Strategy — Hunt & Target

The AI uses a two-mode strategy:

```
HUNT mode   → attacks random untried cells
     ↓ (on hit)
TARGET mode → attacks adjacent cells to sink the ship
     ↓ (ship sunk)
HUNT mode   → back to scanning
```

This is implemented via the **Strategy design pattern** — the AI can swap between `RandomStrategy` and `HuntTargetStrategy` at runtime.

---

## 🛠️ Design Patterns Used

| Pattern | Class | Purpose |
|---|---|---|
| **Strategy** | `AttackStrategy` interface | Swap AI behavior at runtime |
| **Factory** | `ShipFactory` | Create a standard fleet without exposing constructors |
| **Template Method** | `Player` abstract class | Define the algorithm skeleton, subclasses fill in details |

---

## 👨‍💻 Built With

- **Java 21**
- **JavaFX 26** — UI framework
- **OOP principles** — Abstraction, Inheritance, Polymorphism, Encapsulation
- **Design Patterns** — Strategy, Factory

---

## 📄 License

This project was built as an academic mini project for the **Object-Oriented Programming** subject.
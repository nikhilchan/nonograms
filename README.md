# Nonograms (Picross) Game

A grid-based logic puzzle game implemented in Java using the `javalib` framework. The game automatically generates a randomized puzzle board, calculates the correct row and column clues, and tracks your progress in real-time.

---

## 🎮 How to Play

* **Left Click:** Select a tile to guess its state. 
* **Incorrect Guesses:** If you guess wrong, a **Red X** will overlay the tile and your mistake count will increase.
* **Press 'H':** Feeling stuck? Press the `h` key on your keyboard to reveal a random unsolved tile as a hint (marked with a **Green H**).
* **Game Over:** Once the entire board is filled, the game pauses and your total mistake count will be displayed in the top-left corner.

---

## 🛠️ Features & Visuals

* **Dynamic Clue Generation:** Automatically processes the puzzle matrix to generate matching column and row clue counts.
* **Visual Legend:**
  * 🟦 **Blue Tile:** Active/Correctly filled tile.
  * ⬜ **Gray Tile:** Inactive/Correctly cleared tile.
  * ❌ **Red X:** A mistaken guess.
  * 🟩 **Green H:** A cell revealed by using a hint.

---

## 🚀 Getting Started

### Prerequisites
To run this project, you need to have the following library `.jar` files added to your Java build path:
* `javalib.jar` (for `impworld` graphics and `worldimages`)
* `tester.jar` (to initialize and launch the game window)

### Running the Game
The game is initialized via the `Examples` class. Simply run this project as a Java Application through your IDE, which will trigger the `launchGame()` method and open up a $6 \times 6$ Nonogram board.
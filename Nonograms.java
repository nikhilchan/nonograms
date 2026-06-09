import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import java.util.Arrays;
import java.util.Optional;
import javalib.worldimages.*;
import java.util.Random;
import java.util.ArrayList;

public class Nonograms extends World {
	BooleanPackage[][] mainBoard;
	ArrayList<ArrayList<Integer>> columnClues;
	ArrayList<ArrayList<Integer>> rowClues;
	
	int height;
	int width;
	int totalHeight;
	int totalWidth;
	int filled = 0;
	int mistakes = 0;
	
	static int TILE_SIZE = 30;
	Color BG_COLOR = Color.WHITE;
	WorldImage ACTIVE = new RectangleImage(TILE_SIZE, TILE_SIZE, OutlineMode.SOLID, Color.BLUE);
	WorldImage INACTIVE = new RectangleImage(TILE_SIZE, TILE_SIZE, OutlineMode.SOLID, Color.GRAY);
	WorldImage REDX = new TextImage("X", TILE_SIZE / 2, Color.RED);
	WorldImage BLANK = new RectangleImage(TILE_SIZE, TILE_SIZE, OutlineMode.OUTLINE, Color.DARK_GRAY);
	WorldImage GREENH = new TextImage("H", TILE_SIZE / 2, Color.GREEN);
	
	Nonograms(int height, int width) {
		this.height = height;
		this.width = width;
		
		if ((height < 1) || (width < 1)) {
			throw new IllegalArgumentException("Must pass positive dimensions for puzzle");
		}
		
		
		this.mainBoard = randomizeGrid(height, width);
		
		
		this.rowClues = new ArrayList<ArrayList<Integer>>();
		this.columnClues = new ArrayList<ArrayList<Integer>>();
		
		this.makeClues();
		
		this.totalHeight = this.height + this.columnClues.stream().mapToInt(column -> column.size()).max().orElse(0);
		this.totalWidth = this.width + this.rowClues.stream().mapToInt(row -> row.size()).max().orElse(0);
	}
	
	static BooleanPackage[][] randomizeGrid(int height, int width) {
		BooleanPackage[][] matrix = new BooleanPackage[height][width];
		Random rand = new Random(42);
		for (int i = 0; i < height; i += 1) {
			for (int j = 0; j < width; j += 1) {
				matrix[i][j] = new BooleanPackage(rand.nextBoolean());
			}
		}
		
		return matrix;
	}
	
	void makeClues() {
		for (int i = 0; i < this.height; i += 1) {
			BooleanPackage[] r = this.mainBoard[i];
			this.rowClues.add(this.makeCluesForLine(r));
		}
		
		for (int j = 0; j < this.width; j += 1) {
			BooleanPackage[] c = new BooleanPackage[this.height];
			
			for (int i = 0; i < this.height; i += 1) {
				c[i] = this.mainBoard[i][j];
			}
			
			this.columnClues.add(this.makeCluesForLine(c));
		}
	}
	
	ArrayList<Integer> makeCluesForLine(BooleanPackage[] arr) {
		int currentStreak = 0;
		ArrayList<Integer> currentClues = new ArrayList<Integer>();
		boolean last = false;
		
		for (int i = 0; i < arr.length; i += 1) {
			boolean currentElt = arr[i].abs;
			
			if ((currentElt == false) && (last == false)) {
				continue;
			} else if ((currentElt == false) && (last == true)) {
				currentClues.add(currentStreak);
				last = false;
				currentStreak = 0;
			} else {
				last = true;
				currentStreak += 1;
			}
		}
		
		if ((last == true) && (currentStreak > 0)) {
			currentClues.add(currentStreak);
		}
		
		if (currentClues.isEmpty()) {
			currentClues.add(0);
		}
		
		return currentClues;

	}
	
	public void onMouseClicked(Posn pixel, String buttonPressed) {
		int column = pixel.x / TILE_SIZE;
		int row = pixel.y / TILE_SIZE;
		
		int xOffset = this.totalWidth - this.width;
		int yOffset = this.totalHeight - this.height;
		
		if ((column < xOffset) || (row < yOffset)) {
			return;
		} else if (buttonPressed.equals("MiddleButton")) {			
			return;
		}
		
		int rowOnGrid = row - yOffset;
		int columnOnGrid = column - xOffset;
		
		BooleanPackage current = this.mainBoard[rowOnGrid][columnOnGrid];
		
		if (current.correct.isPresent()) {
			return;
		}
								
		current.correct = Optional.of(buttonPressed.equals("LeftButton") == current.abs);
		
		if (! current.correct.get()) {
			this.mistakes += 1;
		}
		this.filled += 1;
		
		
			
	}
	
	public void onKeyEvent(String ke) { //Extension 1
		if (ke.equals("h")) {
			this.revealRandom(new Random(), new Random());
		}
	}
	
	public WorldScene makeScene() {
		int xOffset = this.totalWidth - this.width;
		int yOffset = this.totalHeight - this.height;
		WorldScene background = new WorldScene(this.totalWidth * TILE_SIZE, this.totalHeight * TILE_SIZE);
		
		for (int i = 0; i < this.totalHeight; i += 1) {
			for (int j = 0; j < this.totalWidth; j += 1) {
				WorldImage currentTile;
				
				if ((i < yOffset) && (j < xOffset)) {
					currentTile = BLANK;
				} else if ((i < yOffset) && (j >= xOffset)) {
					currentTile = this.drawColumnClue(i, j, xOffset, yOffset);
				} else if ((i >= yOffset) && (j < xOffset))  {
					currentTile = this.drawRowClue(i, j, xOffset, yOffset);
				} else {
					currentTile = this.drawBoard(i, j, xOffset, yOffset);
				}
				
				background.placeImageXY(currentTile, (j * TILE_SIZE) + (TILE_SIZE / 2), (i * TILE_SIZE) + (TILE_SIZE / 2));
				
				
			}
		}
		
		if (this.filled == (this.width * this.height)) {
			background.placeImageXY(new TextImage(String.valueOf(this.mistakes), TILE_SIZE, Color.RED), TILE_SIZE / 2, TILE_SIZE / 2);
		}
		
		return background;
		
	}
	
	WorldImage drawColumnClue(int i, int j, int xOffset, int yOffset) {
		ArrayList<Integer> clues = this.columnClues.get(j - xOffset);
		int clueIndex = i - (yOffset - clues.size());
		
		if ((clueIndex >= 0) && (clueIndex < clues.size())) {
			return new OverlayImage(new TextImage(String.valueOf(clues.get(clueIndex)), TILE_SIZE, Color.BLACK), BLANK);
		} else {
			return BLANK;
		}
		
	}
	
	WorldImage drawRowClue(int i, int j, int xOffset, int yOffset) {
		ArrayList<Integer> clues = this.rowClues.get(i - yOffset);
		int clueIndex = j - (xOffset - clues.size());
		
		if ((clueIndex >= 0) && (clueIndex < clues.size())) {
			return new OverlayImage(new TextImage(String.valueOf(clues.get(clueIndex)), TILE_SIZE, Color.BLACK), BLANK);
		} else {
			return BLANK;
		}
		
	}
	
	WorldImage drawBoard(int i, int j, int xOffset, int yOffset) {
		int mainRow = i - yOffset;
		int mainColumn = j - xOffset;
		
		BooleanPackage current = this.mainBoard[mainRow][mainColumn];
		WorldImage currentTile;
		
		if (current.correct.isEmpty()) { 
			currentTile = BLANK;
		} else if (current.abs) {
			currentTile = ACTIVE;
		} else {
			currentTile = INACTIVE;
		}
		
		if (!current.correct.orElse(true)) {
			currentTile = new OverlayImage(REDX, currentTile);
		}
		
		if (current.hinted == true) {
			currentTile = new OverlayImage(GREENH, currentTile);
		}
		
		return currentTile;
	}
	
	public WorldEnd worldEnds() {
		if (this.filled == (this.width * this.height)) {
			return new WorldEnd(true, this.makeScene());
		} else {
			return new WorldEnd(false, this.makeScene());
		}
	} 

	
	void launchGame() {
		this.bigBang(this.totalWidth * TILE_SIZE, this.totalHeight * TILE_SIZE, 0.1);
	}
	
	void revealRandom(Random rr, Random rc) {
		BooleanPackage currentCell = this.mainBoard[rr.nextInt(this.width)][rc.nextInt(this.height)];
		
		if (currentCell.correct.isEmpty()) {
			currentCell.correct = Optional.of(true);
			currentCell.hinted = true;
			
			this.filled += 1;
		} else {
			this.revealRandom(rr, rc);
		}
	}
	

}


class BooleanPackage {
	boolean abs;
	Optional<Boolean> correct = Optional.empty();
	boolean hinted = false;
	
	BooleanPackage(boolean bool) {
		this.abs = bool;
	}
	
}

class Examples {
	Nonograms nono = new Nonograms(6, 6);
	
	void testGame(Tester t) {
		nono.launchGame();
	}
	
	
}

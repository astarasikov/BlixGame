package ru.hse.se.hci.blixgame.model;
import java.awt.Color;
import java.util.Random;


public class GameModel {
	final Color mTiles[][];
	final Color mColors[];
	final boolean mFromFile;
		
	final public int numColumns;
	final public int numRows;
	final public int numLevel;
	
	final Random mRNG;
	
	int mActiveRow = 0;
	int mMovesLeft = 0;
	int mCurrentScore = 0;
	
	LevelGenerator mLevelGenerator;
	
	public Color tileAt(int row, int column) {
		return mTiles[row][column];
	}
	
	public GameModel(boolean fromFile) {
		this(0, 0, fromFile);
	}
	
	public GameModel nextLevel() {
		return new GameModel(numLevel + 1, mCurrentScore, mFromFile);
	}
		
	public GameModel(int level, int score, boolean fromFile) {
		int columns = 10 + level;
		int rows = 3 + level;
		
		numLevel = level;
		numColumns = columns;
		numRows = rows;
		mFromFile = fromFile;

		mLevelGenerator = new LevelGenerator(fromFile, rows, columns);
		
		int numColors = (int)(Math.log(columns + rows) / Math.log(2.0));
		
		mTiles = new Color[rows][columns];
		mColors = new Color[numColors];
		
		mRNG = new Random(System.currentTimeMillis());
		
		mCurrentScore = score;
		mMovesLeft = 6 * (rows - 2);
		
		update();
	}
	
	void bumpScore() {
		mCurrentScore += 25 * (numRows + 1);
	}
	
	Color nextColor() {
		return mColors[mRNG.nextInt(mColors.length)];
	}
	
	public void update() {
		for (int i = 0; i < mColors.length; i++) {
			mColors[i] = new Color(mRNG.nextInt(0xffffff));
		}
		
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				mTiles[i][j] = nextColor();
			}
		}
	}
	
	void shiftLeft(int row) {
		for (int i = 0; i < numColumns; i++) {
			mTiles[row][i] = mTiles[row][(i + 1) % numColumns];
		}
		mTiles[row][numColumns - 1] = nextColor();
	}
	
	void shiftRight(int row) {
		for (int i = numColumns - 1; i >= 0; i--) {
			mTiles[row][i] = mTiles[row][(numColumns + i - 1) % numColumns];
		}
		mTiles[row][0] = nextColor();
	}
	
	void eliminateColumn(int column) {		
		for (int i = 0; i < numRows; i++) {
			for (int j = column; j < numColumns - 1; j++) {
				mTiles[i][j] = mTiles[i][j+1];
			}
		}
		for (int i = 0; i < numRows; i++) {
			mTiles[i][numColumns - 1] = nextColor();
		}
		checkState(false);
	}
	
	void checkState(boolean isBadMove) {		
		for (int i = 0; i < numColumns; i++) {
			boolean columnMatched = true;
			
			
			for (int j = 0; j < numRows; j++) {
				Color c = mTiles[j][i];
				Color next = mTiles[(j + 1) % numRows][i];
				
				if (!c.equals(next)) {
					columnMatched = false;
				}
			}
			
			if (columnMatched) {
				isBadMove = false;
				bumpScore();
				eliminateColumn(i);
				return;
			}			
		}
		
		if (isBadMove) {
			mMovesLeft--;
		}
	}
	
	public void shiftLeft() {
		shiftLeft(mActiveRow);
		checkState(true);
	}
	
	public void shiftRight() {
		shiftRight(mActiveRow);
		checkState(true);
	}
	
	public int activeRow() {
		return mActiveRow;
	}
	
	public void increaseRow() {
		mActiveRow = (mActiveRow + 1) % numRows;
	}
	
	public void decreaseRow() {
		mActiveRow = (numRows + mActiveRow - 1) % numRows;		
	}
	
	public int score() {
		return mCurrentScore;
	}
	
	public int movesLeft() {
		return mMovesLeft;
	}
}

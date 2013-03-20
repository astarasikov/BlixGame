package ru.hse.se.hci.blixgame.model;

import java.awt.Color;

public class LevelGenerator {
	
	
	public LevelGenerator(boolean fromFile, int rows, int cols) {
		int numNeededColumns = (int)Math.ceil(800.0 / (25 * (rows + 1)));
		int moves = 3 * (rows - 2);
	}
	
	public Color[][] tiles() {
		return null;
	}
	
	public Color tileAt(int row, int column) {
		return null;
	}
	
	public Color nextTileAt(int row, int column) {
		return null;
	}
}

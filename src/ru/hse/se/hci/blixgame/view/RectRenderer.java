package ru.hse.se.hci.blixgame.view;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import ru.hse.se.hci.blixgame.GameModel;

public class RectRenderer implements Renderer {
	boolean mFirstTime = true;
	
	@Override
	public void render(Graphics2D g, GameModel mdl) {
		g.setStroke(STROKE);
		Rectangle r = g.getClipBounds();
		
		if (mFirstTime) {
			g.fillRect(0, 0, r.width, r.height);
			mFirstTime = false;
		}
		
		if (mdl == null) {
			return;
		}
		
		int mRow = mdl.activeRow();
		
		int tw = r.width / mdl.numColumns;
		int th = r.height / mdl.numRows;
		
		int side = Math.min(tw, th);
				
		for (int row = 0; row < mdl.numRows; row++) {
			for (int col = 0; col < mdl.numColumns; col++) {			
				
				g.setColor(mdl.tileAt(row, col));
				g.fillRect(col * side, row * side, side, side);
				
			}
		}
		
		g.setColor(COLOR_GRID_ACTIVE);
		for (int col = 0; col < mdl.numColumns; col++) {
			g.drawRect(col * side, mRow * side, side, side);
		}
	}
}

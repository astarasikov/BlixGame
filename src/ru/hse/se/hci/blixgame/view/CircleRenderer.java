package ru.hse.se.hci.blixgame.view;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import ru.hse.se.hci.blixgame.model.GameModel;

public class CircleRenderer implements Renderer {
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
		int side = Math.min(r.width, r.height);
		
		int radius = side / mdl.numRows;
				
		for (int row = mdl.numRows - 1; row >= 0; row--) {
			int curSide = radius * (1 + row);
			int pos = (side - curSide) >> 1;			
			
			int angleInc = 360 / mdl.numColumns;
			int angle = 0;
			
			for (int col = 0; col < mdl.numColumns; col++) {
				g.setColor(mdl.tileAt(row, col));
				g.fillArc(pos, pos, curSide, curSide, angle, angleInc);
				angle = (angle + angleInc) % 360;
			}
			
			if (row == mRow) {
				g.setColor(COLOR_GRID_ACTIVE);
				g.drawOval(pos, pos, curSide, curSide);
			}
		}
	}
}

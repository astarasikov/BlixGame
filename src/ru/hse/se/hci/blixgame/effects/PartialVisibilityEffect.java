package ru.hse.se.hci.blixgame.effects;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import ru.hse.se.hci.blixgame.ui.GameDisplay;


public class PartialVisibilityEffect implements PostEffect {
	GameDisplay mDisplay;
	
	public PartialVisibilityEffect(GameDisplay display) {
		mDisplay = display;
		
		mDisplay.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent arg0) {
				mDisplay.paintGame();
			}
			
			@Override
			public void mouseDragged(MouseEvent arg0) {}
		});
	}
	
	@Override
	public void process(BufferedImage image) {
		Point p = mDisplay.getMousePosition();
				
		int w = image.getWidth();
		int h = image.getHeight();
		
		int delta = image.getWidth() / 10;
		
		for (int j = 0; j < w; j++) {
			for (int i = 0; i < h; i++) {
				if (p == null
						|| Math.abs(j - p.x) > delta
						|| Math.abs(i - p.y) > delta) {
					image.setRGB(j, i, 0xffffff);	
				}				
			}
		}
	}

	@Override
	public void cleanup() {}
}

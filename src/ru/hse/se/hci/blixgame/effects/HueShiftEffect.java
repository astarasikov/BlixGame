package ru.hse.se.hci.blixgame.effects;

import java.awt.image.BufferedImage;

public class HueShiftEffect implements PostEffect {
	int mXORmask;
	Thread mThread;
	
	public HueShiftEffect() {
		
		mThread = new Thread() {
			public void run() {
				boolean direction = false;
				final int shift = 0x1000;
				
				while (!isInterrupted()) {
					try {
						int nextMask = mXORmask;
						
						if (direction) {
							nextMask -= shift;
						}
						else {
							nextMask += shift;
						}
						
						if (nextMask > 0xffffff) {
							nextMask = 0xffffff;
							direction = true;
						}
						else if (nextMask < 0) {
							nextMask = 0;
							direction = false;
						}
						mXORmask = nextMask;

						sleep(500);
					} catch (InterruptedException e) {
						break;
					}
				}
			};
		};
		mThread.start();
	}
	
	@Override
	public void process(BufferedImage image) {
		int mask = mXORmask;
		
		int w = image.getWidth();
		int h = image.getHeight();
		
		for (int j = 0; j < w; j++) {
			for (int i = 0; i < h; i++) {
				int r = image.getRGB(j, i);
				r ^= mask;
				image.setRGB(j, i, r);
			}
		}
	}

	@Override
	public void cleanup() {
		mThread.interrupt();
	}
}

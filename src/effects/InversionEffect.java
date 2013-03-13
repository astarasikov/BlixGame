package effects;

import java.awt.image.BufferedImage;

public class InversionEffect implements PostEffect {
	int mMask = 0xffffff;
	
	@Override
	public void process(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		
		for (int j = 0; j < w; j++) {
			for (int i = 0; i < h; i++) {
				int r = image.getRGB(j, i);
				r ^= mMask;
				image.setRGB(j, i, r);
			}
		}
		mMask ^= 0xffffff;
	}

	@Override
	public void cleanup() {}
}

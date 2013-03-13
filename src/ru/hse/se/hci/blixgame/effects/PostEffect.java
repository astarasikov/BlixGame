package ru.hse.se.hci.blixgame.effects;

import java.awt.image.BufferedImage;

public interface PostEffect {
	public void cleanup();
	public void process(BufferedImage image);
}

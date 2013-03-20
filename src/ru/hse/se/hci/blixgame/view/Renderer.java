package ru.hse.se.hci.blixgame.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import ru.hse.se.hci.blixgame.model.GameModel;


public interface Renderer {
	final Color COLOR_GRID_ACTIVE = Color.BLACK;
	final Stroke STROKE = new BasicStroke(3.0f, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_BEVEL, 0.0f, new float[] { 10.0f, 5.0f }, 0.0f);
	
	public void render(Graphics2D g, GameModel mdl);
}

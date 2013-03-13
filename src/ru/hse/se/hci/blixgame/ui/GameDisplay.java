package ru.hse.se.hci.blixgame.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JPanel;

import ru.hse.se.hci.blixgame.GameModel;
import ru.hse.se.hci.blixgame.effects.PostEffect;
import ru.hse.se.hci.blixgame.view.Renderer;

@SuppressWarnings({ "serial" })
public class GameDisplay extends JPanel {
	MainFrame mMainFrame;
	
	Renderer mRenderer;
	GameModel mGameModel;
	PostEffect mEffect;
	
	BufferedImage mGameImage;
	Graphics2D mGameGraphics;
	
	BufferedImage mBackImage;
	Graphics2D mBackGraphics;
	
	JPanel mDisplayPanel = new JPanel() {
		@Override
		public void paint(Graphics g) {
			g.drawImage(mBackImage, 0, 0, null);
		}
	};
	
	ActionListener actionUp = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			GameModel mdl = mGameModel;
			if (mdl != null) {
				mdl.decreaseRow();
			}
			checkGameState();
		}
	};
	
	ActionListener actionDown = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			GameModel mdl = mGameModel;
			if (mdl != null) {
				mdl.increaseRow();
			}
			checkGameState();
		}
	};
	
	ActionListener actionLeft = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			GameModel mdl = mGameModel;
			if (mdl != null) {
				mdl.shiftLeft();
			}
			checkGameState();
		}
	};
	
	ActionListener actionRight = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			GameModel mdl = mGameModel;
			if (mdl != null) {
				mdl.shiftRight();
			}
			checkGameState();
		}
	};
	
	JPanel mControlPanel = new JPanel() {
		{
			setLayout(new GridLayout(4, 1));
			add(new JButton("Up") {
				{
					addActionListener(actionUp);
				}
			});
			add(new JButton("Down") {
				{
					addActionListener(actionDown);
				}
			});
			add(new JButton("Left") {
				{
					addActionListener(actionLeft);
				}
			});
			add(new JButton("Right") {
				{
					addActionListener(actionRight);
				}
			});
		}
	};
	
	KeyListener mKeyListener = new KeyListener() {
		
		@Override
		public void keyTyped(KeyEvent e) {/* NOP */}
		
		@Override
		public void keyReleased(KeyEvent e) {/* NOP */}
		
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_K:
				actionUp.actionPerformed(null);
				break;
				
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_J:
				actionDown.actionPerformed(null);
				break;
				
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_H:
				actionLeft.actionPerformed(null);
				break;
				
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_L:
				actionRight.actionPerformed(null);
				break;
			}
		}
	};
	
	public GameDisplay(MainFrame mainFrame) {
		mMainFrame = mainFrame;
		addKeyListener(mKeyListener);
		setLayout(new BorderLayout());
		add(mDisplayPanel, BorderLayout.CENTER);
		add(mControlPanel, BorderLayout.EAST);
		setFocusable(true);
	}
	
	public void setRenderer(Renderer renderer) {
		synchronized (this) {
			mRenderer = renderer;			
		}
		checkGameState();
	}
	
	public void setGameModel(GameModel gameModel) {
		synchronized (this) {
			mGameModel = gameModel;			
		}
		checkGameState();
	}
	
	public void setEffect(PostEffect effect) {
		synchronized (this) {
			mEffect = effect;
		}
		checkGameState();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		checkGameState();
	}
	
	void checkGameState() {
		paintGame();
		mMainFrame.gameStateChanged();
	}
	
	boolean checkBuffers() {
		synchronized(this) {
			int w = mDisplayPanel.getWidth();
			int h = mDisplayPanel.getHeight();
			
			if (w <= 0 || h <= 0) {
				return false;
			}
			
			if (mGameImage != null 
					&& mGameImage.getWidth() == w
					&& mGameImage.getHeight() == h) {
				return true;
			}
			
			mGameImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			mGameGraphics = mGameImage.createGraphics();
			mGameGraphics.setClip(0, 0, w, h);
			
			mBackImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			mBackGraphics = mBackImage.createGraphics();
			mBackGraphics.setClip(0, 0, w, h);
		}
		
		return true;
	}
	
	public void paintGame() {
		if (!checkBuffers()) {
			return;
		}
		
		if (mRenderer != null) {
			mRenderer.render(mGameGraphics, mGameModel);	
		}
				
		if (mEffect != null) {
			mEffect.process(mGameImage);
		}
	
		mBackGraphics.drawImage(mGameImage, 0, 0, null);		
		mDisplayPanel.repaint();
	}	
}

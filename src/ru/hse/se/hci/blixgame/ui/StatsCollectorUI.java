package ru.hse.se.hci.blixgame.ui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import ru.hse.se.hci.blixgame.effects.HueShiftEffect;
import ru.hse.se.hci.blixgame.effects.PartialVisibilityEffect;
import ru.hse.se.hci.blixgame.effects.PostEffect;
import ru.hse.se.hci.blixgame.model.GameModel;
import ru.hse.se.hci.blixgame.view.CircleRenderer;
import ru.hse.se.hci.blixgame.view.RectRenderer;
import ru.hse.se.hci.blixgame.view.Renderer;

public class StatsCollectorUI
	extends JFrame
	implements GameStateCallback
{
	final static long TIMEOUT = (1000 * 60 * 2);
	final static int MAX_LEVEL = 3;
	final static int MAX_UI = 5;
	
	String mUserName;
	GameDisplay mGameDisplay = new GameDisplay(this);
	GameModel mGameModel;
	Renderer mRenderer = new RectRenderer();
	PostEffect mEffect;
	JLabel mScoreLabel = new JLabel();
	Timer mTimer = null;
	
	int mLastScore = 0;
	int mAttemptCount = 0;
	int mUiNum = 0;
	long mTimeStart = 0;
	
	void setUI(int num) {
		switch (num) {
		case 0:
			mRenderer = new RectRenderer();
			mEffect = null;
			break;
		case 1:
			mRenderer = new CircleRenderer();
			mEffect = null;
			break;
		case 2:
			mRenderer = new RectRenderer();
			mEffect = new HueShiftEffect();
			break;
		case 3:
			mRenderer = new CircleRenderer();
			mEffect = new HueShiftEffect();
			break;
		case 4:
			mRenderer = new RectRenderer();
			mEffect = new PartialVisibilityEffect(mGameDisplay);
			break;
		case 5:
			mRenderer = new CircleRenderer();
			mEffect = new PartialVisibilityEffect(mGameDisplay);
			break;
		}
	}
	
	void rearmTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
		}
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(timerTask(), TIMEOUT, TIMEOUT);
	}
	
	boolean hasWon() {
		return mGameModel.score() - mLastScore > 800;
	}
	
	boolean hasLost() {
		return mGameModel.movesLeft() <= 0;
	}
	
	void updateGameDisplay() {
		mGameDisplay.setGameModel(mGameModel);
		mGameDisplay.setRenderer(mRenderer);
		mGameDisplay.setEffect(mEffect);
	}
	
	@Override
	public void gameStateChanged() {
		boolean won = hasWon();
		boolean lost = hasLost();
		
		if (hasWon()) {
			upgradeLevel();
		}
		if (hasLost()) {
			retryLevel(); 
		}
		
		if (won || lost) {
			updateGameDisplay();
			rearmTimer();
		}
		
		mScoreLabel.setText(
				String.format("score %d | moves %d | level %d | attempt %d",
				mGameModel.score(),
				mGameModel.movesLeft(),
				mGameModel.numLevel,
				mAttemptCount));
	}
	
	void dumpStats() {
		long mTimeNow = System.currentTimeMillis();
		long dt = mTimeNow - mTimeStart;
		
		FileOutputStream fout = null;
		try {
			String fname = "stats_out.csv";
			boolean skipHeader = new File(fname).length() != 0;
			
			fout = new FileOutputStream(fname, true);
			PrintWriter p = new PrintWriter(fout);
			if (!skipHeader) {
				p.println("Name,UI,Score,Level,Time,Success,AttemptN");
			}
			p.printf("%s,%d,%d,%d,%d,%d,%d\n",
					mUserName,
					1,
					mGameModel.score(),
					mGameModel.numRows,
					dt,
					hasWon() ? 1 : 0,
					mAttemptCount);
			
			p.flush();
			p.close();
			
			fout.flush();
			fout.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void upgradeLevel() {
		if (mGameModel.numLevel == MAX_LEVEL && mUiNum == MAX_UI) {
			mTimer.cancel();
			mTimer.purge();
			System.exit(0);
		}
		else if (mGameModel.numLevel == MAX_LEVEL) {
			mUiNum++;
			setUI(mUiNum);
			mAttemptCount = 0;
			mLastScore = 0;
			mGameModel = new GameModel(true);
			mTimeStart = System.currentTimeMillis();
		}
		else {
			mAttemptCount = 0;
			mLastScore = mGameModel.score();
			mGameModel = mGameModel.nextLevel();
			mTimeStart = System.currentTimeMillis();
		}
	}
	
	void retryLevel() {
		JOptionPane.showMessageDialog(null, "You lost, retrying");
		mAttemptCount++;
		mGameModel = new GameModel(mGameModel.numLevel, mLastScore, true); 
		mTimeStart = System.currentTimeMillis();
	}
	
	TimerTask timerTask() {
		return new TimerTask() {
			@Override
			public void run() {
				dumpStats();
				
				mTimeStart = System.currentTimeMillis();
				if (hasWon()) {
					upgradeLevel();
				}
				else {
					retryLevel();
				}
				updateGameDisplay();
			}
		};
	}
	
	void getUserName() {
		mUserName = JOptionPane.showInputDialog("Enter User Name");
	}
	
	public StatsCollectorUI() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getUserName();
		mGameModel = new GameModel(true);
		updateGameDisplay();
				
		add(mScoreLabel, BorderLayout.NORTH);
		add(mGameDisplay, BorderLayout.CENTER);
		mTimeStart = System.currentTimeMillis();
		gameStateChanged();
		
		rearmTimer();
		setSize(500, 500);
	}
}

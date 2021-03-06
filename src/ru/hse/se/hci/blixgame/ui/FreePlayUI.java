package ru.hse.se.hci.blixgame.ui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.DefaultFocusTraversalPolicy;
import java.awt.FocusTraversalPolicy;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListDataListener;

import ru.hse.se.hci.blixgame.effects.HueShiftEffect;
import ru.hse.se.hci.blixgame.effects.PartialVisibilityEffect;
import ru.hse.se.hci.blixgame.effects.PostEffect;
import ru.hse.se.hci.blixgame.model.GameModel;
import ru.hse.se.hci.blixgame.view.CircleRenderer;
import ru.hse.se.hci.blixgame.view.RectRenderer;
import ru.hse.se.hci.blixgame.view.Renderer;

@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class FreePlayUI
	extends JFrame
	implements GameStateCallback
{
	/*
	public static void main(String args[]) {
		new FreePlayUI().setVisible(true);
	}
	*/
	
	JLabel mScoreLabel = new JLabel();
	
	String mUserName = "Unknown User";
	GameDisplay mGameDisplay = new GameDisplay(this);
	Renderer mRenderer = new RectRenderer();	
	PostEffect mEffect;
	GameModel mGameModel;
	
	long mStartTime;
	int mScore;
	int mInterfaceIndex = 0;
	
	ComboBoxModel mInterfaceModel = new ComboBoxModel() {		
		class Handler {
			String mDescription;
			void handle() {}
			
			public String toString() {
				return mDescription;
			};
		}
		
		Handler mInterfaces[] = new Handler[] {
			new Handler() {
				{
					mDescription = "Rectangular";
				}
				
				void handle() {
					mRenderer = new RectRenderer();
					mEffect = null;
				}
			},
			new Handler() {
				{
					mDescription = "Circular";
				}
				
				void handle() {
					mRenderer = new CircleRenderer();
					mEffect = null;
				}
			},
			new Handler() {
				{
					mDescription = "Rectangular Hue Shift";
				}
				
				void handle() {
					mRenderer = new RectRenderer();
					mEffect = new HueShiftEffect();
				}
			},
			new Handler() {
				{
					mDescription = "Circular Hue Shift";
				}
				
				void handle() {
					mRenderer = new CircleRenderer();
					mEffect = new HueShiftEffect();
				}
			},
			new Handler() {
				{
					mDescription = "Rectangular Partial";
				}
				
				void handle() {
					mRenderer = new RectRenderer();
					mEffect = new PartialVisibilityEffect(mGameDisplay);
				}
			},
			new Handler() {
				{
					mDescription = "Circular Partial";
				}
				
				void handle() {
					mRenderer = new CircleRenderer();
					mEffect = new PartialVisibilityEffect(mGameDisplay);
				}
			},
		};
		
		Object mSelection = mInterfaces[0];

		@Override
		public void addListDataListener(ListDataListener arg0) {}

		@Override
		public Object getElementAt(int arg0) {
			return mInterfaces[arg0];
		}

		@Override
		public int getSize() {
			return mInterfaces.length;
		}

		@Override
		public void removeListDataListener(ListDataListener arg0) {}

		@Override
		public Object getSelectedItem() {
			return mSelection;
		}

		@Override
		public void setSelectedItem(Object anItem) {
			mSelection = anItem;
			index();
			cleanInterface();			
			((Handler)anItem).handle();
			newGame();
		}
		
		void index() {
			for (int i = 0; i < mInterfaces.length; i++) {
				if (mInterfaces[i] == mSelection) {
					mInterfaceIndex = i;
				}
			}
		}
	};
	
	ActionListener actionQuit = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	};
	
	ActionListener actionNewGame = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			newGame();
		}
	};
	
	void dumpStats() {
		if (mGameModel != null) {
			mScore = mGameModel.score();
		}
		if (mScore <= 0) {
			return;
		}
		
		long mTimeNow = System.currentTimeMillis();
		long dt = mTimeNow - mStartTime;
		
		FileOutputStream fout = null;
		try {
			String fname = "stats.csv";
			boolean skipHeader = new File(fname).length() != 0;
			
			fout = new FileOutputStream(fname, true);
			PrintWriter p = new PrintWriter(fout);
			if (!skipHeader) {
				p.println("Name,UI,Score,Level,Time");
			}
			p.printf("%s,%d,%d,%d,%d\n",
					mUserName,
					mInterfaceIndex,
					mScore,
					mGameModel.numRows,
					dt);
			
			p.flush();
			p.close();
			
			fout.flush();
			fout.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void cleanInterface() {
		if (mEffect != null) {
			mEffect.cleanup();
		}
		cleanup();
	}
	
	void cleanup() {		
		dumpStats();		
		mScore = 0;
	}
	
	void newGame() {
		cleanup();
		mGameModel = new GameModel(false);
		updateGameDisplay();
		
		mStartTime = System.currentTimeMillis();
	}
	
	@Override
	public void gameStateChanged() {
		if (mGameModel.movesLeft() <= 0) {
			JOptionPane.showMessageDialog(this, "You lost");
			newGame();
		}
		else {
			int newScore = mGameModel.score();
			
			mScoreLabel.setText(String.format("score %d | moves %d | level %d",
					newScore,
					mGameModel.movesLeft(),
					mGameModel.numLevel));

			//upgrade level with each 800 points
			if (newScore - mScore > 800) {
				mScore = newScore;

				mGameModel = mGameModel.nextLevel();
				updateGameDisplay();
			}
		}
	}
	
	void updateGameDisplay() {
		mGameDisplay.setGameModel(mGameModel);
		mGameDisplay.setRenderer(mRenderer);
		mGameDisplay.setEffect(mEffect);
	}
	
	void buildControls() {
		JPanel controlPane = new JPanel() {
			{
				add(new JComboBox(mInterfaceModel));
				
				add(new JTextField(mUserName) {
					void update() {
						String text = getText();
						
						if (text.compareTo(mUserName) != 0) {
							mUserName = text;
							newGame();
						}
					}
					
					{
						addKeyListener(new KeyListener() {
							
							@Override
							public void keyTyped(KeyEvent arg0) {}
							
							@Override
							public void keyReleased(KeyEvent arg0) {}
							
							@Override
							public void keyPressed(KeyEvent arg0) {
								if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
									update();
								}
							}
						});
						
						addFocusListener(new FocusListener() {
							
							@Override
							public void focusLost(FocusEvent arg0) {
								update();
							}
							
							@Override
							public void focusGained(FocusEvent arg0) {}
						});
					}
				});
				
				add(mScoreLabel);
				
				add(new JButton("New Game") {
					{
						newGame();
					}
				});
				
				add(new JButton("Quit") {
					{
						dumpStats();
						addActionListener(actionQuit);	
					}
				});
				
			}
		};
		controlPane.setLayout(new GridLayout(1, 10));
		
		add(controlPane, BorderLayout.NORTH);		
	}
	
	FocusTraversalPolicy traversalPolicy = new 
		DefaultFocusTraversalPolicy()
	{
		@Override
		public Component getDefaultComponent(Container aContainer) {
			return mGameDisplay;
		}
	};
		
	public FreePlayUI() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		buildControls();
		
		newGame();		
		add(mGameDisplay, BorderLayout.CENTER);
		
		setSize(500, 500);
		setFocusTraversalPolicy(traversalPolicy);
	}
}

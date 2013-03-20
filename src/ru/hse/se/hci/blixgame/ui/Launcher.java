package ru.hse.se.hci.blixgame.ui;

import javax.swing.JOptionPane;

public class Launcher {
	static void newGame() {
		int idx = JOptionPane.showOptionDialog(
				null,
				"Launch stats collector or free play?",
				"Game Mode",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE,
				null,
				new String[]{"Stats Collector", "Free Play", "Exit"},
				"Free Play");
		if (idx == 0) {
			new StatsCollectorUI().setVisible(true);
		}
		else if (idx == 1) {
			new FreePlayUI().setVisible(true);
		}
		else {
			System.exit(0);
		}
	}
	
	public static void main(String args[]) {
		newGame();
	}
}

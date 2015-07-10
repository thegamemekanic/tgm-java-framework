package com.thegamemekanic.framework;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * It's the window...for the game...clue's in the name.
 * 
 * @author The Game Mechanic
 */
@SuppressWarnings("serial")
public final class GameWindow extends JFrame {
	
	//=================================================================================================================
	// Member Variables
	
	private final Game _game;
	private JPanel _exitConfirmationPanel;
	
	
	
	//=================================================================================================================
	// Constructor
	
	/**
	 * Create an instance of the game window.
	 * @param game - Reference to the game so it can inform the game class of window events.
	 */
	GameWindow(Game game) {
		_game = game;
		_exitConfirmationPanel =  null;

		this.getContentPane().add(game.getGameCanvas());
		this.setSize(800, 600);							// Default Window Size
		this.setLocationRelativeTo(null);				// Center Screen
		this.setTitle(_game.Name);						// Default Title
		
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
		this.pack();
		
		this.addWindowListener(new WindowListener() {

			@Override // WindowListener
			public void windowActivated(WindowEvent e) {
				_game.activate();
			}
			
			@Override // WindowListener
			public void windowDeactivated(WindowEvent e) {
				_game.decativate();
			}
			
			@Override // WindowListener
			public void windowClosing(WindowEvent e) {
				
				if(JOptionPane.showConfirmDialog(null, getExitConfirmationPanel(), "Exit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					_game.stop();
					System.exit(0);
				}
				
			}

			@Override // WindowListener
			public void windowDeiconified(WindowEvent e) {
				_game.activate();
			}

			@Override // WindowListener
			public void windowIconified(WindowEvent e) {
				_game.decativate();
			}

			@Override // WindowListener
			public void windowOpened(WindowEvent e) { }
			
			@Override // WindowListener
			public void windowClosed(WindowEvent e) { }
			
		});
	}
	
	
	
	//=================================================================================================================
	// Private Helper Methods
	
	/**
	 * Uses the developer customized JPanel if they provided one, otherwise the default version is returned.
	 * @return The exit confirmation panel displayed to the user when the game window is closing.
	 */
	private final JPanel getExitConfirmationPanel() {
		JPanel panel = _exitConfirmationPanel;
		
		if(panel == null) {
	        panel = new JPanel();
	        JLabel label = new JLabel("Are you sure you want to exit?");
	        
	        panel.add(label);
		}

        return panel;
    }
	
	
	
	//=================================================================================================================
	// Settings
	
	/**
	 * Customize the exit dialog box that appears when a user closes the game window.
	 * @param panel - Panel to display in place of the default one.
	 */
	public final void setExitConfirmationPanel(JPanel panel) {
		_exitConfirmationPanel = panel;
	}
	
	
	
} // END GameWindow
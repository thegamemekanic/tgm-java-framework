package com.thegamemekanic.framework.test;

import java.applet.Applet;

import com.thegamemekanic.framework.Game;

@SuppressWarnings("serial")
public class Program extends Applet {
	
	//private static Game _game;
	
//	//=============================================================================================
//	// Run game as Applet
//	
//	@Override // Applet
//	public final void init() {
//		_game = new TestGame();
//		
//		setLayout(new BorderLayout());
//		add(new GameScreen(256, 244), BorderLayout.CENTER);
//		setSize(256, 244);
//		
//		_game.start();
//	}
//	
//	@Override // Applet
//	public final void start() {
//		// TODO: Up to the game design how to handle this.
//		// This method is called directly after init and any time the applet should resume
//		// execution, such as regaining focus.
//	}
//	
//	@Override // Applet
//	public final void stop() {
//		// TODO: Up to the game design how to handle this.
//		// Called just before destroy when the applet is closing and any time the applet should
//		// stop executing, but be able to start back up, such as window losing focus.
//	}
//	
//	@Override // Applet
//	public final void destroy() {
//		_game.stop();
//
//	}
	
	
	//=============================================================================================
	// Run game as Application

	public static void main(String[] args) {
		Game game = new TestGame();
		game.start();
	}
}

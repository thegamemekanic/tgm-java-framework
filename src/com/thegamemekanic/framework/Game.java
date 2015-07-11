package com.thegamemekanic.framework;


/**
 * 
 * 
 * @author The Game Mechanic
 */
public abstract class Game {

	//=================================================================================================================
	// Member Variables

	public final String Name;
	private final double NSPerUpdate;
	
	private Thread _gameThread;		// Main thread that the game is running on.
	private boolean _isRunning;		// The game is considered running as long as the game loop is being executed.
	private double _nsPerDraw;		// Number of time between draws to limit how many draws happen, 0 removes this limit.
	
	
	
	//=================================================================================================================
	// Constructor
	
	/**
	 * Create an instance of a game with a default update rate of 60 updates per second.
	 * @param name - Name of the game, used as the default window title and thread name.
	 */
	public Game(String name) {
		this(name, 60);
		
	}
	
	/**
	 * Create an instance of a game.
	 * @param name - Name of the game, used as the default window title and thread name.
	 * @param updateRate - Number of times updates should be called per second. This value is final and cannot be changed
	 * during the runtime of a game.
	 */
	public Game(String name, int updateRate) {
		Name = name;
		NSPerUpdate = updateRate > 0 ? 1000000000.0 / (double) updateRate : 1000000000.0 / 60.0;
		
		_gameThread = new Thread(new GameRunner(), Name);
		_isRunning = false;
		_nsPerDraw = 0.0;
	}
	
	
	
	//=================================================================================================================
	// Public Interface

	/**
	 * Begins execution of the game thread which runs the game loop.
	 */
	public final synchronized void start() {
		if(!_isRunning && _gameThread != null) {
			_isRunning = true;
			
			_gameThread.start();
		}
	}
	
	/**
	 * Stops the game thread, terminating the game loop. The thread must be reinitialized in order to start the game
	 * loop again.
	 */
	public final synchronized void stop() {
		
		OnExiting();
		
		if(_isRunning && _gameThread != null) {
			_isRunning = false;
			
			try {
				_gameThread.join();
				_gameThread = null;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	
	
	//=================================================================================================================
	// Customizable methods
	
	/**
	 * Called once before the game loop starts, initialize everything that is needed to get the
	 * game running here.
	 */
	protected abstract void initialize();
	
	/**
	 * This is where all game logic should be updated, process user input, updating game state,  update physics, etc.
	 */
	protected abstract void update();
	
	/**
	 * Called as frequently as possible unless limited by changing the game settings to <code>setMaxFramerate()</code>.
	 * @param delta - Difference in time from when the update should have been called, and when it
	 * actually was, guaranteed to be between 0 and 1.
	 */
	protected abstract void render(float delta);

	/**
	 * Called as the game is being closed.
	 */
	protected void OnExiting() { }

	/**
	 * Called when the game gains focus.
	 */
	protected void onActivated() { }
	
	/**
	 * Called when the game loses focus.
	 */
	protected void onDeactivated() { }
	
	/**
	 * Called when the game is dropping frames in order to update.
	 */
	protected void onRunningSlowly() { }

	
	
	//=================================================================================================================
	// Settings
	
	/**
	 * @param maxFramerate - Limit the frame rate to prevent it from drawing as fast as possible, 0 or negative values
	 * will remove the limit.
	 */
	public final void setMaxFramerate(int maxFramerate) {
		_nsPerDraw = maxFramerate > 0 ? 1000000000D / (double)maxFramerate : 0.0F;
	}
	
	
	
	//=================================================================================================================
	// Package Access Methods
	
	/**
	 * I hate this method, but it's the only way (I can think of currently) to call the onActivate method from the game
	 * window without people extending the game class to be able to call it.
	 */
	final void activate() { onActivated(); }
	
	/**
	 * I hate this method, but it's the only way (I can think of currently) to call the onDeactivate method from the game
	 * window without people extending the game class to be able to call it.
	 */
	final void decativate() { onDeactivated(); }
	
	
	
	//=================================================================================================================
	// Private Classes
	
	/**
	 * Handles running the game loop. It's placed in a private class to prevent exposing the run method, making it
	 * stupid proof.
	 * 
	 * @author The Game Mechanic
	 */
	private class GameRunner implements Runnable {

		/**
		 * The heartbeat of the game
		 */
		@Override 	// Runnable
		public final void run() {

			long timeLast = System.nanoTime();
			long timeNow = System.nanoTime();
			long timeElapsed = 0L;
			double delta = 0.0;
			double drawDelta = 0.0;
			
			int updates = 0;
			int draws = 0;

			initialize();

			// The game loop!
			while (_isRunning) {
				timeNow = System.nanoTime();
				
				delta += (timeNow - timeLast) / NSPerUpdate;
				drawDelta += _nsPerDraw > 0 ? (timeNow - timeLast) / _nsPerDraw : 0;
				
				timeElapsed += timeNow - timeLast;
				timeLast = timeNow;
				
				// If the delta value ever exceeds 2, then it will be dropping frames to compensate, let the developer
				// handle what should happen when the game is falling behind.
				if(delta >= 2.0F) {
					onRunningSlowly();	//TODO: TGM - Should I pass the delta value in so they know how bad it is?
				}

				// Will update to meet the desired update rate
				while (delta >= 1) {
					update();
					
					--delta;
					++updates;
				}

				// Keeps drawing calls limited unless otherwise specified. Draws can and will be dropped if needed.
				if (_nsPerDraw == 0 || drawDelta >= 1) {
					render((float)delta);
					
					++draws;
					drawDelta = 0;
				}

				// TODO: TGM - Delete this, temporary! This is executed every second to display the update and frame rate.
				if (timeElapsed >= 1000000000) {
					timeElapsed -= 1000000000;

					System.out.println("[" + _gameThread.getName() + "][" + _gameThread.getId() + "] Updates: " + updates + ", Draws: " + draws);
					updates = 0;
					draws = 0;
				}

			}
		}
		
		
		
	} // END GameRunner
	
	

} // END Game

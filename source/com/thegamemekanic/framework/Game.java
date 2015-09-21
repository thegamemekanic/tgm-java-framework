package com.thegamemekanic.framework;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * 
 * 
 * @author The Game Mechanic
 */
public abstract class Game {

	// ================================================================================================================
	// Member Variables

	public final String Name;
	public final GameWindow Window;
	private final double NSPerUpdate;

	private GLFWErrorCallback _errorCallback;
	private Thread _gameThread;
	private boolean _isRunning;
	private double _nsPerDraw;
	private boolean _isVsyncEnabled;

	// ================================================================================================================
	// Constructors

	/**
	 * Create an instance of a game. Uses 60 updates per second as a default.
	 * 
	 * @param name
	 *            - Name of the game, used as the default window title and
	 *            thread name.
	 */
	public Game(String name) {
		this(name, 60);
	}

	/**
	 * Create an instance of a game.
	 * 
	 * @param name
	 *            - Name of the game, used as the default window title and
	 *            thread name.
	 * @param updateRate
	 *            - Number of updates per second the game loop will attempt to
	 *            achieve.
	 */
	public Game(String name, int updateRate) {
		Name = name;
		Window = new GameWindow();
		NSPerUpdate = updateRate > 0 ? 1000000000.0 / (double) updateRate : 1000000000.0 / 60.0;

		_gameThread = new Thread(new GameRunner(), Name);
		_isRunning = false;
		_nsPerDraw = 0.0D;
		_isVsyncEnabled = true;
	}

	// ================================================================================================================
	// Public Interface

	/**
	 * Begins execution of the game thread which runs the game loop.
	 */
	public final synchronized void start() {
		if (!_isRunning && _gameThread != null) {
			_isRunning = true;

			Window.game = this;
			_gameThread.start();
		}
	}

	/**
	 * Stops the game thread, terminating the game loop. The thread must be
	 * reinitialized in order to start the game loop again.
	 */
	public final synchronized void stop() {

		onExiting();

		if (_isRunning && _gameThread != null) {
			_isRunning = false;

			try {
				_gameThread.join();
				_gameThread = null;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Set a limit on how many frames per second can be drawn when v-sync is
	 * disabled. To remove the limit, set it to 0.
	 * 
	 * @param maxFPS
	 *            - Maximum frames that can be drawn when v-sync is disabled.
	 */
	public final void setMaxFPS(int maxFPS) {
		_nsPerDraw = maxFPS > 0 ? 1000000000D / (double) maxFPS : 0.0F;
	}

	/**
	 * Turn v-sync on or off. When enabled, the game will attempt to achieve a
	 * framerate that matches the refresh rate of the monitor it's being
	 * displayed on.
	 * 
	 * @param enable
	 *            - True to enable v-sync, false to disable.
	 */
	public final void ToggleVsync(boolean enable) {

		_isVsyncEnabled = enable;

		if (enable) {
			glfwMakeContextCurrent(Window.getHandle());
			glfwSwapInterval(1);
		} else {
			glfwMakeContextCurrent(Window.getHandle());
			glfwSwapInterval(0);
		}
	}

	// ================================================================================================================
	// Customizable methods

	/**
	 * Called once before the game loop starts, initialize everything that is
	 * needed to get the game running here.
	 */
	protected abstract void initialize();

	/**
	 * This is where all game logic should be updated, process user input,
	 * updating game state, update physics, etc.
	 */
	protected abstract void update();

	/**
	 * Called as frequently as possible unless limited by changing the game
	 * settings to <code>setMaxFramerate()</code>.
	 */
	protected abstract void render();

	/**
	 * Called as the game is being closed.
	 */
	protected void onExiting() {
	}

	/**
	 * Called when the game gains focus.
	 */
	protected void onActivated() {
	}

	/**
	 * Called when the game loses focus.
	 */
	protected void onDeactivated() {
	}

	/**
	 * Called when the game is dropping frames in order to update.
	 */
	protected void onRunningSlowly() {
	}

	// ================================================================================================================
	// Private Classes

	/**
	 * Handles running the game loop. It's placed in a private class to prevent
	 * exposing the run method, making it stupid proof.
	 * 
	 * @author The Game Mechanic
	 */
	private class GameRunner implements Runnable {

		/**
		 * The heartbeat of the game
		 */
		@Override // Runnable
		public final void run() {

			try {

				// Initialize LWJGL
				glfwSetErrorCallback(_errorCallback = errorCallbackPrint(System.err));
				if (glfwInit() != GL11.GL_TRUE) {
					throw new IllegalStateException("Unable to initialize GLFW");
				}

				// Window Hints! I should look these up...
				glfwDefaultWindowHints();
				glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
				glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

				// Create window.
				Window.Create(Name, 800, 600);
				Window.setCallbacks();
				long windowID = Window.getHandle();

				GLContext.createFromCurrent();
				
				// Cornflower blue baby! Shout out to XNA!
				glClearColor(0.39f, 0.58f, 0.93f, 0.0f); 

				// Allow the game extending this class to initialize their game
				// before starting the loop
				initialize();

				double delta = 0.0D;
				double drawDelta = 0.0D;
				long currTime = System.nanoTime();
				long lastTime = System.nanoTime();
				long elapsedTime = 0;
				int updates = 0, renders = 0;

				// The Game Loop!
				while (glfwWindowShouldClose(windowID) == GL_FALSE) {
					glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

					currTime = System.nanoTime();
					delta += (currTime - lastTime) / NSPerUpdate;
					drawDelta += _nsPerDraw > 0 ? (currTime - lastTime) / _nsPerDraw : 0;
					elapsedTime += currTime - lastTime;
					lastTime = currTime;

					// If the delta is greater than 2, frames will start
					// dropping and the game needs to be informed.
					if (delta >= 2.0D) {
						onRunningSlowly();
					}

					// Updates the game, keeping it in fixed step.
					while (delta >= 1) {
						update();

						++updates;
						--delta;
					}

					// Renders the game, dropping any frames the game doesn't
					// have time for.
					if (_isVsyncEnabled || _nsPerDraw == 0 || drawDelta > 1.0D) {
						render();

						++renders;
						--drawDelta;
					}

					glfwSwapBuffers(windowID);
					glfwPollEvents();

					if (elapsedTime >= 1000000000) {
						elapsedTime -= 1000000000;
						System.out.println("Updates: " + updates + " | Draws: " + renders);
						updates = 0;
						renders = 0;
					}

				}

				// Allows the game extending this class to handle what happens
				// as a game is exiting.
				onExiting();

				Window.release();

			} finally {
				glfwTerminate();
				_errorCallback.release();
			}

		}

	} // END GameRunner

} // END Game

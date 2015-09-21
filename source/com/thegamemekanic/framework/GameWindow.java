package com.thegamemekanic.framework;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.ByteBuffer;

import org.lwjgl.glfw.*;

/**
 * 
 * 
 * @author The Game Mekanic
 */
public class GameWindow {

	// ================================================================================================================
	// Member Variables

	private GLFWWindowFocusCallback _focusCallback;
	private GLFWKeyCallback _keyCallback;
	private String _title;
	private long _windowID;
	
	Game game;

	// ================================================================================================================
	// Constructor

	/**
	 * Create an instance of a Game Window.
	 * 
	 * @see <code>Create()</code> to finish initialization and show the window.
	 */
	public GameWindow() {
		_windowID = NULL;
		_title = "";
	}

	// ================================================================================================================
	// Public Interface

	/**
	 * Creates the window on the current OpenGL thread. If the window has
	 * already been created, this method does nothing.
	 * 
	 * @param title
	 *            - The title of the window.
	 * @param width
	 *            - Starting width of the window.
	 * @param height
	 *            - Starting height of the window.
	 */
	public void Create(String title, int width, int height) {

		// Only works when window hasn't been created.
		if (_windowID == NULL) {

			_windowID = glfwCreateWindow(width, height, title, NULL, NULL);

			// Need to crash if the window could not be created!
			if (_windowID == NULL) {
				throw new RuntimeException("Failed to create the GLFW window.");
			}

			// Handles key actions while the window is active.
			glfwSetKeyCallback(_windowID, _keyCallback = new GLFWKeyCallback() {
				@Override
				public void invoke(long window, int key, int scancode, int action, int mods) {
					if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)

						glfwSetWindowShouldClose(window, GL_TRUE);
				}
			});

			// Get the resolution of the primary monitor
			ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center our window
			glfwSetWindowPos(_windowID, (GLFWvidmode.width(vidmode) - width) / 2,
					(GLFWvidmode.height(vidmode) - height) / 2);

			// Make the OpenGL context current
			glfwMakeContextCurrent(_windowID);
			
			// Enable v-sync
			glfwSwapInterval(1);

			// Make the window visible
			glfwShowWindow(_windowID);
		}

	}

	/**
	 * Set the title of the window. If the window hasn't been created, this
	 * method won't do anything.
	 * 
	 * @param title
	 *            - Replaces the current title with the provided one.
	 */
	public final void setTitle(String title) {
		if (_windowID != NULL) {
			_title = title == null ? "" : title;
			glfwSetWindowTitle(_windowID, _title);
		}
	}

	/**
	 * Retrieve the current title of the window.
	 * 
	 * @return The current title of the window.
	 */
	public final String getTitle() {
		return _title;
	}

	/**
	 * Retrieves the handle to the window in the current OpenGL context. If the
	 * window hasn't been created, this will return NULL.
	 * 
	 * @return - Handle to the window in the current OpenGL context.
	 */
	public final long getHandle() {
		return _windowID;
	}

	/**
	 * Removes reference to the window from current context of OpenGL.
	 */
	public final void release() {
		glfwDestroyWindow(_windowID);
		
		_focusCallback.release();
		_keyCallback.release();
	}

	// ================================================================================================================
	// Package Methods

	final void setCallbacks() {

		glfwSetWindowFocusCallback(_windowID, _focusCallback = new GLFWWindowFocusCallback() {

			@Override
			public void invoke(long window, int focused) {
				if (focused == GL_TRUE) {
					game.onActivated();
				} else {
					game.onDeactivated();
				}
			}
		});

	}

} // END GameWindow
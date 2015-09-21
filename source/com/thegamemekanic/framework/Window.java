package com.thegamemekanic.framework;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;

public class Window {
	
	@SuppressWarnings("unused")
	private GLFWKeyCallback _keyCallback;
	private long _windowID;
	
	public Window() {
		_windowID = NULL;
	}
	
	public void Create(String title, int width, int height) {
		
		// Only works when window hasn't been created.
		if(_windowID == NULL) {
			
			glfwDefaultWindowHints();
			glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
			glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
			
			_windowID = glfwCreateWindow(width, height, title, NULL, NULL);
			
			if(_windowID == NULL) {
				throw new RuntimeException("Failed to create the GLFW window.");
			}
			
			glfwSetKeyCallback(_windowID, _keyCallback = new GLFWKeyCallback() {
	            @Override
	            public void invoke(long window, int key, int scancode, int action, int mods) {
	                if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
	                    glfwSetWindowShouldClose(window, GL_TRUE);
	            }
			});
			
			// Get the resolution of the primary monitor
	        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
	        // Center our window
	        glfwSetWindowPos(_windowID,
	            (GLFWvidmode.width(vidmode) - width) / 2,
	            (GLFWvidmode.height(vidmode) - height) / 2
	        );
	 
	        // Make the OpenGL context current
	        glfwMakeContextCurrent(_windowID);
	        // Enable v-sync
	        glfwSwapInterval(1);
	 
	        // Make the window visible
	        glfwShowWindow(_windowID);
			
		}		
	}
	
	
	final long getID() {
		return _windowID;
	}
	

}

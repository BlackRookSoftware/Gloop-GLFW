package com.blackrook.gloop.glfw;

import java.awt.Dimension;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWVidMode;

public final class GLFWTest 
{
	private GLFWWindow window; 
	
	public void run() 
	{
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
		loop();

		// Free the window callbacks and destroy the window
		window.destroy();

		// Terminate GLFW and free the error callback
		GLFWContext.terminate();
	}
	
	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWContext.setErrorStream(System.err);
		
		GLFWContext.init();

		// Configure GLFW
		GLFWWindow.Hints.reset();            // optional, the current window hints are already the default
		GLFWWindow.Hints.setVisible(false);  // the window will stay hidden after creation
		GLFWWindow.Hints.setResizable(true); // the window will be resizable
		
		// Create the window
		window = new GLFWWindow("Hello World!", 300, 300);

		/*
		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});
		*/

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = GLFWMonitor.getPrimaryMonitor().getVideoMode();
		Dimension dimension = window.getSize();
		window.setPosition(
			(vidmode.width() - (int)dimension.getWidth()) / 2,
			(vidmode.height() - (int)dimension.getHeight()) / 2
		);

		GLFWContext.makeWindowContextCurrent(window);

		// Enable v-sync
		GLFWContext.setSwapInterval(1);

		// Make the window visible
		window.setVisible(true);
	}

	private void loop() 
	{
		while (!window.isClosing())
		{
			window.swapBuffers();
			GLFWContext.pollEvents();
		}
	}


	public static void main(String[] args) 
	{
		(new GLFWTest()).run();
	}
}

package com.blackrook.gloop.glfw;

import java.awt.Dimension;
import java.util.Arrays;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWVidMode;

import com.blackrook.gloop.glfw.input.annotation.OnKeyAction;
import com.blackrook.gloop.glfw.input.annotation.OnKeyTypedAction;
import com.blackrook.gloop.glfw.input.annotation.OnMouseAxisAction;
import com.blackrook.gloop.glfw.input.annotation.OnMousePositionAction;
import com.blackrook.gloop.glfw.input.enums.KeyType;
import com.blackrook.gloop.glfw.input.enums.MouseAxisType;

public final class GLFWTest 
{
	private GLFWWindow window; 
	private GLFWInputSystem inputSystem;

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
	
	private void init()
	{
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWContext.setErrorStream(System.err);
		
		GLFWContext.init();

		// Configure GLFW
		GLFWWindow.Hints.reset();            // optional, the current window hints are already the default
		GLFWWindow.Hints.setVisible(false);  // the window will stay hidden after creation
		GLFWWindow.Hints.setResizable(true); // the window will be resizable
		
		inputSystem = new GLFWInputSystem();
				
		// Create the window
		window = new GLFWWindow("Hello World!", 300, 300);

		inputSystem.attachToWindow(window);
		inputSystem.addInputObject(new Keyboard());

		window.addDropListener((window, files)->{
			System.out.println(Arrays.toString(files));
		});

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = GLFWMonitor.getPrimaryMonitor().getVideoMode();
		
		// Center window.
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
			inputSystem.pollJoysticks();
		}
	}

	public class Keyboard
	{
		@OnKeyAction
		public void onKey(KeyType type, boolean pressed)
		{
			if (type == KeyType.ESCAPE && !pressed)
				window.setClosing(true);
		}
		
		@OnKeyTypedAction
		public void onType(char c)
		{
			System.out.println(c);
		}
		
		@OnMouseAxisAction
		public void onMouseAxis(MouseAxisType type, double amount)
		{
			if (type == MouseAxisType.X)
				System.out.println("Mouse X: " + amount);
			else if (type == MouseAxisType.Y)
				System.out.println("Mouse Y: " + amount);
		}

		@OnMousePositionAction
		public void onMousePosition(MouseAxisType type, double value)
		{
			if (type == MouseAxisType.X)
				System.out.println("Mouse Pos X: " + value);
			else if (type == MouseAxisType.Y)
				System.out.println("Mouse Pos Y: " + value);
		}
		
	}

	public static void main(String[] args) 
	{
		(new GLFWTest()).run();
	}
}

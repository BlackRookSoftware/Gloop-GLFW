package com.blackrook.gloop.glfw;

import java.awt.Dimension;
import java.util.Arrays;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWVidMode;

import com.blackrook.gloop.glfw.input.GLFWInputSystem;
import com.blackrook.gloop.glfw.input.GLFWInputSystem.JoystickConnectionListener;
import com.blackrook.gloop.glfw.input.annotation.OnJoystickAxisAction;
import com.blackrook.gloop.glfw.input.annotation.OnJoystickButtonAction;
import com.blackrook.gloop.glfw.input.annotation.OnJoystickHatAction;
import com.blackrook.gloop.glfw.input.annotation.OnKeyAction;
import com.blackrook.gloop.glfw.input.annotation.OnKeyTypedAction;
import com.blackrook.gloop.glfw.input.annotation.OnMouseAxisAction;
import com.blackrook.gloop.glfw.input.annotation.OnMouseButtonAction;
import com.blackrook.gloop.glfw.input.annotation.OnMousePositionAction;
import com.blackrook.gloop.glfw.input.annotation.OnMouseScrollAction;
import com.blackrook.gloop.glfw.input.enums.JoystickAxisType;
import com.blackrook.gloop.glfw.input.enums.JoystickButtonType;
import com.blackrook.gloop.glfw.input.enums.JoystickHatType;
import com.blackrook.gloop.glfw.input.enums.KeyType;
import com.blackrook.gloop.glfw.input.enums.MouseAxisType;
import com.blackrook.gloop.glfw.input.enums.MouseButtonType;

public final class GLFWTest 
{
	private GLFWWindow window; 
	private GLFWInputSystem inputSystem;

	public void run() 
	{
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");
		init();
		GLFWContext.mainLoop(window, inputSystem);
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
		inputSystem.addJoystickListener(new JoystickConnectionListener()
		{
			@Override
			public void onJoystickConnect(int joystickId, boolean isGamepad, String guid, String name)
			{
				System.out.println("Connect " + joystickId);
				System.out.println("    Gamepad? " + isGamepad);
				System.out.println("    GUID:    " + guid);
				System.out.println("    Name:    " + name);
				inputSystem.addJoystickInputObject(joystickId, new Gamepad());
			}

			@Override
			public void onJoystickDisconnect(int joystickId)
			{
				System.out.println("Disconnect " + joystickId);
				inputSystem.removeJoystickInputObject(joystickId);
			}
		});

		window.addDropListener((window, files) ->
		{
			System.out.println(Arrays.toString(files));
		});
		
		inputSystem.enableJoysticks();

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = GLFWMonitor.getPrimaryMonitor().getVideoMode();
		
		// Center window.
		Dimension dimension = window.getSize();
		window.setPosition(
			(vidmode.width() - (int)dimension.getWidth()) / 2,
			(vidmode.height() - (int)dimension.getHeight()) / 2
		);

		// Enable v-sync
		GLFWContext.setSwapInterval(1);

		GLFWContext.makeWindowContextCurrent(window);

		// Make the window visible
		window.setVisible(true);
		
		GLFWContext.addAlwaysRunnable(()->window.swapBuffers());
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
			System.out.println("Mouse " + type + ": " + amount);
		}

		@OnMousePositionAction
		public void onMousePosition(MouseAxisType type, double value)
		{
			System.out.println("Mouse Position " + type + ": " + value);
		}

		@OnMouseButtonAction
		public void onMouseButton(MouseButtonType type, boolean pressed)
		{
			System.out.println("Mouse " + type + ": " + pressed);
		}

		@OnMouseScrollAction
		public void onMouseScroll(MouseAxisType type, double amount)
		{
			System.out.println("Mouse Scroll " + type + ": " + amount);
		}

	}
	
	public class Gamepad
	{
		@OnJoystickAxisAction
		public void onAxis(JoystickAxisType type, double value)
		{
			System.out.println("Axis " + type + ": " + value);
		}

		@OnJoystickButtonAction
		public void onButton(JoystickButtonType type, boolean pressed)
		{
			System.out.println("Button " + type + ": " + pressed);
		}
		
		@OnJoystickHatAction
		public void onHat(int index, JoystickHatType type)
		{
			System.out.println("Hat " + index + ": " + type);
		}
	}

	public static void main(String[] args) 
	{
		(new GLFWTest()).run();
	}
}

/*******************************************************************************
 * Copyright (c) 2020-2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.glfw;

import java.util.Arrays;

import org.lwjgl.Version;

import com.blackrook.gloop.glfw.GLFWInputSystem.JoystickConnectionListener;
import com.blackrook.gloop.glfw.GLFWWindow.CursorMode;
import com.blackrook.gloop.glfw.GLFWWindow.WindowHints;
import com.blackrook.gloop.glfw.GLFWWindow.WindowListener;
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

	private void init()
	{
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWContext.setErrorStream(System.err);
		
		// Configure GLFW
		WindowHints hints = (new WindowHints())
			.setVisible(false)
			.setResizable(true);
		
		inputSystem = new GLFWInputSystem();

		// Create the window
		window = new GLFWWindow(hints, "Hello World!", 300, 300);

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

		inputSystem.enableJoysticks();

		window.addDropListener((window, files) ->
		{
			System.out.println(Arrays.toString(files));
		});

		window.addWindowListener(new WindowListener()
		{
			@Override
			public void onSizeChange(GLFWWindow window, int width, int height)
			{
				System.out.println("Window Size Change: " + width + ", " + height);
			}
			
			@Override
			public void onRestore(GLFWWindow window)
			{
				System.out.println("Window Restore");
			}
			
			@Override
			public void onRefresh(GLFWWindow window)
			{
				System.out.println("Window Refresh");
			}
			
			@Override
			public void onPositionChange(GLFWWindow window, int x, int y)
			{
				System.out.println("Window Position Change: " + x + ", " + y);
			}
			
			@Override
			public void onMouseExited(GLFWWindow window)
			{
				System.out.println("Mouse Exit");
			}
			
			@Override
			public void onMouseEntered(GLFWWindow window)
			{
				System.out.println("Mouse Entered");
			}
			
			@Override
			public void onMaximize(GLFWWindow window)
			{
				System.out.println("Window Maximize");
			}
			
			@Override
			public void onIconify(GLFWWindow window)
			{
				System.out.println("Window Iconify");
			}
			
			@Override
			public void onFramebufferChange(GLFWWindow window, int width, int height)
			{
				System.out.println("Window Framebuffer Change: " + width + ", " + height);
			}
			
			@Override
			public void onFocus(GLFWWindow window)
			{
				System.out.println("Window Focus");
			}
			
			@Override
			public void onContentScaleChange(GLFWWindow window, float x, float y)
			{
				System.out.println("Window Content Scale Change: " + x + ", " + y);
			}
			
			@Override
			public void onClose(GLFWWindow window)
			{
				System.out.println("Window Close");
			}
			
			@Override
			public void onBlur(GLFWWindow window)
			{
				System.out.println("Window Blur");
			}
		});
		
		window.center(GLFWMonitor.getPrimaryMonitor());

		GLFWContext.makeWindowContextCurrent(window);

		// Enable v-sync
		GLFWContext.setSwapInterval(1);

		// Swap buffers every poll loop.
		GLFWContext.addRunnableAlways(()->window.swapBuffers());

		// Make the window visible
		window.setVisible(true);
	}

	public void run() 
	{
		init();
		GLFWContext.mainLoop(window, inputSystem);
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
			switch (c)
			{
				case 'j':
					GLFWContext.addRunnableOnce(()->inputSystem.enableJoysticks());
					System.out.println("Joysticks enabled.");
					break;
				case 'J':
					GLFWContext.addRunnableOnce(()->inputSystem.disableJoysticks());
					System.out.println("Joysticks disabled.");
					break;
				case 'c':
					GLFWContext.addRunnableOnce(()->{
						window.setRawMouseMotion(false);
						window.setCursorMode(CursorMode.NORMAL);
					});
					System.out.println("Cursor enabled.");
					break;
				case 'C':
					GLFWContext.addRunnableOnce(()->{
						window.setRawMouseMotion(true);
						window.setCursorMode(CursorMode.DISABLED);
					});
					System.out.println("Cursor disabled.");
					break;
			}
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
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");
		(new GLFWTest()).run();
	}
}

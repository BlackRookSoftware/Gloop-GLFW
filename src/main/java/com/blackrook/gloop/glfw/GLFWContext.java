package com.blackrook.gloop.glfw;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryUtil;

import com.blackrook.gloop.glfw.exception.GLFWException;

/**
 * GLFW initializer state.
 * @author Matthew Tropiano
 */
public final class GLFWContext 
{
	private static boolean initialized = false;
	private static Map<GLFWWindow, Thread> WINDOW_TO_CONTEXT_THREAD;
	private static Map<Thread, GLFWWindow> CONTEXT_THREAD_TO_WINDOW;
	private static List<JoystickConnectionListener> LISTENERS_JOYSTICK;

	/**
	 * A listener interface for when joysticks/gamepads get 
	 * connected or disconnected from GLFW.
	 */
	public interface JoystickConnectionListener
	{
		/**
		 * Called when a joystick connects to the system.
		 * @param joystickId the GLFW joystick id.
		 */
		void onJoystickConnect(int joystickId);

		/**
		 * Called when a joystick disconnects from the system.
		 * @param joystickId the GLFW joystick id.
		 */
		void onJoystickDisconnect(int joystickId);
	}

	/**
	 * Initializes GLFW.
	 * If already initialized, this does nothing.
	 * <p><b>This must only be called from the main thread.</b>
	 */
	public static void init()
	{
		if (initialized)
			return;
		
		GLFWErrorCallback.createPrint(System.err).set();
		if (!GLFW.glfwInit())
			throw new GLFWException("GLFW initialization failed!");

		GLFW.glfwDefaultWindowHints();
		WINDOW_TO_CONTEXT_THREAD = new HashMap<>();
		CONTEXT_THREAD_TO_WINDOW = new HashMap<>();
		LISTENERS_JOYSTICK = new ArrayList<>();
		
		GLFW.glfwSetJoystickCallback((jid, event) -> 
		{
			switch (event)
			{
				case GLFW.GLFW_CONNECTED:
				{
					for (int i = 0; i < LISTENERS_JOYSTICK.size(); i++)
						LISTENERS_JOYSTICK.get(i).onJoystickConnect(jid);
				}
				break;
				
				case GLFW.GLFW_DISCONNECTED:
				{
					for (int i = 0; i < LISTENERS_JOYSTICK.size(); i++)
						LISTENERS_JOYSTICK.get(i).onJoystickDisconnect(jid);
				}
				break;
			}
		});
		
		initialized = true;
	}
	
	/**
	 * Destroys GLFW and frees its resources.
	 * The error callback is also freed.
	 * Does nothing if GLFW was not initialized.
	 * <p><b>This must only be called from the main thread.</b>
	 */
	public static void terminate()
	{
		if (!initialized)
			return;
		WINDOW_TO_CONTEXT_THREAD = null;
		CONTEXT_THREAD_TO_WINDOW = null;
		LISTENERS_JOYSTICK = null;
		GLFW.glfwTerminate();
		setErrorStream(null);
		initialized = false;
	}

	/**
	 * Sets the output stream for GLFW error callbacks.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param stream the stream to print to. 
	 */
	public static void setErrorStream(PrintStream stream)
	{
		if (stream == null)
			GLFW.glfwSetErrorCallback(null).free();
		else
			GLFWErrorCallback.createPrint(stream).set();
	}
	
	/**
	 * Makes a window the current target of OpenGL/GLES calls for this thread.
	 * If the provided window is already current on this thread, nothing happens.
	 * If the provided window is already current on a different thread, this throws an {@link IllegalStateException}.
	 * If the provided window is null, and this thread has no bound context, nothing happens.
	 * <p>This can be called from any thread, preferably the rendering thread.
	 * @param window the window to set as current, or null to detach from the thread.
	 * @throws IllegalStateException if the provided window is not null, and already current on a different thread.
	 * @see GLFW#glfwMakeContextCurrent(long)
	 */
	public static void makeWindowContextCurrent(GLFWWindow window)
	{
		Thread currentThread = Thread.currentThread();
		if (window != null)
		{
			Thread thread;
			if ((thread = WINDOW_TO_CONTEXT_THREAD.get(window)) != null)
			{
				if (thread != currentThread)
					throw new IllegalStateException("Window's context is already current on a different thread.");
				else
					return;
			}
			else
			{
				GLFW.glfwMakeContextCurrent(window.getHandle());
				WINDOW_TO_CONTEXT_THREAD.put(window, currentThread);
				CONTEXT_THREAD_TO_WINDOW.put(currentThread, window);
			}
		}
		else
		{
			GLFW.glfwMakeContextCurrent(MemoryUtil.NULL);
			GLFWWindow currentWindow;
			if ((currentWindow = CONTEXT_THREAD_TO_WINDOW.remove(currentThread)) != null)
				WINDOW_TO_CONTEXT_THREAD.remove(currentWindow);
		}		
	}
	
	/**
	 * Sets how many vertical blanks need to occur before a window buffer swap.
	 * In layman's terms, this either sets VSync on (1) or off (0).
	 * This is set for all windows.
	 * <p>This can be called from any thread.
	 * @param blanks the amount of vertical blanks to wait.
	 * @throws IllegalArgumentException if blanks is less than 0.
	 */
	public static void setSwapInterval(int blanks)
	{
		if (blanks < 0)
			throw new IllegalArgumentException("blanks cannot be less than 0");
		GLFW.glfwSwapInterval(blanks);
	}

	/**
	 * Polls all pending events.
	 * Any event that came in from any window is flushed.
	 * <p><b>This must only be called from the main thread.</b>
	 * <p><b>This must NOT be called from any callback or listener.</b>
	 * @throws IllegalArgumentException if blanks is less than 0.
	 */
	public static void pollEvents()
	{
		GLFW.glfwPollEvents();
	}

}

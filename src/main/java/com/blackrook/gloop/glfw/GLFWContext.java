/*******************************************************************************
 * Copyright (c) 2020-2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.glfw;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryUtil;

import com.blackrook.gloop.glfw.exception.GLFWException;

/**
 * GLFW context state.
 * @author Matthew Tropiano
 */
public final class GLFWContext 
{
	private static GLFWErrorCallback ERROR_STREAM_CALLBACK;

	private static boolean initialized = false;
	private static Map<GLFWWindow, Thread> WINDOW_TO_CONTEXT_THREAD;
	private static Map<Thread, GLFWWindow> CONTEXT_THREAD_TO_WINDOW;

	private static Object RUNNABLE_MUTEX;
	private static List<Runnable> RUNNABLE_ALWAYS;
	private static Deque<Runnable> RUNNABLE_ONCE;
	private static List<Runnable> RUNNABLE_ALWAYS_AFTER_POLL;
	private static Deque<Runnable> RUNNABLE_ON_END;

	/**
	 * Initializes GLFW.
	 * If already initialized, this does nothing.
	 * <p><b>This must only be called from the main thread.</b>
	 */
	static void init()
	{
		if (initialized)
			return;
		
		if (!GLFW.glfwInit())
			throw new GLFWException("GLFW initialization failed!");

		GLFW.glfwDefaultWindowHints();
		WINDOW_TO_CONTEXT_THREAD = new HashMap<>(4);
		CONTEXT_THREAD_TO_WINDOW = new HashMap<>(4);
		RUNNABLE_ALWAYS = new ArrayList<>(4);
		RUNNABLE_ALWAYS_AFTER_POLL = new ArrayList<>(4);
		RUNNABLE_ONCE = new LinkedList<>();
		RUNNABLE_ON_END = new LinkedList<>();
		RUNNABLE_MUTEX = new Object();
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
		RUNNABLE_ALWAYS = null;
		RUNNABLE_ALWAYS_AFTER_POLL = null;
		RUNNABLE_ONCE = null;
		RUNNABLE_ON_END = null;
		RUNNABLE_MUTEX = null;
		GLFW.glfwTerminate();
		setErrorStream(null);
		initialized = false;
	}

	/**
	 * Sets the output stream for GLFW error callbacks.
	 * This can be called before initialization.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param stream the stream to print to. 
	 */
	public static void setErrorStream(PrintStream stream)
	{
		if (stream == null)
		{
			if (ERROR_STREAM_CALLBACK != null)
				ERROR_STREAM_CALLBACK.free();
		}
		else
		{
			if (ERROR_STREAM_CALLBACK != null)
				ERROR_STREAM_CALLBACK.free();
			ERROR_STREAM_CALLBACK = GLFWErrorCallback.createPrint(stream).set();
		}
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
	public static void makeContextCurrent(GLFWWindow window)
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
	 * <p>This cannot be called until {@link #makeContextCurrent(GLFWWindow)} is called in this thread.
	 * <p>This can be called from any thread.
	 * @param blanks the amount of vertical blanks to wait.
	 * @throws IllegalArgumentException if blanks is less than 0.
	 * @see GLFWContext#makeContextCurrent(GLFWWindow)
	 */
	public static void setSwapInterval(int blanks)
	{
		if (blanks < 0)
			throw new IllegalArgumentException("blanks cannot be less than 0");
		GLFW.glfwSwapInterval(blanks);
	}

	/**
	 * Polls and processes all pending events.
	 * Any event that came in from any window is flushed through the callbacks and processed by bound listeners.
	 * <p><b>This must only be called from the main thread. 
	 * It is suggested that this be put in a loop at the end of initialization.</b>
	 * <p><b>This must NOT be called from any callback nor listener.</b>
	 * @throws IllegalArgumentException if blanks is less than 0.
	 */
	public static void pollEvents()
	{
		init();
		GLFW.glfwPollEvents();
	}

	/**
	 * Adds a Runnable that is invoked at the beginning of the loop
	 * in {@link #mainLoop(GLFWWindow, GLFWInputSystem)} such that they execute on the main thread.
	 * Runnables are executed in the order that they are added this way. 
	 * @param runnable the runnable to add.
	 * @see #mainLoop(GLFWWindow, GLFWInputSystem)
	 */
	public static void addRunnableAlways(Runnable runnable)
	{
		synchronized (RUNNABLE_MUTEX)
		{
			RUNNABLE_ALWAYS.add(runnable);
		}
	}

	/**
	 * Removes a Runnable that was added by {@link #addRunnableAlways(Runnable)}.
	 * @param runnable the runnable to remove.
	 * @see #mainLoop(GLFWWindow, GLFWInputSystem)
	 */
	public static void removeRunnableAlways(Runnable runnable)
	{
		synchronized (RUNNABLE_MUTEX)
		{
			RUNNABLE_ALWAYS.remove(runnable);
		}
	}

	/**
	 * Adds a Runnable that is invoked at the end of the loop after events are polled
	 * in {@link #mainLoop(GLFWWindow, GLFWInputSystem)} such that they execute on the main thread, 
	 * after event/input polling.
	 * <p>Runnables are executed in the order that they are added this way, after event/input polling.
	 * <p>This can be called from any thread.
	 * @param runnable the runnable to add.
	 * @see #mainLoop(GLFWWindow, GLFWInputSystem)
	 */
	public static void addRunnableAlwaysAfterPoll(Runnable runnable)
	{
		synchronized (RUNNABLE_MUTEX)
		{
			RUNNABLE_ALWAYS_AFTER_POLL.add(runnable);
		}
	}

	/**
	 * Removes a Runnable that was added by {@link #addRunnableAlwaysAfterPoll(Runnable)}.
	 * @param runnable the runnable to remove.
	 * @see #mainLoop(GLFWWindow, GLFWInputSystem)
	 */
	public static void removeRunnableAlwaysAfterPoll(Runnable runnable)
	{
		synchronized (RUNNABLE_MUTEX)
		{
			RUNNABLE_ALWAYS_AFTER_POLL.remove(runnable);
		}
	}

	/**
	 * Adds a Runnable that is invoked only once at the beginning of the loop
	 * in {@link #mainLoop(GLFWWindow, GLFWInputSystem)} such that it executes on the main thread,
	 * but before the "always runnables" are invoked.
	 * Runnables are executed in the order that they are added this way.
	 * <p> HINT: You can invoke most things that require being on the "main thread" using this,
	 * provided that the loop is active! 
	 * @param runnable the runnable to add.
	 * @see #mainLoop(GLFWWindow, GLFWInputSystem)
	 */
	public static void addRunnableOnce(Runnable runnable)
	{
		synchronized (RUNNABLE_MUTEX)
		{
			RUNNABLE_ONCE.add(runnable);
		}
	}

	/**
	 * Adds a Runnable that is invoked only once at the end of the loop
	 * in {@link #mainLoop(GLFWWindow, GLFWInputSystem)} such that it executes on the main thread,
	 * but only once the loop is escaped due to a closing window.
	 * Runnables are executed in the order that they are added this way.
	 * @param runnable the runnable to add.
	 * @see #mainLoop(GLFWWindow, GLFWInputSystem)
	 */
	public static void addRunnableOnLoopEnd(Runnable runnable)
	{
		synchronized (RUNNABLE_MUTEX)
		{
			RUNNABLE_ON_END.add(runnable);
		}
	}

	/**
	 * Enters a loop that sets the window and input system, and polls for events and input 
	 * until the window is closed.
	 * When the window closes, the window is destroyed and GLFW is terminated.
	 * <p>It is strongly advised that you use this method for starting the main loop
	 * <p><b>This must only be called from the main thread to ensure that everything works properly!</b>
	 * @param window the provided window handle.
	 * @param inputSystem the input system to poll (for joystick input - mouse/keyboard is handled by the window).
	 * @see #pollEvents()
	 * @see GLFWWindow#isClosing()
	 * @see GLFWInputSystem#pollJoysticks()
	 * @see GLFWWindow#destroy()
	 * @see #terminate()
	 */
	public static void mainLoop(GLFWWindow window, GLFWInputSystem inputSystem)
	{
		Objects.requireNonNull(window);
		Objects.requireNonNull(inputSystem);
		
		// Enter loop until the window wants to close.
		while (!window.isClosing())
		{
			synchronized (RUNNABLE_MUTEX)
			{
				runAll(RUNNABLE_ONCE);
				runAll(RUNNABLE_ALWAYS);
				pollEvents();
				inputSystem.pollJoysticks();
				runAll(RUNNABLE_ALWAYS_AFTER_POLL);
			}
		}

		runAll(RUNNABLE_ON_END);
		
		// Disable joysticks (frees some callbacks).
		inputSystem.disableJoysticks();
		
		// Destroy the window (its callbacks are freed).
		window.destroy();

		// Terminate GLFW (its callbacks are freed).
		terminate();
	}
	
	private static void runAll(List<Runnable> runnables)
	{
		for (int i = 0; i < runnables.size(); i++)
			runnables.get(i).run();
	}

	private static void runAll(Deque<Runnable> runnables)
	{
		while (!runnables.isEmpty())
			runnables.pollFirst().run();
	}

}

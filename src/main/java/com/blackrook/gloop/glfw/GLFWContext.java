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
	private static volatile boolean initialized = false;
	private static final Object INITMUTEX = new Object();
	private static GLFWErrorCallback ERROR_STREAM_CALLBACK;
	private static Map<GLFWWindow, Thread> WINDOW_TO_CONTEXT_THREAD;
	private static Map<Thread, GLFWWindow> CONTEXT_THREAD_TO_WINDOW;

	/**
	 * Initializes GLFW.
	 * If already initialized, this does nothing.
	 * <p><b>This must only be called from the main thread.</b>
	 */
	public static void init()
	{
		if (initialized)
			return;
		
		synchronized (INITMUTEX)
		{
			// Early out for other threads.
			if (initialized)
				return;
			
			if (ERROR_STREAM_CALLBACK == null)
				setErrorStream(System.err);
			if (!GLFW.glfwInit())
				throw new GLFWException("GLFW initialization failed!");

			GLFW.glfwDefaultWindowHints();
			WINDOW_TO_CONTEXT_THREAD = new HashMap<>(4);
			CONTEXT_THREAD_TO_WINDOW = new HashMap<>(4);
			initialized = true;
		}
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
	 * In layman's terms, this either sets VSync on (1 or greater) or off (0).
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
	 */
	public static void pollEvents()
	{
		init();
		GLFW.glfwPollEvents();
	}

	/**
	 * Polls and processes all pending events, but waits for an event first.
	 * Any event that came in from any window is flushed through the callbacks and processed by bound listeners.
	 * <p><b>This must only be called from the main thread. 
	 * It is suggested that this be put in a loop at the end of initialization.</b>
	 * <p><b>This must NOT be called from any callback nor listener.</b>
	 */
	public static void awaitEvents()
	{
		init();
		GLFW.glfwWaitEvents();
	}

	/**
	 * Polls and processes all pending events, but waits for an event first for a specific amount of time.
	 * Any event that came in from any window is flushed through the callbacks and processed by bound listeners.
	 * <p><b>This must only be called from the main thread. 
	 * It is suggested that this be put in a loop at the end of initialization.</b>
	 * <p><b>This must NOT be called from any callback nor listener.</b>
	 * <p>NOTE: The corresponding call in GLFW uses a double for its wait parameter. 
	 * This is in milliseconds because anything smaller than a millisecond does not 
	 * sleep an adequate time, and would act as though you called <code>glfwPollEvents()</code> instead.
	 * @param millis the time in milliseconds to wait.
	 */
	public static void awaitEvents(long millis)
	{
		init();
		GLFW.glfwWaitEventsTimeout(millis / 1000.0);
	}

	/**
	 * Posts an empty event so that a call to {@link #awaitEvents()}
	 * continues on without receiving anything processable.
	 * <p>This can be called from any thread.
	 */
	public static void postEmptyEvent()
	{
		init();
		GLFW.glfwPostEmptyEvent();
	}

	/**
	 * Creates a mechanism for looping on polling a GLFWWindow and InputSystem.
	 * Already assumes that the {@link GLFWInputSystem} is attached ({@link GLFWInputSystem#attachToWindow(GLFWWindow)})
	 * to the window for listening to events.
	 * @param window the provided window handle.
	 * @param inputSystem the input system to poll (for joystick input - mouse/keyboard is handled by the window).
	 * @return a new main loop object.
	 */
	public static MainLoop createLoop(GLFWWindow window, GLFWInputSystem inputSystem)
	{
		return new MainLoop(window, inputSystem);
	}
	
	/**
	 * Main GLFW looper.
	 * This class serves as a means to create a simple event listener loop
	 * where the user can add hooks that need to run on the main thread before
	 * or after event processing.
	 */
	public static class MainLoop implements Runnable
	{
		private GLFWWindow window;
		private GLFWInputSystem inputSystem;
		private boolean shutDownOnExit;
		private long eventWaitMillis;
		private long loopWaitMillis;
		private Object runnableMutex;
		private List<Runnable> runnableAlways;
		private Deque<Runnable> runnableOnce;
		private List<Runnable> runnableAlwaysAfterPoll;
		private Deque<Runnable> runnableOnExit;
		
		/**
		 * @param window the provided window handle.
		 * @param inputSystem the input system to poll (for joystick input - mouse/keyboard is handled by the window).
		 */
		private MainLoop(GLFWWindow window, GLFWInputSystem inputSystem)
		{
			Objects.requireNonNull(window);
			Objects.requireNonNull(inputSystem);
			this.window = window;
			this.inputSystem = inputSystem;
			this.shutDownOnExit = false;
			this.eventWaitMillis = 1L;
			this.loopWaitMillis = 0L;
			this.runnableAlways = new ArrayList<>(4);
			this.runnableAlwaysAfterPoll = new ArrayList<>(4);
			this.runnableOnce = new LinkedList<>();
			this.runnableOnExit = new LinkedList<>();
			this.runnableMutex = new Object();
		}
		
		/**
		 * Adds a Runnable that is invoked at the beginning of the loop
		 * such that they execute on the main thread.
		 * <p>
		 * Runnables are executed in the order that they are added this way. 
		 * @param runnable the runnable to add.
		 */
		public void addRunnableAlways(Runnable runnable)
		{
			synchronized (runnableMutex)
			{
				runnableAlways.add(runnable);
			}
		}

		/**
		 * Removes a Runnable that was added by {@link #addRunnableAlways(Runnable)}.
		 * @param runnable the runnable to remove.
		 */
		public void removeRunnableAlways(Runnable runnable)
		{
			synchronized (runnableMutex)
			{
				runnableAlways.remove(runnable);
			}
		}

		/**
		 * Adds a Runnable that is invoked at the end of the loop after events are polled
		 * such that they execute on the main thread, after event/input polling.
		 * <p>Runnables are executed in the order that they are added this way, after event/input polling.
		 * @param runnable the runnable to add.
		 */
		public void addRunnableAlwaysAfterPoll(Runnable runnable)
		{
			synchronized (runnableMutex)
			{
				runnableAlwaysAfterPoll.add(runnable);
			}
		}

		/**
		 * Removes a Runnable that was added by {@link #addRunnableAlwaysAfterPoll(Runnable)}.
		 * @param runnable the runnable to remove.
		 */
		public void removeRunnableAlwaysAfterPoll(Runnable runnable)
		{
			synchronized (runnableMutex)
			{
				runnableAlwaysAfterPoll.remove(runnable);
			}
		}

		/**
		 * Adds a Runnable that is invoked only once at the beginning of the loop
		 * such that it executes on the main thread, but before the "always runnables" are invoked.
		 * <p> Runnables are executed in the order that they are added this way.
		 * <p> NOTE: You can invoke most things that require being on the "main thread" using this,
		 * provided that the loop is active! 
		 * @param runnable the runnable to add.
		 */
		public void addRunnableOnce(Runnable runnable)
		{
			synchronized (runnableMutex)
			{
				runnableOnce.add(runnable);
			}
		}

		/**
		 * Adds a Runnable that is invoked only once at the end of the loop
		 * such that it executes on the main thread, but only once the loop 
		 * is escaped due to a closing window.
		 * <p>
		 * Runnables are executed in the order that they are added this way.
		 * @param runnable the runnable to add.
		 */
		public void addRunnableOnLoopExit(Runnable runnable)
		{
			synchronized (runnableMutex)
			{
				runnableOnExit.add(runnable);
			}
		}

		/**
		 * Sets the amount of time in milliseconds to wait for new events in the loop.
		 * By the time this wait occurs, joystick events will have been polled.
		 * Default is 1 millisecond.
		 * @param eventWaitMillis the time to wait in milliseconds, or 0 or less for no wait.
		 */
		public void setEventWait(long eventWaitMillis)
		{
			this.eventWaitMillis = Math.max(0L, eventWaitMillis);
		}
		
		/**
		 * Sets the amount of time in milliseconds to wait at the end of the loop after
		 * all hooks run and all events are polled.
		 * Default is 0 milliseconds (no wait).
		 * @param loopWaitMillis the time to wait in milliseconds, or 0 or less for no wait.
		 */
		public void setLoopWaitMillis(long loopWaitMillis)
		{
			this.loopWaitMillis = Math.max(0L, loopWaitMillis);
		}
		
		/**
		 * Sets if GLFW shuts down on exit.
		 * If enabled, once the loop exits, the window is destroyed, input callbacks are freed,
		 * and {@link GLFWContext#terminate()} is called.
		 * Disabled by default.
		 * @param shutDownOnExit true to enable, false to disable.
		 */
		public void setShutDownOnExit(boolean shutDownOnExit)
		{
			this.shutDownOnExit = shutDownOnExit;
		}
		
		/**
		 * Enters a loop that sets the window and input system, and polls for events and input 
		 * until the window is closed. When the window closes, the loop exits.
		 * <p>It is strongly advised that you use this method for starting the main event loop.
		 * <p><b>This must only be called from the main thread to ensure that everything works properly!</b>
		 * @see GLFWWindow#isClosing()
		 * @see GLFWInputSystem#pollJoysticks()
		 * @see GLFWContext#awaitEvents(long)
		 */
		public void run()
		{
			// Enter loop until the window wants to close.
			while (!window.isClosing())
			{
				synchronized (runnableMutex)
				{
					runAll(runnableOnce);
					runAll(runnableAlways);
					inputSystem.pollJoysticks();
					awaitEvents(eventWaitMillis);
					runAll(runnableAlwaysAfterPoll);
				}
				loopWait(loopWaitMillis);
			}
		
			runAll(runnableOnExit);
			
			if (shutDownOnExit)
			{
				// Disable joysticks (frees some callbacks).
				inputSystem.disableJoysticks();
				// Destroy the window (its callbacks are freed).
				window.destroy();
				// Terminate GLFW (its callbacks are freed).
				terminate();
			}
		}

		private static void loopWait(long millis)
		{
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				// Eat interrupt.
			}
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
	
}

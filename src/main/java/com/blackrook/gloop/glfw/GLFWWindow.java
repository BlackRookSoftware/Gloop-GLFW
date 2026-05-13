/*******************************************************************************
 * Copyright (c) 2020-2024 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.glfw;

import java.awt.image.BufferedImage;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.blackrook.gloop.glfw.exception.GLFWException;

/**
 * A GLFW window instance.
 * @author Matthew Tropiano
 */
public class GLFWWindow extends GLFWHandle
{
	/** Internal listener for internal state. */
	private static final WindowAdapter INTERNAL_ADAPTER = new WindowAdapter()
	{
		@Override
		public void onPositionChange(GLFWWindow window, int x, int y) 
		{
			window.state.positionX = x;
			window.state.positionY = y;
		}
		
		@Override
		public void onSizeChange(GLFWWindow window, int width, int height)
		{
			window.state.width = width;
			window.state.height = height;
		}
		
		@Override
		public void onFramebufferChange(GLFWWindow window, int width, int height)
		{
			window.state.frameBufferWidth = width;
			window.state.frameBufferHeight = height;
		}
		
		@Override
		public void onContentScaleChange(GLFWWindow window, float x, float y)
		{
			window.state.contentScaleX = x;
			window.state.contentScaleY = y;
		}
		
	};
	
	/** The memory address. */
	private long handle;
	/** Is this allocated? */
	private boolean allocated;
	/** Assigned monitor. */
	private GLFWMonitor monitor;
	
	/** List of window event listeners. */
	private List<WindowListener> windowListeners;
	/** List of input event listeners. */
	private List<InputListener> inputListeners;
	/** List of file drop event listeners. */
	private List<DropListener> dropListeners;

	/** Window characteristic state set via callbacks. */
	private State state;
	
	/**
	 * Enum of window cursor modes.
	 */
	public enum CursorMode
	{
		/** Cursor acts normally.  */
		NORMAL(GLFW.GLFW_CURSOR_NORMAL),
		/** Cursor is hidden during window hover.  */
		HIDDEN(GLFW.GLFW_CURSOR_HIDDEN),
		/** Cursor is disabled; mouse movement is grabbed by the window and movement is unlimited.  */
		DISABLED(GLFW.GLFW_CURSOR_DISABLED);
		
		final int glfwVal;
		private CursorMode(int value) {this.glfwVal = value;}
	}

	/**
	 * A window event listener interface. 
	 */
	public static interface WindowListener
	{
		/**
		 * Called on a window close event.
		 * @param window the source window.
		 */
		void onClose(GLFWWindow window);
		
		/**
		 * Called on a window refresh event.
		 * @param window the source window.
		 */
		void onRefresh(GLFWWindow window);
		
		/**
		 * Called on a window focus event.
		 * @param window the source window.
		 */
		void onFocus(GLFWWindow window);
		
		/**
		 * Called on a window blur event.
		 * @param window the source window.
		 */
		void onBlur(GLFWWindow window);
		
		/**
		 * Called on a window iconified event.
		 * @param window the source window.
		 */
		void onIconify(GLFWWindow window);
		
		/**
		 * Called on a window restore event.
		 * @param window the source window.
		 */
		void onRestore(GLFWWindow window);
		
		/**
		 * Called on a window maximized event.
		 * @param window the source window.
		 */
		void onMaximize(GLFWWindow window);
		
		/**
		 * Called when the mouse cursor enters the window.
		 * @param window the source window.
		 */
		void onMouseEntered(GLFWWindow window);
		
		/**
		 * Called when the mouse cursor exits the window.
		 * @param window the source window.
		 */
		void onMouseExited(GLFWWindow window);
		
		/**
		 * Called on a window position change event.
		 * @param window the source window.
		 * @param x the new x-coordinate (upper-left corner).
		 * @param y the new y-coordinate (upper-left corner).
		 */
		void onPositionChange(GLFWWindow window, int x, int y);
	
		/**
		 * Called on a window size change event.
		 * @param window the source window.
		 * @param width the new width.
		 * @param height the new height.
		 */
		void onSizeChange(GLFWWindow window, int width, int height);
	
		/**
		 * Called on a window frame buffer change event.
		 * @param window the source window.
		 * @param width the new width.
		 * @param height the new height.
		 */
		void onFramebufferChange(GLFWWindow window, int width, int height);
		
		/**
		 * Called on a window content scale change event.
		 * @param window the source window.
		 * @param x the x-scaling.
		 * @param y the y-scaling.
		 */
		void onContentScaleChange(GLFWWindow window, float x, float y);
		
	}
	
	/**
	 * An implementation of {@link WindowListener} that provides a blank, no-op 
	 * implementation so that only the desired functions need overriding.
	 */
	public static class WindowAdapter implements WindowListener
	{
		@Override
		public void onClose(GLFWWindow window)
		{
			// Do nothing.
		}

		@Override
		public void onRefresh(GLFWWindow window)
		{
			// Do nothing.
		}

		@Override
		public void onFocus(GLFWWindow window)
		{
			// Do nothing.
		}

		@Override
		public void onBlur(GLFWWindow window)
		{
			// Do nothing.
		}

		@Override
		public void onIconify(GLFWWindow window)
		{
			// Do nothing.
		}

		@Override
		public void onRestore(GLFWWindow window)
		{
			// Do nothing.
		}

		@Override
		public void onMaximize(GLFWWindow window)
		{
			// Do nothing.
		}

		@Override
		public void onMouseEntered(GLFWWindow window)
		{
			// Do nothing.
		}

		@Override
		public void onMouseExited(GLFWWindow window)
		{
			// Do nothing.
		}

		@Override
		public void onPositionChange(GLFWWindow window, int x, int y)
		{
			// Do nothing.
		}

		@Override
		public void onSizeChange(GLFWWindow window, int width, int height)
		{
			// Do nothing.
		}

		@Override
		public void onFramebufferChange(GLFWWindow window, int width, int height)
		{
			// Do nothing.
		}

		@Override
		public void onContentScaleChange(GLFWWindow window, float x, float y)
		{
			// Do nothing.
		}
	}

	/**
	 * An input event listener interface. 
	 */
	public static interface InputListener
	{
		/**
		 * Called when a key is pressed.
		 * @param window the source window.
		 * @param glfwKey the key code as a GLFW id.
		 * @param scanCode the system-specific scancode.
		 * @param modFlags the key modifier flags active.
		 */
		void onKeyPress(GLFWWindow window, int glfwKey, int scanCode, int modFlags);

		/**
		 * Called when a key is repeat-fired via being held during a press.
		 * @param window the source window.
		 * @param glfwKey the key code as a GLFW id.
		 * @param scanCode the system-specific scancode.
		 * @param modFlags the key modifier flags active.
		 */
		void onKeyRepeated(GLFWWindow window, int glfwKey, int scanCode, int modFlags);

		/**
		 * Called when a key is released.
		 * @param window the source window.
		 * @param glfwKey the key code as a GLFW id.
		 * @param scanCode the system-specific scancode.
		 * @param modFlags the key modifier flags active.
		 */
		void onKeyRelease(GLFWWindow window, int glfwKey, int scanCode, int modFlags);

		/**
		 * Called when a key character is typed (character).
		 * @param window the source window.
		 * @param c the key character typed.
		 */
		void onKeyTyped(GLFWWindow window, char c);

		/**
		 * Called when the mouse cursor is moved in the window.
		 * @param window the source window.
		 * @param x the mouse cursor position in the window, x-axis.
		 * @param y the mouse cursor position in the window, y-axis.
		 */
		void onMousePosition(GLFWWindow window, double x, double y);

		/**
		 * Called when a mouse button is pressed.
		 * @param window the source window.
		 * @param glfwButton the mouse button code as a GLFW id.
		 * @param modFlags the key modifier flags active.
		 */
		void onMouseButtonPress(GLFWWindow window, int glfwButton, int modFlags);

		/**
		 * Called when a mouse button is released.
		 * @param window the source window.
		 * @param glfwButton the mouse button code as a GLFW id.
		 * @param modFlags the key modifier flags active.
		 */
		void onMouseButtonRelease(GLFWWindow window, int glfwButton, int modFlags);
		
		/**
		 * Called when a scrolling action occurs.
		 * @param window the source window.
		 * @param x the scroll amount, x-axis.
		 * @param y the scroll amount, y-axis.
		 */
		void onScroll(GLFWWindow window, double x, double y);
		
	}
	
	/**
	 * An implementation of {@link InputListener} that provides a blank, no-op 
	 * implementation so that only the desired functions need overriding.
	 */
	public static class InputAdapter implements InputListener
	{

		@Override
		public void onKeyPress(GLFWWindow window, int glfwKey, int scanCode, int modFlags)
		{
			// Do nothing.
		}

		@Override
		public void onKeyRepeated(GLFWWindow window, int glfwKey, int scanCode, int modFlags)
		{
			// Do nothing.
		}

		@Override
		public void onKeyRelease(GLFWWindow window, int glfwKey, int scanCode, int modFlags)
		{
			// Do nothing.
		}

		@Override
		public void onKeyTyped(GLFWWindow window, char c)
		{
			// Do nothing.
		}

		@Override
		public void onMousePosition(GLFWWindow window, double x, double y)
		{
			// Do nothing.
		}

		@Override
		public void onMouseButtonPress(GLFWWindow window, int glfwButton, int modFlags)
		{
			// Do nothing.
		}

		@Override
		public void onMouseButtonRelease(GLFWWindow window, int glfwButton, int modFlags)
		{
			// Do nothing.
		}

		@Override
		public void onScroll(GLFWWindow window, double x, double y)
		{
			// Do nothing.
		}
	}
	
	/**
	 * A drag 'n drop event listener interface.
	 */
	@FunctionalInterface
	public static interface DropListener
	{
		/**
		 * Called when one or more files are dropped on the window.
		 * @param window the source window.
		 * @param files the file paths dropped.
		 */
		void onDrop(GLFWWindow window, File[] files);
	}

	/** 
	 * A single point. 
	 */
	public static class Point
	{
		public int x;
		public int y;
		
		private Point(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}
	
	/** 
	 * A single point, floating-point. 
	 */
	public static class PointF
	{
		public float x;
		public float y;
		
		private PointF(float x, float y)
		{
			this.x = x;
			this.y = y;
		}
	}
	
	/** 
	 * A dimension. 
	 */
	public static class Dimension
	{
		public int width;
		public int height;
		
		private Dimension(int width, int height)
		{
			this.width = width;
			this.height = height;
		}
	}
	
	/** 
	 * A rectangle bound. 
	 */
	public static class Rectangle
	{
		public int left;
		public int top;
		public int right;
		public int bottom;
		
		private Rectangle(int left, int top, int right, int bottom)
		{
			super();
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}

		public int getWidth()
		{
			return right - left;
		}

		public int getHeight()
		{
			return bottom - top;
		}
	}
	
	/**
	 * Window characteristic state that can be fetched outside of the main thread.
	 */
	public static class State
	{
		private int positionX;
		private int positionY;
		private int width;
		private int height;
		private int frameBufferWidth;
		private int frameBufferHeight;
		private float contentScaleX;
		private float contentScaleY;
		
		/**
		 * @return the position of the window, X-coordinate.
		 */
		public int getPositionX()
		{
			return positionX;
		}
		
		/**
		 * @return the position of the window, Y-coordinate.
		 */
		public int getPositionY()
		{
			return positionY;
		}
		
		/**
		 * @return the width of the window itself, in pixels.
		 */
		public int getWidth()
		{
			return width;
		}
		
		/**
		 * @return the height of the window itself, in pixels.
		 */
		public int getHeight()
		{
			return height;
		}
		
		/**
		 * @return the width of the window framebuffer, in framebuffer viewport pixels.
		 */
		public int getFrameBufferWidth()
		{
			return frameBufferWidth;
		}
		
		/**
		 * @return the height of the window framebuffer, in framebuffer viewport pixels.
		 */
		public int getFrameBufferHeight()
		{
			return frameBufferHeight;
		}
		
		/**
		 * @return the content scaling value, X-axis.
		 */
		public float getContentScaleX()
		{
			return contentScaleX;
		}
		
		/**
		 * @return the content scaling value, Y-axis.
		 */
		public float getContentScaleY()
		{
			return contentScaleY;
		}
		
	}
	
	// Set up structures.
	private GLFWWindow()
	{
		this.monitor = null;
		this.windowListeners = new ArrayList<>(4);
		this.inputListeners = new ArrayList<>(4);
		this.dropListeners = new ArrayList<>(4);
		this.state = new State();
	}
	
	// Accept and verify handle.
	private void acceptHandle(long handle)
	{
		this.handle = handle;
		if (this.handle == MemoryUtil.NULL)
			throw new GLFWException("Window could not be created!");
		this.allocated = true;
	}
	
	// Init window listeners.
	private void initListeners()
	{
		GLFW.glfwSetWindowCloseCallback(handle, (handle) -> 
		{
			for (int i = 0; i < windowListeners.size(); i++)
				windowListeners.get(i).onClose(this);
		});
		GLFW.glfwSetWindowRefreshCallback(handle, (handle) -> 
		{
			for (int i = 0; i < windowListeners.size(); i++)
				windowListeners.get(i).onRefresh(this);
		});
		GLFW.glfwSetWindowFocusCallback(handle, (handle, state) -> 
		{
			if (state)
			{
				for (int i = 0; i < windowListeners.size(); i++)
					windowListeners.get(i).onFocus(this);
			}
			else
			{
				for (int i = 0; i < windowListeners.size(); i++)
					windowListeners.get(i).onBlur(this);
			}
		});
		GLFW.glfwSetWindowIconifyCallback(handle, (handle, state) -> 
		{
			if (state)
			{
				for (int i = 0; i < windowListeners.size(); i++)
					windowListeners.get(i).onIconify(this);
			}
			else
			{
				for (int i = 0; i < windowListeners.size(); i++)
					windowListeners.get(i).onRestore(this);
			}
		});
		GLFW.glfwSetWindowMaximizeCallback(handle, (handle, state) -> 
		{
			if (state)
			{
				for (int i = 0; i < windowListeners.size(); i++)
					windowListeners.get(i).onMaximize(this);
			}
			else
			{
				for (int i = 0; i < windowListeners.size(); i++)
					windowListeners.get(i).onRestore(this);
			}
		});
		GLFW.glfwSetWindowPosCallback(handle, (handle, x, y) -> 
		{
			for (int i = 0; i < windowListeners.size(); i++)
				windowListeners.get(i).onPositionChange(this, x, y);
		});
		GLFW.glfwSetWindowSizeCallback(handle, (handle, width, height) -> 
		{
			for (int i = 0; i < windowListeners.size(); i++)
				windowListeners.get(i).onSizeChange(this, width, height);
		});
		GLFW.glfwSetWindowContentScaleCallback(handle, (handle, width, height) -> 
		{
			for (int i = 0; i < windowListeners.size(); i++)
				windowListeners.get(i).onContentScaleChange(this, width, height);
		});
		GLFW.glfwSetFramebufferSizeCallback(handle, (handle, x, y) -> 
		{
			for (int i = 0; i < windowListeners.size(); i++)
				windowListeners.get(i).onFramebufferChange(this, x, y);
		});
		GLFW.glfwSetCursorEnterCallback(handle, (handle, entered) ->
		{
			if (entered)
			{
				for (int i = 0; i < windowListeners.size(); i++)
					windowListeners.get(i).onMouseEntered(this);
			}
			else
			{
				for (int i = 0; i < windowListeners.size(); i++)
					windowListeners.get(i).onMouseExited(this);
			}
		});

		GLFW.glfwSetDropCallback(handle, (handle, count, namesPointer) ->
		{
			File[] files = new File[count];
			for (int i = 0; i < count; i++)
				files[i] = new File(GLFWDropCallback.getName(namesPointer, i));
			for (int i = 0; i < dropListeners.size(); i++)
				dropListeners.get(i).onDrop(this, files);			
		});

		GLFW.glfwSetKeyCallback(handle, (handle, glfwKey, scancode, actionId, modflags) -> 
		{
			switch (actionId)
			{
				case GLFW.GLFW_PRESS:
					for (int i = 0; i < inputListeners.size(); i++)
						inputListeners.get(i).onKeyPress(this, glfwKey, scancode, modflags);
					break;
				case GLFW.GLFW_REPEAT:
					for (int i = 0; i < inputListeners.size(); i++)
						inputListeners.get(i).onKeyRepeated(this, glfwKey, scancode, modflags);
					break;
				case GLFW.GLFW_RELEASE:
					for (int i = 0; i < inputListeners.size(); i++)
						inputListeners.get(i).onKeyRelease(this, glfwKey, scancode, modflags);
					break;
			}
		});
		GLFW.glfwSetCharCallback(handle, (handle, codepoint) -> 
		{
			for (int i = 0; i < inputListeners.size(); i++)
				inputListeners.get(i).onKeyTyped(this, (char)codepoint);
		});

		GLFW.glfwSetCursorPosCallback(handle, (handle, x, y) ->
		{
			for (int i = 0; i < inputListeners.size(); i++)
				inputListeners.get(i).onMousePosition(this, x, y);
		});
		GLFW.glfwSetMouseButtonCallback(handle, (handle, glfwButton, actionId, modFlags) -> 
		{
			switch (actionId)
			{
				case GLFW.GLFW_PRESS:
					for (int i = 0; i < inputListeners.size(); i++)
						inputListeners.get(i).onMouseButtonPress(this, glfwButton, modFlags);
					break;
				case GLFW.GLFW_RELEASE:
					for (int i = 0; i < inputListeners.size(); i++)
						inputListeners.get(i).onMouseButtonRelease(this, glfwButton, modFlags);
					break;
			}
		});
		GLFW.glfwSetScrollCallback(handle, (handle, x, y) -> 
		{
			for (int i = 0; i < inputListeners.size(); i++)
				inputListeners.get(i).onScroll(this, x, y);
		});
		
		addWindowListener(INTERNAL_ADAPTER);
	}
	
	/**
	 * Creates a new GLFW window.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param hints the hints to use for the window.
	 * @param title the window title.
	 * @param width the window's initial width.
	 * @param height the window's initial height.
	 * @throws GLFWException if the window could not be created.
	 * @see GLFWWindowHints
	 * @see GLFW#glfwCreateWindow(int, int, CharSequence, long, long)
	 */
	public GLFWWindow(GLFWWindowHints hints, String title, int width, int height) 
	{
		this();
		hints.setHints();
		acceptHandle(GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL));
		initListeners();
	}
		
	/**
	 * Creates a new GLFW window, sharing its OpenGL resources with another window. 
	 * <p><b>This must only be called from the main thread.</b>
	 * @param hints the hints to use for the window.
	 * @param sharedWindow the window to share OpenGL resources with.
	 * @param title the window title.
	 * @param width the window's initial width.
	 * @param height the window's initial height.
	 * @throws GLFWException if the window could not be created.
	 * @see GLFWWindowHints
	 * @see GLFW#glfwCreateWindow(int, int, CharSequence, long, long)
	 */
	public GLFWWindow(GLFWWindowHints hints, GLFWWindow sharedWindow, String title, int width, int height) 
	{
		this();
		hints.setHints();
		acceptHandle(GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, sharedWindow.getHandle()));
		initListeners();
	}
		
	/**
	 * Creates a new GLFW window.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param hints the hints to use for the window.
	 * @param monitor the monitor to use for fullscreen mode.
	 * @param title the window title.
	 * @param width the window's initial width.
	 * @param height the window's initial height.
	 * @throws GLFWException if the window could not be created.
	 * @see GLFWWindowHints
	 * @see GLFW#glfwCreateWindow(int, int, CharSequence, long, long)
	 */
	public GLFWWindow(GLFWWindowHints hints, GLFWMonitor monitor, String title, int width, int height) 
	{
		this();
		hints.setHints();
		acceptHandle(GLFW.glfwCreateWindow(width, height, title, monitor.getHandle(), MemoryUtil.NULL));
		initListeners();
	}
		
	/**
	 * Creates a new GLFW window, sharing its OpenGL resources with another window.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param hints the hints to use for the window.
	 * @param monitor the monitor to use for fullscreen mode.
	 * @param sharedWindow the window to share OpenGL resources with.
	 * @param title the window title.
	 * @param width the window's initial width.
	 * @param height the window's initial height.
	 * @throws GLFWException if the window could not be created.
	 * @see GLFWWindowHints
	 * @see GLFW#glfwCreateWindow(int, int, CharSequence, long, long)
	 */
	public GLFWWindow(GLFWWindowHints hints, GLFWMonitor monitor, GLFWWindow sharedWindow, String title, int width, int height) 
	{
		this();
		hints.setHints();
		acceptHandle(GLFW.glfwCreateWindow(width, height, title, monitor.getHandle(), sharedWindow.getHandle()));
		initListeners();
	}
		
	@Override
	public long getHandle()
	{
		return handle;
	}

	@Override
	public boolean isCreated()
	{
		return allocated;
	}

	@Override
	public void destroy()
	{
		if (allocated)
		{
			Callbacks.glfwFreeCallbacks(handle);
			GLFW.glfwDestroyWindow(handle);
			allocated = false;
		}
	}
	
	/**
	 * Adds a {@link WindowListener} to this window for listening for window events.
	 * Events are dispatched to listeners in the order added.
	 * This method is thread safe.
	 * @param listener the listener to add.
	 */
	public void addWindowListener(WindowListener listener)
	{
		synchronized (windowListeners)
		{
			windowListeners.add(listener);
		}
	}
	
	/**
	 * Removes a {@link WindowListener} from this window.
	 * This method is thread safe.
	 * @param listener the listener to remove.
	 */
	public void removeWindowListener(WindowListener listener)
	{
		synchronized (windowListeners)
		{
			windowListeners.remove(listener);
		}
	}
	
	/**
	 * Adds an {@link InputListener} to this window for listening for window events.
	 * Events are dispatched to listeners in the order added.
	 * This method is thread safe.
	 * @param listener the listener to add.
	 */
	public void addInputListener(InputListener listener)
	{
		synchronized (inputListeners)
		{
			inputListeners.add(listener);
		}
	}
	
	/**
	 * Removes an {@link InputListener} from this window.
	 * This method is thread safe.
	 * @param listener the listener to remove.
	 */
	public void removeInputListener(InputListener listener)
	{
		synchronized (inputListeners)
		{
			inputListeners.remove(listener);
		}
	}
	
	/**
	 * Adds a {@link DropListener} to this window for listening for window events.
	 * Events are dispatched to listeners in the order added.
	 * This method is thread safe.
	 * @param listener the listener to add.
	 */
	public void addDropListener(DropListener listener)
	{
		synchronized (dropListeners)
		{
			dropListeners.add(listener);
		}
	}
	
	/**
	 * Removes a {@link DropListener} from this window.
	 * This method is thread safe.
	 * @param listener the listener to remove.
	 */
	public void removeDropListener(DropListener listener)
	{
		synchronized (dropListeners)
		{
			dropListeners.remove(listener);
		}
	}
	
	/**
	 * Sets the target monitor for this window in fullscreen mode.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param monitor the desired monitor, or null to set windowed mode.
	 * @param xpos the desired x-coordinate of the upper-left corner of the content area.
	 * @param ypos the desired y-coordinate of the upper-left corner of the content area.
	 * @param width the desired width in screen coordinates of the content area or video mode.
	 * @param height the desired height in screen coordinates of the content area or video mode.
	 * @param refreshRate the desired refresh rate in Hz of the video mode, or {@link GLFWWindowHints#DONT_CARE}
	 */
	public void setMonitor(GLFWMonitor monitor, int xpos, int ypos, int width, int height, int refreshRate)
	{
		GLFW.glfwSetWindowMonitor(handle, monitor != null ? monitor.getHandle() : MemoryUtil.NULL, xpos, ypos, width, height, refreshRate);
		this.monitor = monitor;
	}

	/**
	 * Sets the current monitor for this window.
	 * @return the current monitor, or null if none.
	 */
	public GLFWMonitor getMonitor()
	{
		return monitor;
	}

	/**
	 * Sets this window's title.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param title the new title.
	 */
	public void setTitle(String title)
	{
		GLFW.glfwSetWindowTitle(handle, title);
	}
	
	/**
	 * Sets this window's icon. 
	 * Only works in non-macOS environments. 
	 * In macOS, the application bundle icons are used. 
	 * <p><b>This must only be called from the main thread.</b>
	 * @param icon the new icon.
	 */
	public void setIcon(BufferedImage icon)
	{
		try (
			MemoryStack stack = MemoryStack.stackPush(); 
			GLFWImage image = GLFWImage.malloc(); 
			GLFWImage.Buffer imageBuffer = GLFWImage.malloc(1)
		){
			ByteBuffer buffer = ByteBuffer.allocateDirect(icon.getWidth() * icon.getHeight() * 4);
			buffer.rewind();
			int[] argbRow = new int[icon.getWidth()];
			for (int y = 0; y < icon.getHeight(); y++)
			{
				icon.getRGB(0, y, argbRow.length, 1, argbRow, 0, 0);
				for (int x = 0; x < icon.getWidth(); x++)
				{
					// ARGB in, RGBA out.
					int argb = argbRow[x];
					buffer.put(0, (byte)((argb & 0x00ff0000) >>> 16));
					buffer.put(0, (byte)((argb & 0x0000ff00) >>> 8));
					buffer.put(0, (byte)((argb & 0x000000ff) >>> 0));
					buffer.put(0, (byte)((argb & 0xff000000) >>> 24));
				}
			}
			buffer.flip();
			image.set(icon.getWidth(), icon.getHeight(), buffer);
			imageBuffer.put(0, image);
			GLFW.glfwSetWindowIcon(handle, imageBuffer);
		}
	}
	
	/**
	 * Sets this window's cursor.
	 * @param cursor the cursor. 
	 * <p><b>This must only be called from the main thread.</b>
	 */
	public void setCursor(GLFWCursor cursor)
	{
		GLFW.glfwSetCursor(handle, cursor != null ? cursor.getHandle() : MemoryUtil.NULL);
	}
	
	/**
	 * Sets this window's cursor mode.
	 * @param cursorMode the cursor mode. 
	 * <p><b>This must only be called from the main thread.</b>
	 */
	public void setCursorMode(CursorMode cursorMode)
	{
		GLFW.glfwSetInputMode(handle, GLFW.GLFW_CURSOR, cursorMode.glfwVal);
	}
	
	/**
	 * Sets if the mouse motion on this window is raw, no acceleration applied.
	 * Only works if the cursor mode is {@link CursorMode#DISABLED}.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param enabled true to enable, false to disable.
	 */
	public void setRawMouseMotion(boolean enabled)
	{
		if (GLFW.glfwRawMouseMotionSupported())
			GLFW.glfwSetInputMode(handle, GLFW.GLFW_RAW_MOUSE_MOTION, glfwBoolean(enabled));
	}
	
	/**
	 * Sets if sticky keys is enabled.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param enabled true to enable, false to disable.
	 */
	public void setStickyKeys(boolean enabled)
	{
		GLFW.glfwSetInputMode(handle, GLFW.GLFW_STICKY_KEYS, glfwBoolean(enabled));
	}
	
	/**
	 * Sets if sticky mouse buttons is enabled.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param enabled true to enable, false to disable.
	 */
	public void setStickyMouseButtons(boolean enabled)
	{
		GLFW.glfwSetInputMode(handle, GLFW.GLFW_STICKY_MOUSE_BUTTONS, glfwBoolean(enabled));
	}
	
	/**
	 * Sets if the lock keys pass their modifier bits to the input system.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param enabled true to enable, false to disable.
	 */
	public void setLockKeyMods(boolean enabled)
	{
		GLFW.glfwSetInputMode(handle, GLFW.GLFW_LOCK_KEY_MODS, glfwBoolean(enabled));
	}
	
	/**
	 * Sets this window's position (upper-left coordinate) to center of the screen using its current size and a monitor.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param monitor the monitor to center on.
	 * @see #getSize()
	 * @see #setPosition(int, int)
	 */
	public void center(GLFWMonitor monitor)
	{
		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = monitor.getVideoMode();
		
		// Center window.
		Dimension dimension = getSize();
		setPosition(
			(vidmode.width() - dimension.width) / 2,
			(vidmode.height() - dimension.height) / 2
		);
	}

	/**
	 * Iconifies the window.
	 * Fires an event to listeners, but only when GLFW's Poll Events function is called. 
	 * <p><b>This must only be called from the main thread.</b>
	 */
	public void iconify()
	{
		GLFW.glfwIconifyWindow(handle);
	}

	/**
	 * Restores the window.
	 * Fires an event to listeners, but only when GLFW's Poll Events function is called. 
	 * <p><b>This must only be called from the main thread.</b>
	 */
	public void restore()
	{
		GLFW.glfwRestoreWindow(handle);
	}

	/**
	 * Maximizes the window.
	 * Fires an event to listeners, but only when GLFW's Poll Events function is called. 
	 * <p><b>This must only be called from the main thread.</b>
	 */
	public void maximize()
	{
		GLFW.glfwMaximizeWindow(handle);
	}

	/**
	 * Sets the visibility of the window.
	 * Fires an event to listeners, but only when GLFW's Poll Events function is called. 
	 * <p><b>This must only be called from the main thread.</b>
	 * @param state true to show, false to hide.
	 */
	public void setVisible(boolean state)
	{
		if (state)
			GLFW.glfwShowWindow(handle);
		else
			GLFW.glfwHideWindow(handle);
	}

	/**
	 * Requests focus on the window.
	 * Fires an event to listeners, but only when GLFW's Poll Events function is called. 
	 * <p><b>This must only be called from the main thread.</b>
	 */
	public void focus()
	{
		GLFW.glfwFocusWindow(handle);
	}

	/**
	 * Signals user attention from this window.
	 * Fires an event to listeners, but only when GLFW's Poll Events function is called. 
	 * <p><b>This must only be called from the main thread.</b>
	 */
	public void requestAttention()
	{
		GLFW.glfwRequestWindowAttention(handle);
	}

	/**
	 * Checks the closing flag of this window.
	 * <p>This can be called from any thread.
	 * @return true if set, false if not.
	 */
	public boolean isClosing()
	{
		return GLFW.glfwWindowShouldClose(handle);
	}

	/**
	 * Sets the closing flag of this window (aka "window should close").
	 * <p>This can be called from any thread.
	 * @param state the closing state.
	 */
	public void setClosing(boolean state)
	{
		GLFW.glfwSetWindowShouldClose(handle, state);
	}

	/**
	 * Sets this window's position (upper-left coordinate).
	 * <p><b>This must only be called from the main thread.</b>
	 * @param x the x-coordinate.
	 * @param y the y-coordinate.
	 * @see GLFW#glfwSetWindowPos(long, int, int)
	 */
	public void setPosition(int x, int y)
	{
		GLFW.glfwSetWindowPos(handle, x, y);
	}
	
	/**
	 * Gets this window's position (upper-left coordinate).
	 * <p><b>This must only be called from the main thread.</b>
	 * @return a Point representing the current window position.
	 */
	public Point getPosition()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer buf1 = stack.mallocInt(1);
			IntBuffer buf2 = stack.mallocInt(1);
			GLFW.glfwGetWindowPos(handle, buf1, buf2);
			return new Point(buf1.get(0), buf2.get(0));
		}
	}

	/**
	 * Sets this window's size.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param width the new width in pixels.
	 * @param height the new height in pixels.
	 */
	public void setSize(int width, int height)
	{
		GLFW.glfwSetWindowPos(handle, width, height);
	}
	
	/**
	 * Gets this window's size.
	 * <p><b>This must only be called from the main thread.</b>
	 * @return a Dimension representing the current window size/dimensions.
	 */
	public Dimension getSize()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer buf1 = stack.mallocInt(1);
			IntBuffer buf2 = stack.mallocInt(1);
			GLFW.glfwGetWindowSize(handle, buf1, buf2);
			return new Dimension(buf1.get(0), buf2.get(0));
		}
	}
	
	/**
	 * Sets the size limits of the content area of the specified window.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param minwidth the minimum width in screen coordinates of the content area, or {@link GLFWWindowHints#DONT_CARE}
	 * @param minheight the minimum height in screen coordinates of the content area, or {@link GLFWWindowHints#DONT_CARE}
	 * @param maxwidth the maximum width in screen coordinates of the content area, or {@link GLFWWindowHints#DONT_CARE}
	 * @param maxheight the maximum height in screen coordinates of the content area, or {@link GLFWWindowHints#DONT_CARE}
	 */
	public void setSizeLimits(int minwidth, int minheight, int maxwidth, int maxheight)
	{
		GLFW.glfwSetWindowSizeLimits(handle, minwidth, minheight, maxwidth, maxheight);
	}

	/**
	 * Sets the window opacity.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param scalar the new opacity scalar.
	 */
	public void setOpacity(float scalar)
	{
		GLFW.glfwSetWindowOpacity(handle, scalar);
	}
	
	/**
	 * Gets the window opacity.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param scalar the new opacity scalar.
	 * @return the window opacity.
	 */
	public float getOpacity(float scalar)
	{
		return GLFW.glfwGetWindowOpacity(handle);
	}
	
	/**
	 * Sets this window's aspect ratio limits.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param numer the numerator of the desired aspect ratio, or {@link GLFWWindowHints#DONT_CARE}
	 * @param denom the denominator of the desired aspect ratio, or {@link GLFWWindowHints#DONT_CARE}
	 */
	public void setAspectRatio(int numer, int denom) 
	{
		GLFW.glfwSetWindowAspectRatio(handle, numer, denom);
	}
	
	/**
	 * Sets if this window is decorated (has shell bordering).
	 * <p><b>This must only be called from the main thread.</b>
	 * @param value true if so, false if not.
	 */
	public void setDecorated(boolean value) 
	{
		GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_DECORATED, glfwBoolean(value));
	}

	/**
	 * Sets if this window is resizable (via user, not program).
	 * <p><b>This must only be called from the main thread.</b>
	 * @param value true if so, false if not.
	 */
	public void setResizable(boolean value) 
	{
		GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_RESIZABLE, glfwBoolean(value));
	}

	/**
	 * Sets if this window is iconified on creation.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param value true if so, false if not.
	 */
	public void setAutoIconified(boolean value)
	{
		GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_AUTO_ICONIFY, glfwBoolean(value));
	}
	
	/**
	 * Sets if this window is floating, or rather, "always on top".
	 * <p><b>This must only be called from the main thread.</b>
	 * @param value true if so, false if not.
	 */
	public void setFloating(boolean value)
	{
		GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_FLOATING, glfwBoolean(value));
	}
	
	/**
	 * Sets if this window requests focus when shown.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param value true if so, false if not.
	 */
	public void setFocusOnShow(boolean value)
	{
		GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_FOCUS_ON_SHOW, glfwBoolean(value));
	}

	/**
	 * Gets the frame buffer size.
	 * <p><b>This must only be called from the main thread.</b>
	 * @return a Dimension that represents the framebuffer size.
	 */
	public Dimension getFramebufferSize()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer buf1 = stack.mallocInt(1);
			IntBuffer buf2 = stack.mallocInt(1);
			GLFW.glfwGetFramebufferSize(handle, buf1, buf2);
			return new Dimension(buf1.get(0), buf2.get(0));
		}
	}
	
	/**
	 * Gets the window's frame size.
	 * <p><b>This must only be called from the main thread.</b>
	 * @return a Rectangle that represents the framebuffer size (x, y, width, height).
	 */
	public Rectangle getFrameSize()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer left = stack.mallocInt(1);
			IntBuffer top = stack.mallocInt(1);
			IntBuffer right = stack.mallocInt(1);
			IntBuffer bottom = stack.mallocInt(1);
			GLFW.glfwGetWindowFrameSize(handle, left, top, right, bottom);
			return new Rectangle(left.get(0), top.get(0), right.get(0), bottom.get(0));
		}
	}
	
	/**
	 * Gets the content scale for the specified window. 
	 * The content scale is the ratio between the current DPI and the platform's default DPI.
	 * Depends on monitor.
	 * @return the scalars.
	 */
	public PointF getContentScale()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf1 = stack.mallocFloat(1);
			FloatBuffer fbuf2 = stack.mallocFloat(1);
			GLFW.glfwGetWindowContentScale(handle, fbuf1, fbuf2);
			return new PointF(fbuf1.get(0), fbuf2.get(0));
		}
	}
	
	/**
	 * Gets a window characteristic state that can be fetched outside of the main polling thread.
	 * The reference returned is the only instance - the values on this object can change frequently.
	 * @return this window's characteristics, set from the last event poll.
	 */
	public State getState()
	{
		return state;
	}
	
	/**
	 * Swaps the front and back buffer on the window, redrawing its contents to the foreground.
	 * If a swap interval is set, the system may wait for a set of vertical blank 
	 * signals before this happens, and will block until they occur.
	 * <p>This is only necessary for OpenGL/GLES contexts.
	 * <p>This can be called from any thread.
	 * @see GLFWContext#setSwapInterval(int)
	 */
	public void swapBuffers()
	{
		GLFW.glfwSwapBuffers(handle);
	}
	
	// Convert Java boolean to GLFW boolean.
	private static int glfwBoolean(boolean value)
	{
		return value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE;
	}

}

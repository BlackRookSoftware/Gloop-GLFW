package com.blackrook.gloop.glfw;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Vector;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * A GLFW window instance.
 * @author Matthew Tropiano
 */
public class GLFWWindow extends GLFWHandle
{
	/** The memory address. */
	private long handle;
	/** Is this allocated? */
	private boolean allocated;
	
	/** List of event listeners. */
	private List<Listener> listeners;

	/**
	 * Windows hints for the next window created.
	 */
	public static class Hints
	{
		public enum ClientAPI
		{
			OPENGL_API(GLFW.GLFW_OPENGL_API),
			OPENGL_ES_API(GLFW.GLFW_OPENGL_ES_API),
			NO_API(GLFW.GLFW_NO_API);
			
			final int glfwVal;
			private ClientAPI(int value) {this.glfwVal = value;}
		}

		public enum ContextCreationAPI
		{
			NATIVE_CONTEXT_API(GLFW.GLFW_NATIVE_CONTEXT_API),
			EGL_CONTEXT_API(GLFW.GLFW_EGL_CONTEXT_API),
			OSMESA_CONTEXT_API(GLFW.GLFW_OSMESA_CONTEXT_API);
		
			final int glfwVal;
			private ContextCreationAPI(int value) {this.glfwVal = value;}
		}

		public enum ContextRobustness
		{
			NO_ROBUSTNESS(GLFW.GLFW_NO_ROBUSTNESS),
			NO_RESET_NOTIFICATION(GLFW.GLFW_NO_RESET_NOTIFICATION),
			LOSE_CONTEXT_ON_RESET(GLFW.GLFW_LOSE_CONTEXT_ON_RESET);
		
			final int glfwVal;
			private ContextRobustness(int value) {this.glfwVal = value;}
		}

		public enum ContextReleaseBehavior
		{
			ANY_RELEASE_BEHAVIOR(GLFW.GLFW_ANY_RELEASE_BEHAVIOR),
			RELEASE_BEHAVIOR_FLUSH(GLFW.GLFW_RELEASE_BEHAVIOR_FLUSH),
			RELEASE_BEHAVIOR_NONE(GLFW.GLFW_RELEASE_BEHAVIOR_NONE);
		
			final int glfwVal;
			private ContextReleaseBehavior(int value) {this.glfwVal = value;}
		}

		public enum OpenGLProfile
		{
			ANY_PROFILE(GLFW.GLFW_OPENGL_ANY_PROFILE),
			CORE_PROFILE(GLFW.GLFW_OPENGL_CORE_PROFILE),
			COMPAT_PROFILE(GLFW.GLFW_OPENGL_COMPAT_PROFILE);
		
			final int glfwVal;
			private OpenGLProfile(int value) {this.glfwVal = value;}
		}

		/** The Don't Care constant. */
		public static final int DONT_CARE = -1;
		
		/**
		 * Resets all hints to default:
		 * 
		 */
		public static void reset()
		{
			GLFW.glfwDefaultWindowHints();
		}

		/**
		 * Sets if the next created window is resizable (manually, not via methods).
		 * @param value true if so, false if not.
		 */
		public static void setResizable(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}

		/**
		 * Sets if the next created window is visible on creation.
		 * @param value true if so, false if not.
		 */
		public static void setVisible(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}
		
		/**
		 * Sets if the next created window is decorated on creation.
		 * @param value true if so, false if not.
		 */
		public static void setDecorated(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}
		
		/**
		 * Sets if the next created window gains immediate focus on creation.
		 * @param value true if so, false if not.
		 */
		public static void setFocused(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_FOCUSED, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}
		
		/**
		 * Sets if the next created window starts iconified on creation.
		 * @param value true if so, false if not.
		 */
		public static void setAutoIconified(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_AUTO_ICONIFY, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}
		
		/**
		 * Sets if the next created window starts floating, or rather, "always on top".
		 * @param value true if so, false if not.
		 */
		public static void setFloating(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_FLOATING, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}
		
		/**
		 * Sets if the next created window starts maximized on creation.
		 * @param value true if so, false if not.
		 */
		public static void setMaximized(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}
		
		/**
		 * Sets if the next created window starts with the mouse cursor centered over it (fullscreen only).
		 * @param value true if so, false if not.
		 */
		public static void setCenteredCursor(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_CENTER_CURSOR, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}
		
		/**
		 * Sets if the next created window can be transparent (not supported everywhere).
		 * @param value true if so, false if not.
		 */
		public static void setTransparent(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}
		
		/**
		 * Sets if the next created window requests focus when shown.
		 * @param value true if so, false if not.
		 */
		public static void setFocusOnShow(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_FOCUS_ON_SHOW, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}
		
		/**
		 * Sets if the next created window resizes proportionally to each monitor it touches.
		 * @param value true if so, false if not.
		 */
		public static void setScaleToMonitor(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_SCALE_TO_MONITOR, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}
		
		/**
		 * Sets the next created window's framebuffer's red color bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
		 */
		public static void setRedBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, value);
		}
		
		/**
		 * Sets the next created window's framebuffer's green color bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
		 */
		public static void setGreenBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, value);
		}
		
		/**
		 * Sets the next created window's framebuffer's blue color bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
		 */
		public static void setBlueBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, value);
		}
		
		/**
		 * Sets the next created window's framebuffer's alpha bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
		 */
		public static void setAlphaBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_ALPHA_BITS, value);
		}
		
		/**
		 * Sets the next created window's depth buffer bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
		 */
		public static void setDepthBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_DEPTH_BITS, value);
		}
		
		/**
		 * Sets the next created window's stencil buffer bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
		 */
		public static void setStencilBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, value);
		}
		
		/**
		 * Sets the next created window's accumulation framebuffer's red color bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
		 */
		public static void setAccumRedBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_RED_BITS, value);
		}
		
		/**
		 * Sets the next created window's accumulation framebuffer's green color bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
		 */
		public static void setAccumGreenBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_GREEN_BITS, value);
		}
		
		/**
		 * Sets the next created window's accumulation framebuffer's blue color bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
		 */
		public static void setAccumBlueBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_BLUE_BITS, value);
		}
		
		/**
		 * Sets the next created window's accumulation framebuffer's alpha bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
		 */
		public static void setAccumAlphaBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_ALPHA_BITS, value);
		}
		
		/**
		 * Sets the next created window's auxiliary buffers.
		 * @param value 0 to {@link Integer#MAX_VALUE}.
		 */
		public static void setAuxBuffers(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_AUX_BUFFERS, value);
		}
		
		/**
		 * Sets the next created window's multisample samples.
		 * @param value 0 to {@link Integer#MAX_VALUE}.
		 */
		public static void setSamples(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, value);
		}
		
		/**
		 * Sets the next created window's refresh rate.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
		 */
		public static void setRefreshRate(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, value);
		}
		
		/**
		 * Sets if the next created window is in stereo mode.
		 * @param value true if so, false if not.
		 */
		public static void setRefreshRate(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_STEREO, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}

		/**
		 * Sets if the next created window is SRGB Capable.
		 * @param value true if so, false if not.
		 */
		public static void setSRGBCapable(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_SRGB_CAPABLE, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}

		/**
		 * Sets if the next created window is double-buffered.
		 * @param value true if so, false if not.
		 */
		public static void setDoubleBuffered(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}

		/**
		 * Sets which client API to create the context for. Hard constraint. 
		 * @param clientAPI the API type.
		 */
		public static void setClientAPI(ClientAPI clientAPI)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, clientAPI.glfwVal);
		}
		
		/**
		 * Sets which context creation API to use to create the context. Hard constraint.
		 * Only used if the client API is set.
		 * @param contextCreationAPI the context creation type.
		 */
		public static void setContextCreationAPI(ContextCreationAPI contextCreationAPI)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_CREATION_API, contextCreationAPI.glfwVal);
		}
		
		/**
		 * Sets the OpenGL version.
		 * @param major the major version number.
		 * @param minor the minor version number.
		 */
		public static void setContextVersion(int major, int minor)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, major);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, minor);
		}
		
		/**
		 * Sets the robustness strategy to be used by the context.
		 * @param contextRobustness the robustness strategy.
		 */
		public static void setContextRobustness(ContextRobustness contextRobustness)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_ROBUSTNESS, contextRobustness.glfwVal);
		}
		
		/**
		 * Sets the release behavior to be used by the context.
		 * @param contextReleaseBehavior the release behavior.
		 */
		public static void setContextReleaseBehavior(ContextReleaseBehavior contextReleaseBehavior)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_RELEASE_BEHAVIOR, contextReleaseBehavior.glfwVal);
		}
		
		/**
		 * Sets whether errors should be generated by the context.
		 * @param value true if so, false if not.
		 */
		public static void setContextNoError(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_NO_ERROR, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}

		/**
		 * Sets whether the OpenGL context should be forward-compatible, 
		 * i.e. one where all functionality deprecated in the requested version of OpenGL is removed. 
		 * This must only be used if the requested OpenGL version is 3.0 or above. 
		 * If OpenGL ES is requested, this hint is ignored. 
		 * @param value true if so, false if not.
		 */
		public static void setOpenGLForwardCompatibility(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}

		/**
		 * Sets whether to create a debug OpenGL context, 
		 * which may have additional error and performance issue reporting functionality. 
		 * If OpenGL ES is requested, this hint is ignored. 
		 * @param value true if so, false if not.
		 */
		public static void setOpenGLDebugContext(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}

		/**
		 * Sets which OpenGL profile to create the context for.
 		 * If requesting an OpenGL version below 3.2, {@link OpenGLProfile#ANY_PROFILE} must be used. 
 		 * If OpenGL ES is requested, this hint is ignored. 
 		 * @param openGLProfile the profile to create.
		 */
		public static void setOpenGLProfile(OpenGLProfile openGLProfile)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, openGLProfile.glfwVal);
		}

		/**
		 * Sets whether to use full resolution framebuffers on Retina displays. 
		 * This is ignored on non-macOS platforms.
		 * @param value true if so, false if not.
		 */
		public static void setCocoaRetinaFrameBuffer(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_COCOA_RETINA_FRAMEBUFFER, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}

		/**
		 * Specifies whether to enable Automatic Graphics Switching, 
		 * i.e. to allow the system to choose the integrated GPU for the OpenGL 
		 * context and move it between GPUs if necessary or whether to force it 
		 * to always run on the discrete GPU. This only affects systems with both 
		 * integrated and discrete GPUs.
		 * This is ignored on non-macOS platforms.
		 * @param value true if so, false if not.
		 */
		public static void setCocoaGraphicsSwitching(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_COCOA_GRAPHICS_SWITCHING, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}

	}
	
	// Set up structures.
	private GLFWWindow()
	{
		this.listeners = new Vector<>(4);
	}
	
	// Init listeners.
	private void initListeners()
	{
		GLFW.glfwSetWindowCloseCallback(handle, (handle) -> 
		{
			for (int i = 0; i < listeners.size(); i++)
				listeners.get(i).onClose(this);
		});
		GLFW.glfwSetWindowRefreshCallback(handle, (handle) -> 
		{
			for (int i = 0; i < listeners.size(); i++)
				listeners.get(i).onRefresh(this);
		});
		GLFW.glfwSetWindowFocusCallback(handle, (handle, state) -> 
		{
			if (state)
			{
				for (int i = 0; i < listeners.size(); i++)
					listeners.get(i).onFocus(this);
			}
			else
			{
				for (int i = 0; i < listeners.size(); i++)
					listeners.get(i).onBlur(this);
			}
		});
		GLFW.glfwSetWindowIconifyCallback(handle, (handle, state) -> 
		{
			if (state)
			{
				for (int i = 0; i < listeners.size(); i++)
					listeners.get(i).onIconify(this);
			}
			else
			{
				for (int i = 0; i < listeners.size(); i++)
					listeners.get(i).onRestore(this);
			}
		});
		GLFW.glfwSetWindowMaximizeCallback(handle, (handle, state) -> 
		{
			if (state)
			{
				for (int i = 0; i < listeners.size(); i++)
					listeners.get(i).onMaximize(this);
			}
			else
			{
				for (int i = 0; i < listeners.size(); i++)
					listeners.get(i).onRestore(this);
			}
		});
		GLFW.glfwSetWindowPosCallback(handle, (handle, x, y) -> 
		{
			for (int i = 0; i < listeners.size(); i++)
				listeners.get(i).onPositionChange(this, x, y);
		});
		GLFW.glfwSetWindowSizeCallback(handle, (handle, width, height) -> 
		{
			for (int i = 0; i < listeners.size(); i++)
				listeners.get(i).onSizeChange(this, width, height);
		});
		GLFW.glfwSetWindowContentScaleCallback(handle, (handle, width, height) -> 
		{
			for (int i = 0; i < listeners.size(); i++)
				listeners.get(i).onContentScaleChange(this, width, height);
		});
		GLFW.glfwSetFramebufferSizeCallback(handle, (handle, x, y) -> 
		{
			for (int i = 0; i < listeners.size(); i++)
				listeners.get(i).onFramebufferChange(this, x, y);
		});
	}
	
	/**
	 * Creates a new GLFW window.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param title the window title.
	 * @param width the window's initial width.
	 * @param height the window's initial height.
	 * @see Hints
	 * @See {@link GLFW#glfwCreateWindow(int, int, CharSequence, long, long)}
	 */
	public GLFWWindow(String title, int width, int height) 
	{
		this();
		GLFWContext.init(); // init GLFW if not already (only happens once).
		this.handle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
		this.allocated = true;
		initListeners();
	}
		
	/**
	 * Creates a new GLFW window.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param sharedWindow the window to share OpenGL resources with.
	 * @param title the window title.
	 * @param width the window's initial width.
	 * @param height the window's initial height.
	 * @see Hints
	 * @See {@link GLFW#glfwCreateWindow(int, int, CharSequence, long, long)}
	 */
	public GLFWWindow(GLFWWindow sharedWindow, String title, int width, int height) 
	{
		this();
		GLFWContext.init(); // init GLFW if not already (only happens once).
		this.handle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, sharedWindow.getHandle());
		this.allocated = true;
		initListeners();
	}
		
	/**
	 * Creates a new GLFW window.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param monitor the monitor to use for fullscreen mode.
	 * @param title the window title.
	 * @param width the window's initial width.
	 * @param height the window's initial height.
	 * @see Hints
	 * @See {@link GLFW#glfwCreateWindow(int, int, CharSequence, long, long)}
	 */
	public GLFWWindow(GLFWMonitor monitor, String title, int width, int height) 
	{
		this();
		GLFWContext.init(); // init GLFW if not already (only happens once).
		this.handle = GLFW.glfwCreateWindow(width, height, title, monitor.getHandle(), MemoryUtil.NULL);
		this.allocated = true;
		initListeners();
	}
		
	/**
	 * Creates a new GLFW window.
	 * <p><b>This must only be called from the main thread.</b>
	 * @param monitor the monitor to use for fullscreen mode.
	 * @param sharedWindow the window to share OpenGL resources with.
	 * @param title the window title.
	 * @param width the window's initial width.
	 * @param height the window's initial height.
	 * @see Hints
	 * @See {@link GLFW#glfwCreateWindow(int, int, CharSequence, long, long)}
	 */
	public GLFWWindow(GLFWMonitor monitor, GLFWWindow sharedWindow, String title, int width, int height) 
	{
		this();
		GLFWContext.init(); // init GLFW if not already (only happens once).
		this.handle = GLFW.glfwCreateWindow(width, height, title, monitor.getHandle(), sharedWindow.getHandle());
		this.allocated = true;
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
			GLFW.glfwDestroyWindow(handle);
			allocated = false;
		}
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
	 * <p><b>This must only be called from the main thread.</b>
	 * @param icon the new title.
	 * @see GLFW#glfwSetWindowIcon(long, Buffer)
	 */
	public void setIcon(Image icon)
	{
		// TODO: Support this.
		// glfwSetWindowIcon(long, Buffer)
		throw new UnsupportedOperationException("Not supported yet.");
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
	 * Sets the closing flag of this window.
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
	
	/*
		TODO: All of this stuff.
		
		glfwSetWindowOpacity(long, float)
		glfwSetWindowSizeLimits(long, int, int, int, int)
		glfwSetWindowAspectRatio(long, int, int)
		glfwSetWindowAttrib(long, int, int)

		glfwGetWindowOpacity(long)
		glfwGetFramebufferSize(long, IntBuffer, IntBuffer)
		glfwGetWindowFrameSize(long, IntBuffer, IntBuffer, IntBuffer, IntBuffer)
		glfwGetWindowAttrib(long, int)

		glfwSetWindowMonitor(long, long, int, int, int, int, int)
		glfwSetWindowUserPointer(long, long)
		
		glfwGetWindowMonitor(long)
		glfwGetWindowUserPointer(long)

		glfwGetWindowContentScale(long, FloatBuffer, FloatBuffer)
	 */

	/**
	 * A window event listener interface. 
	 */
	public static interface Listener
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
		 * @param width the new width.
		 * @param height the new height.
		 * @param window the source window.
		 */
		void onFramebufferChange(GLFWWindow window, int width, int height);
		
		/**
		 * Called on a window content scale change event.
		 * @param width the new width.
		 * @param x the x-scaling.
		 * @param y the y-scaling.
		 */
		void onContentScaleChange(GLFWWindow window, float x, float y);
		
	}
	
}

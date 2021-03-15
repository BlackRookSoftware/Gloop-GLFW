package com.blackrook.gloop.glfw;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
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
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.blackrook.gloop.glfw.struct.Pair2F;

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
	/** Assigned monitor. */
	private GLFWMonitor monitor;
	
	/** List of window event listeners. */
	private List<WindowListener> windowListeners;
	/** List of input event listeners. */
	private List<InputListener> inputListeners;
	/** List of file drop event listeners. */
	private List<DropListener> dropListeners;

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
	 * A drag 'n drop event listener interface. 
	 */
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
			GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, glfwBoolean(value));
		}

		/**
		 * Sets if the next created window is visible on creation.
		 * @param value true if so, false if not.
		 */
		public static void setVisible(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, glfwBoolean(value));
		}
		
		/**
		 * Sets if the next created window is decorated on creation.
		 * @param value true if so, false if not.
		 */
		public static void setDecorated(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, glfwBoolean(value));
		}
		
		/**
		 * Sets if the next created window gains immediate focus on creation.
		 * @param value true if so, false if not.
		 */
		public static void setFocused(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_FOCUSED, glfwBoolean(value));
		}
		
		/**
		 * Sets if the next created window starts iconified on creation.
		 * @param value true if so, false if not.
		 */
		public static void setAutoIconified(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_AUTO_ICONIFY, glfwBoolean(value));
		}
		
		/**
		 * Sets if the next created window starts floating, or rather, "always on top".
		 * @param value true if so, false if not.
		 */
		public static void setFloating(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_FLOATING, glfwBoolean(value));
		}
		
		/**
		 * Sets if the next created window starts maximized on creation.
		 * @param value true if so, false if not.
		 */
		public static void setMaximized(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, glfwBoolean(value));
		}
		
		/**
		 * Sets if the next created window starts with the mouse cursor centered over it (fullscreen only).
		 * @param value true if so, false if not.
		 */
		public static void setCenteredCursor(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_CENTER_CURSOR, glfwBoolean(value));
		}
		
		/**
		 * Sets if the next created window can be transparent (not supported everywhere).
		 * @param value true if so, false if not.
		 */
		public static void setTransparent(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, glfwBoolean(value));
		}
		
		/**
		 * Sets if the next created window requests focus when shown.
		 * @param value true if so, false if not.
		 */
		public static void setFocusOnShow(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_FOCUS_ON_SHOW, glfwBoolean(value));
		}
		
		/**
		 * Sets if the next created window resizes proportionally to each monitor it touches.
		 * @param value true if so, false if not.
		 */
		public static void setScaleToMonitor(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_SCALE_TO_MONITOR, glfwBoolean(value));
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
			GLFW.glfwWindowHint(GLFW.GLFW_STEREO, glfwBoolean(value));
		}

		/**
		 * Sets if the next created window is SRGB Capable.
		 * @param value true if so, false if not.
		 */
		public static void setSRGBCapable(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_SRGB_CAPABLE, glfwBoolean(value));
		}

		/**
		 * Sets if the next created window is double-buffered.
		 * @param value true if so, false if not.
		 */
		public static void setDoubleBuffered(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, glfwBoolean(value));
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
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_NO_ERROR, glfwBoolean(value));
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
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, glfwBoolean(value));
		}

		/**
		 * Sets whether to create a debug OpenGL context, 
		 * which may have additional error and performance issue reporting functionality. 
		 * If OpenGL ES is requested, this hint is ignored. 
		 * @param value true if so, false if not.
		 */
		public static void setOpenGLDebugContext(boolean value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, glfwBoolean(value));
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
			GLFW.glfwWindowHint(GLFW.GLFW_COCOA_RETINA_FRAMEBUFFER, glfwBoolean(value));
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
			GLFW.glfwWindowHint(GLFW.GLFW_COCOA_GRAPHICS_SWITCHING, glfwBoolean(value));
		}

	}
	
	// Set up structures.
	private GLFWWindow()
	{
		this.monitor = null;
		this.windowListeners = new ArrayList<>(4);
		this.inputListeners = new ArrayList<>(4);
		this.dropListeners = new ArrayList<>(4);
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
			Callbacks.glfwFreeCallbacks(handle);
			GLFW.glfwDestroyWindow(handle);
			allocated = false;
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
	 * @param refreshRate the desired refresh rate in Hz of the video mode, or {@link Hints#DONT_CARE}
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
	 * @param minwidth the minimum width in screen coordinates of the content area, or {@link Hints#DONT_CARE}
	 * @param minheight the minimum height in screen coordinates of the content area, or {@link Hints#DONT_CARE}
	 * @param maxwidth the maximum width in screen coordinates of the content area, or {@link Hints#DONT_CARE}
	 * @param maxheight the maximum height in screen coordinates of the content area, or {@link Hints#DONT_CARE}
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
     * @param numer the numerator of the desired aspect ratio, or {@link Hints#DONT_CARE}
     * @param denom the denominator of the desired aspect ratio, or {@link Hints#DONT_CARE}
	 */
	public void setAspectRatio(int numer, int denom) 
	{
		GLFW.glfwSetWindowAspectRatio(handle, 0, 0);
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
			return new Rectangle(left.get(0), top.get(0), right.get(0) - left.get(0), bottom.get(0) - top.get(0));
		}
	}
	
	/**
	 * Gets the content scale for the specified window. 
	 * The content scale is the ratio between the current DPI and the platform's default DPI.
	 * Depends on monitor.
	 * @return the scalars.
	 */
	public Pair2F getContentScale()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf1 = stack.mallocFloat(1);
			FloatBuffer fbuf2 = stack.mallocFloat(1);
			GLFW.glfwGetWindowContentScale(handle, fbuf1, fbuf2);
			return new Pair2F(fbuf1.get(0), fbuf2.get(0));
		}
	}
	
	/**
	 * Swaps the front and back buffer on the window, redrawing its contents to the foreground.
	 * If a swap interval is set, the system may wait for a set of vertical blank 
	 * signals before this happens, and will block until they occur.
	 * <p>This is only necessary for OpenGL/GLES contexts.
	 * <p>This can be called from any thread.
	 * @see GLFWWindow#setSwapInterval(int)
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

}

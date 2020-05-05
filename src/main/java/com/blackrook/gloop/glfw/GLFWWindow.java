package com.blackrook.gloop.glfw;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

/**
 * A GLFW window instance.
 * @author Matthew Tropiano
 */
public class GLFWWindow extends GLFWHandle
{
	/** The window memory address. */
	private long handle;
	/** Is this allocated? */
	private boolean allocated;

	/**
	 * Windows hints for the next window created.
	 */
	public static class Hints
	{
		/**
		 * Resets all hints to default.
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
		 * @param value 0 to {@link Integer#MAX_VALUE}, or -1 for "Don't Care".
		 */
		public static void setRedBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, value);
		}
		
		/**
		 * Sets the next created window's framebuffer's green color bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or -1 for "Don't Care".
		 */
		public static void setGreenBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, value);
		}
		
		/**
		 * Sets the next created window's framebuffer's blue color bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or -1 for "Don't Care".
		 */
		public static void setBlueBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, value);
		}
		
		/**
		 * Sets the next created window's framebuffer's alpha bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or -1 for "Don't Care".
		 */
		public static void setAlphaBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_ALPHA_BITS, value);
		}
		
		/**
		 * Sets the next created window's depth buffer bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or -1 for "Don't Care".
		 */
		public static void setDepthBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_DEPTH_BITS, value);
		}
		
		/**
		 * Sets the next created window's stencil buffer bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or -1 for "Don't Care".
		 */
		public static void setStencilBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, value);
		}
		
		/**
		 * Sets the next created window's accumulation framebuffer's red color bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or -1 for "Don't Care".
		 */
		public static void setAccumRedBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_RED_BITS, value);
		}
		
		/**
		 * Sets the next created window's accumulation framebuffer's green color bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or -1 for "Don't Care".
		 */
		public static void setAccumGreenBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_GREEN_BITS, value);
		}
		
		/**
		 * Sets the next created window's accumulation framebuffer's blue color bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or -1 for "Don't Care".
		 */
		public static void setAccumBlueBits(int value)
		{
			GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_BLUE_BITS, value);
		}
		
		/**
		 * Sets the next created window's accumulation framebuffer's alpha bits.
		 * @param value 0 to {@link Integer#MAX_VALUE}, or -1 for "Don't Care".
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
		 * @param value 0 to {@link Integer#MAX_VALUE}, or -1 for "Don't Care".
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

		// TODO: Finish.
		
		/**
		 * Window hints.
	     * <table class=striped>
	     * <tr><th>Name</th><th>Default value</th><th>Supported values</th></tr>
	     * <tr><td>GLFW_CLIENT_API CLIENT_API</td><td>GLFW_OPENGL_API OPENGL_API</td><td>GLFW_NO_API NO_API GLFW_OPENGL_API OPENGL_API GLFW_OPENGL_ES_API OPENGL_ES_API</td></tr>
	     * <tr><td>GLFW_CONTEXT_CREATION_API CONTEXT_CREATION_API</td><td>GLFW_NATIVE_CONTEXT_API NATIVE_CONTEXT_API</td><td>GLFW_NATIVE_CONTEXT_API NATIVE_CONTEXT_API GLFW_EGL_CONTEXT_API EGL_CONTEXT_API GLFW_OSMESA_CONTEXT_API OSMESA_CONTEXT_API</td></tr>
	     * <tr><td>GLFW_CONTEXT_VERSION_MAJOR CONTEXT_VERSION_MAJOR</td><td>1</td><td>Any valid major version number of the chosen client API</td></tr>
	     * <tr><td>GLFW_CONTEXT_VERSION_MINOR CONTEXT_VERSION_MINOR</td><td>0</td><td>Any valid minor version number of the chosen client API</td></tr>
	     * <tr><td>GLFW_CONTEXT_ROBUSTNESS CONTEXT_ROBUSTNESS</td><td>GLFW_NO_ROBUSTNESS NO_ROBUSTNESS</td><td>GLFW_NO_ROBUSTNESS NO_ROBUSTNESS GLFW_NO_RESET_NOTIFICATION NO_RESET_NOTIFICATION GLFW_LOSE_CONTEXT_ON_RESET LOSE_CONTEXT_ON_RESET</td></tr>
	     * <tr><td>GLFW_CONTEXT_RELEASE_BEHAVIOR CONTEXT_RELEASE_BEHAVIOR</td><td>GLFW_ANY_RELEASE_BEHAVIOR ANY_RELEASE_BEHAVIOR</td><td>GLFW_ANY_RELEASE_BEHAVIOR ANY_RELEASE_BEHAVIOR GLFW_RELEASE_BEHAVIOR_FLUSH RELEASE_BEHAVIOR_FLUSH GLFW_RELEASE_BEHAVIOR_NONE RELEASE_BEHAVIOR_NONE</td></tr>
	     * <tr><td>GLFW_CONTEXT_NO_ERROR CONTEXT_NO_ERROR</td><td>GLFW_FALSE FALSE</td><td>GLFW_TRUE TRUE or GLFW_FALSE FALSE</td></tr>
	     * <tr><td>GLFW_OPENGL_FORWARD_COMPAT OPENGL_FORWARD_COMPAT</td><td>GLFW_FALSE FALSE</td><td>GLFW_TRUE TRUE or GLFW_FALSE FALSE</td></tr>
	     * <tr><td>GLFW_OPENGL_DEBUG_CONTEXT OPENGL_DEBUG_CONTEXT</td><td>GLFW_FALSE FALSE</td><td>GLFW_TRUE TRUE or GLFW_FALSE FALSE</td></tr>
	     * <tr><td>GLFW_OPENGL_PROFILE OPENGL_PROFILE</td><td>GLFW_OPENGL_ANY_PROFILE OPENGL_ANY_PROFILE</td><td>GLFW_OPENGL_ANY_PROFILE OPENGL_ANY_PROFILE GLFW_OPENGL_CORE_PROFILE OPENGL_CORE_PROFILE GLFW_OPENGL_COMPAT_PROFILE OPENGL_COMPAT_PROFILE</td></tr>
	     * <tr><td>GLFW_COCOA_RETINA_FRAMEBUFFER COCOA_RETINA_FRAMEBUFFER</td><td>GLFW_TRUE TRUE</td><td>GLFW_TRUE TRUE or GLFW_FALSE FALSE</td></tr>
	     * <tr><td>GLFW_COCOA_GRAPHICS_SWITCHING COCOA_GRAPHICS_SWITCHING</td><td>GLFW_FALSE FALSE</td><td>GLFW_TRUE TRUE or GLFW_FALSE FALSE</td></tr>
	     * </table>
		 */

	}
	
	/**
	 * Creates a new GLFW window.
	 * @param title the window title.
	 * @param width the window's initial width.
	 * @param height the window's initial height.
	 */
	public GLFWWindow(String title, int width, int height) 
	{
		GLFWInit.init(); // init GLFW if not already (only happens once).
		this.handle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
		allocated = true;
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

}

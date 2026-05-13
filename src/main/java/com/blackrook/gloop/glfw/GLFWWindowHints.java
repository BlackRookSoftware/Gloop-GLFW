package com.blackrook.gloop.glfw;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

/**
 * Windows hints for the next window created.
 * <p> Don't modify these hints while a windows is being built with them, or undefined behavior may occur! 
 */
public class GLFWWindowHints
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
	
	/** The hint map. */
	private Map<Integer, Integer> hintMap;
	
	/**
	 * Creates a new default set of hints.
	 */
	public GLFWWindowHints()
	{
		this.hintMap = new HashMap<>();
		reset();
	}

	/**
	 * Resets all hints to default.
	 * <ul>
	 * <li><code>setResizable(true)</code></li>
	 * <li><code>setVisible(true)</code></li>
	 * <li><code>setDecorated(true)</code></li>
	 * <li><code>setFocused(true)</code></li>
	 * <li><code>setAutoIconified(true)</code></li>
	 * <li><code>setFloating(false)</code></li>
	 * <li><code>setMaximized(false)</code></li>
	 * <li><code>setCenteredCursor(true)</code></li>
	 * <li><code>setTransparentFramebuffer(false)</code></li>
	 * <li><code>setFocusOnShow(true)</code></li>
	 * <li><code>setScaleToMonitor(false)</code></li>
	 * <li><code>setRedBits(8)</code></li>
	 * <li><code>setGreenBits(8)</code></li>
	 * <li><code>setBlueBits(8)</code></li>
	 * <li><code>setAlphaBits(8)</code></li>
	 * <li><code>setDepthBits(24)</code></li>
	 * <li><code>setStencilBits(8)</code></li>
	 * <li><code>setAccumRedBits(0)</code></li>
	 * <li><code>setAccumGreenBits(0)</code></li>
	 * <li><code>setAccumBlueBits(0)</code></li>
	 * <li><code>setAccumAlphaBits(0)</code></li>
	 * <li><code>setAuxBuffers(0)</code></li>
	 * <li><code>setSamples(0)</code></li>
	 * <li><code>setRefreshRate(DONT_CARE)</code></li>
	 * <li><code>setStereo(false)</code></li>
	 * <li><code>setSRGBCapable(false)</code></li>
	 * <li><code>setDoubleBuffered(true)</code></li>
	 * <li><code>setClientAPI(ClientAPI.OPENGL_API)</code></li>
	 * <li><code>setContextCreationAPI(ContextCreationAPI.NATIVE_CONTEXT_API)</code></li>
	 * <li><code>setContextVersion(1, 0)</code></li>
	 * <li><code>setContextRobustness(ContextRobustness.NO_ROBUSTNESS)</code></li>
	 * <li><code>setContextReleaseBehavior(ContextReleaseBehavior.ANY_RELEASE_BEHAVIOR)</code></li>
	 * <li><code>setContextNoError(false)</code></li>
	 * <li><code>setOpenGLForwardCompatibility(false)</code></li>
	 * <li><code>setOpenGLDebugContext(false)</code></li>
	 * <li><code>setOpenGLProfile(OpenGLProfile.ANY_PROFILE)</code></li>
	 * <li><code>setCocoaRetinaFrameBuffer(true)</code></li>
	 * <li><code>setCocoaGraphicsSwitching(false)</code></li>
	 * </ul>
	 */
	public void reset()
	{
		setResizable(true);
		setVisible(true);
		setDecorated(true);
		setFocused(true);
		setAutoIconified(true);
		setFloating(false);
		setMaximized(false);
		setCenteredCursor(true);
		setTransparentFramebuffer(false);
		setFocusOnShow(true);
		setScaleToMonitor(false);
		setRedBits(8);
		setGreenBits(8);
		setBlueBits(8);
		setAlphaBits(8);
		setDepthBits(24);
		setStencilBits(8);
		setAccumRedBits(0);
		setAccumGreenBits(0);
		setAccumBlueBits(0);
		setAccumAlphaBits(0);
		setAuxBuffers(0);
		setSamples(0);
		setRefreshRate(DONT_CARE);
		setStereo(false);
		setSRGBCapable(false);
		setDoubleBuffered(true);
		setClientAPI(ClientAPI.OPENGL_API);
		setContextCreationAPI(ContextCreationAPI.NATIVE_CONTEXT_API);
		setContextVersion(1, 0);
		setContextRobustness(ContextRobustness.NO_ROBUSTNESS);
		setContextReleaseBehavior(ContextReleaseBehavior.ANY_RELEASE_BEHAVIOR);
		setContextNoError(false);
		setOpenGLForwardCompatibility(false);
		setOpenGLDebugContext(false);
		setOpenGLProfile(OpenGLProfile.ANY_PROFILE);
		setCocoaRetinaFrameBuffer(true);
		setCocoaGraphicsSwitching(false);
	}

	/**
	 * Sets if the next created window is resizable (manually, not via methods).
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setResizable(boolean value)
	{
		hintMap.put(GLFW.GLFW_RESIZABLE, glfwBoolean(value));
		return this;
	}

	/**
	 * Sets if the next created window is visible on creation.
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setVisible(boolean value)
	{
		hintMap.put(GLFW.GLFW_VISIBLE, glfwBoolean(value));
		return this;
	}
	
	/**
	 * Sets if the next created window is decorated on creation.
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setDecorated(boolean value)
	{
		hintMap.put(GLFW.GLFW_DECORATED, glfwBoolean(value));
		return this;
	}
	
	/**
	 * Sets if the next created window gains immediate focus on creation.
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setFocused(boolean value)
	{
		hintMap.put(GLFW.GLFW_FOCUSED, glfwBoolean(value));
		return this;
	}
	
	/**
	 * Sets if the next created window starts iconified on creation.
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setAutoIconified(boolean value)
	{
		hintMap.put(GLFW.GLFW_AUTO_ICONIFY, glfwBoolean(value));
		return this;
	}
	
	/**
	 * Sets if the next created window starts floating, or rather, "always on top".
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setFloating(boolean value)
	{
		hintMap.put(GLFW.GLFW_FLOATING, glfwBoolean(value));
		return this;
	}
	
	/**
	 * Sets if the next created window starts maximized on creation.
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setMaximized(boolean value)
	{
		hintMap.put(GLFW.GLFW_MAXIMIZED, glfwBoolean(value));
		return this;
	}
	
	/**
	 * Sets if the next created window starts with the mouse cursor centered over it (fullscreen only).
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setCenteredCursor(boolean value)
	{
		hintMap.put(GLFW.GLFW_CENTER_CURSOR, glfwBoolean(value));
		return this;
	}
	
	/**
	 * Sets if the next created window can have a transparent framebuffer (not supported everywhere).
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setTransparentFramebuffer(boolean value)
	{
		hintMap.put(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, glfwBoolean(value));
		return this;
	}
	
	/**
	 * Sets if the next created window requests focus when shown.
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setFocusOnShow(boolean value)
	{
		hintMap.put(GLFW.GLFW_FOCUS_ON_SHOW, glfwBoolean(value));
		return this;
	}
	
	/**
	 * Sets if the next created window resizes proportionally to each monitor it touches.
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setScaleToMonitor(boolean value)
	{
		hintMap.put(GLFW.GLFW_SCALE_TO_MONITOR, glfwBoolean(value));
		return this;
	}
	
	/**
	 * Sets the next created window's framebuffer's red color bits.
	 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setRedBits(int value)
	{
		hintMap.put(GLFW.GLFW_RED_BITS, value);
		return this;
	}
	
	/**
	 * Sets the next created window's framebuffer's green color bits.
	 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setGreenBits(int value)
	{
		hintMap.put(GLFW.GLFW_GREEN_BITS, value);
		return this;
	}
	
	/**
	 * Sets the next created window's framebuffer's blue color bits.
	 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setBlueBits(int value)
	{
		hintMap.put(GLFW.GLFW_BLUE_BITS, value);
		return this;
	}
	
	/**
	 * Sets the next created window's framebuffer's alpha bits.
	 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setAlphaBits(int value)
	{
		hintMap.put(GLFW.GLFW_ALPHA_BITS, value);
		return this;
	}
	
	/**
	 * Sets the next created window's depth buffer bits.
	 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setDepthBits(int value)
	{
		hintMap.put(GLFW.GLFW_DEPTH_BITS, value);
		return this;
	}
	
	/**
	 * Sets the next created window's stencil buffer bits.
	 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setStencilBits(int value)
	{
		hintMap.put(GLFW.GLFW_STENCIL_BITS, value);
		return this;
	}
	
	/**
	 * Sets the next created window's accumulation framebuffer's red color bits.
	 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setAccumRedBits(int value)
	{
		hintMap.put(GLFW.GLFW_ACCUM_RED_BITS, value);
		return this;
	}
	
	/**
	 * Sets the next created window's accumulation framebuffer's green color bits.
	 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setAccumGreenBits(int value)
	{
		hintMap.put(GLFW.GLFW_ACCUM_GREEN_BITS, value);
		return this;
	}
	
	/**
	 * Sets the next created window's accumulation framebuffer's blue color bits.
	 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setAccumBlueBits(int value)
	{
		hintMap.put(GLFW.GLFW_ACCUM_BLUE_BITS, value);
		return this;
	}
	
	/**
	 * Sets the next created window's accumulation framebuffer's alpha bits.
	 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setAccumAlphaBits(int value)
	{
		hintMap.put(GLFW.GLFW_ACCUM_ALPHA_BITS, value);
		return this;
	}
	
	/**
	 * Sets the next created window's auxiliary buffers.
	 * @param value 0 to {@link Integer#MAX_VALUE}.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setAuxBuffers(int value)
	{
		hintMap.put(GLFW.GLFW_AUX_BUFFERS, value);
		return this;
	}
	
	/**
	 * Sets the next created window's multisample samples.
	 * @param value 0 to {@link Integer#MAX_VALUE}.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setSamples(int value)
	{
		hintMap.put(GLFW.GLFW_SAMPLES, value);
		return this;
	}
	
	/**
	 * Sets the next created window's refresh rate.
	 * @param value 0 to {@link Integer#MAX_VALUE}, or {@link #DONT_CARE}.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setRefreshRate(int value)
	{
		hintMap.put(GLFW.GLFW_REFRESH_RATE, value);
		return this;
	}
	
	/**
	 * Sets if the next created window is in stereo mode.
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setStereo(boolean value)
	{
		hintMap.put(GLFW.GLFW_STEREO, glfwBoolean(value));
		return this;
	}

	/**
	 * Sets if the next created window is SRGB Capable.
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setSRGBCapable(boolean value)
	{
		hintMap.put(GLFW.GLFW_SRGB_CAPABLE, glfwBoolean(value));
		return this;
	}

	/**
	 * Sets if the next created window is double-buffered.
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setDoubleBuffered(boolean value)
	{
		hintMap.put(GLFW.GLFW_DOUBLEBUFFER, glfwBoolean(value));
		return this;
	}

	/**
	 * Sets which client API to create the context for. Hard constraint. 
	 * @param clientAPI the API type.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setClientAPI(ClientAPI clientAPI)
	{
		hintMap.put(GLFW.GLFW_CLIENT_API, clientAPI.glfwVal);
		return this;
	}
	
	/**
	 * Sets which context creation API to use to create the context. Hard constraint.
	 * Only used if the client API is set.
	 * @param contextCreationAPI the context creation type.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setContextCreationAPI(ContextCreationAPI contextCreationAPI)
	{
		hintMap.put(GLFW.GLFW_CONTEXT_CREATION_API, contextCreationAPI.glfwVal);
		return this;
	}
	
	/**
	 * Sets the OpenGL version.
	 * @param major the major version number.
	 * @param minor the minor version number.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setContextVersion(int major, int minor)
	{
		hintMap.put(GLFW.GLFW_CONTEXT_VERSION_MAJOR, major);
		hintMap.put(GLFW.GLFW_CONTEXT_VERSION_MINOR, minor);
		return this;
	}
	
	/**
	 * Sets the robustness strategy to be used by the context.
	 * @param contextRobustness the robustness strategy.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setContextRobustness(ContextRobustness contextRobustness)
	{
		hintMap.put(GLFW.GLFW_CONTEXT_ROBUSTNESS, contextRobustness.glfwVal);
		return this;
	}
	
	/**
	 * Sets the release behavior to be used by the context.
	 * @param contextReleaseBehavior the release behavior.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setContextReleaseBehavior(ContextReleaseBehavior contextReleaseBehavior)
	{
		hintMap.put(GLFW.GLFW_CONTEXT_RELEASE_BEHAVIOR, contextReleaseBehavior.glfwVal);
		return this;
	}
	
	/**
	 * Sets whether errors should be generated by the context.
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setContextNoError(boolean value)
	{
		hintMap.put(GLFW.GLFW_CONTEXT_NO_ERROR, glfwBoolean(value));
		return this;
	}

	/**
	 * Sets whether the OpenGL context should be forward-compatible, 
	 * i.e. one where all functionality deprecated in the requested version of OpenGL is removed. 
	 * This must only be used if the requested OpenGL version is 3.0 or above. 
	 * If OpenGL ES is requested, this hint is ignored. 
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setOpenGLForwardCompatibility(boolean value)
	{
		hintMap.put(GLFW.GLFW_OPENGL_FORWARD_COMPAT, glfwBoolean(value));
		return this;
	}

	/**
	 * Sets whether to create a debug OpenGL context, 
	 * which may have additional error and performance issue reporting functionality. 
	 * If OpenGL ES is requested, this hint is ignored. 
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setOpenGLDebugContext(boolean value)
	{
		hintMap.put(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, glfwBoolean(value));
		return this;
	}

	/**
	 * Sets which OpenGL profile to create the context for.
	 * If requesting an OpenGL version below 3.2, {@link OpenGLProfile#ANY_PROFILE} must be used. 
	 * If OpenGL ES is requested, this hint is ignored. 
	 * @param openGLProfile the profile to create.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setOpenGLProfile(OpenGLProfile openGLProfile)
	{
		hintMap.put(GLFW.GLFW_OPENGL_PROFILE, openGLProfile.glfwVal);
		return this;
	}

	/**
	 * Sets whether to use full resolution framebuffers on Retina displays. 
	 * This is ignored on non-macOS platforms.
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setCocoaRetinaFrameBuffer(boolean value)
	{
		hintMap.put(GLFW.GLFW_COCOA_RETINA_FRAMEBUFFER, glfwBoolean(value));
		return this;
	}

	/**
	 * Specifies whether to enable Automatic Graphics Switching, 
	 * i.e. to allow the system to choose the integrated GPU for the OpenGL 
	 * context and move it between GPUs if necessary or whether to force it 
	 * to always run on the discrete GPU. This only affects systems with both 
	 * integrated and discrete GPUs.
	 * This is ignored on non-macOS platforms.
	 * @param value true if so, false if not.
	 * @return itself, for chaining calls.
	 */
	public GLFWWindowHints setCocoaGraphicsSwitching(boolean value)
	{
		hintMap.put(GLFW.GLFW_COCOA_GRAPHICS_SWITCHING, glfwBoolean(value));
		return this;
	}
	
	// Sets the window hints using this hint set.
	void setHints()
	{
		GLFWContext.init();
		callHints();
	}
	
	/**
	 * Sets the hints for this context via {@link GLFW#glfwWindowHint(int, int)}.
	 * DO NOT call this outside of situations that require GLFW Window Hints to be set.
	 */
	public void callHints()
	{
		for (Map.Entry<Integer, Integer> entry : hintMap.entrySet())
			GLFW.glfwWindowHint(entry.getKey(), entry.getValue());
	}
	
	// Convert Java boolean to GLFW boolean.
	private static int glfwBoolean(boolean value)
	{
		return value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE;
	}

}


package com.blackrook.gloop.glfw;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import com.blackrook.gloop.glfw.exception.GLFWException;

/**
 * GLFW initializer state.
 * @author Matthew Tropiano
 */
public final class GLFWContext 
{
	private static boolean initialized = false;
	
	/**
	 * Initializes GLFW.
	 * If already initialized, this does nothing.
	 * <p><b>This must only be called from the main thread.</b>
	 */
	static void init()
	{
		if (initialized)
			return;
		
		GLFWErrorCallback.createPrint(System.err).set();
		if (!GLFW.glfwInit())
			throw new GLFWException("GLFW initialization failed!");

		GLFW.glfwDefaultWindowHints();
		
		initialized = true;
	}
	
	/**
	 * Destroys GLFW and frees its resources.
	 * Does nothing if GLFW was not initialized.
	 * <p><b>This must only be called from the main thread.</b>
	 */
	public static void terminate()
	{
		if (!initialized)
			return;
		GLFW.glfwTerminate();
		initialized = false;
	}
	
}

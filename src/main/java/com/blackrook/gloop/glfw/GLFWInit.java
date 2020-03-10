package com.blackrook.gloop.glfw;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import com.blackrook.gloop.glfw.exception.GLFWException;

/**
 * GLFW initializer state.
 * @author Matthew Tropiano
 */
final class GLFWInit 
{
	private static boolean initialized = false;
	
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
}

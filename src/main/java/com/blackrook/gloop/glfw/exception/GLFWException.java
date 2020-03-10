package com.blackrook.gloop.glfw.exception;

/**
 * Generic GLFW error.
 * @author Matthew Tropiano
 */
public class GLFWException extends RuntimeException
{
	private static final long serialVersionUID = 120947875949893413L;

	public GLFWException()
	{
		super("A new sound buffer couldn't be allocated.");
	}

	public GLFWException(String message)
	{
		super(message);
	}
	
	public GLFWException(String message, Throwable cause)
	{
		super(message, cause);
	}
}

package com.blackrook.gloop.glfw.input.exception;

/**
 * An exception that can occur on input event dispatch setup.
 * @author Matthew Tropiano
 */
public class InputDispatchException extends RuntimeException
{
	private static final long serialVersionUID = 5078120301230495550L;

	public InputDispatchException()
	{
		super("Input dispatch error.");
	}

	public InputDispatchException(String message)
	{
		super(message);
	}
	
	public InputDispatchException(String message, Throwable cause)
	{
		super(message, cause);
	}
}

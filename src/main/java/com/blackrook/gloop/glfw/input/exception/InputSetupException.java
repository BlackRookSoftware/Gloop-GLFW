package com.blackrook.gloop.glfw.input.exception;

/**
 * An exception that can occur on input system setup.
 * @author Matthew Tropiano
 */
public class InputSetupException extends RuntimeException
{
	private static final long serialVersionUID = -4143889559705481169L;

	public InputSetupException()
	{
		super("Input system setup error.");
	}

	public InputSetupException(String message)
	{
		super(message);
	}
	
	public InputSetupException(String message, Throwable cause)
	{
		super(message, cause);
	}
}

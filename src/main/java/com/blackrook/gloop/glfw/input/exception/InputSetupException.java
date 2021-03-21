/*******************************************************************************
 * Copyright (c) 2020-2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
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

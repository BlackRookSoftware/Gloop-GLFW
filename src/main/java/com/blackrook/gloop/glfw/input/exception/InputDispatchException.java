/*******************************************************************************
 * Copyright (c) 2020-2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
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

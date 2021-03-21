/*******************************************************************************
 * Copyright (c) 2020-2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
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
		super("A GLFW object couldn't be allocated.");
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

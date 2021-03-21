/*******************************************************************************
 * Copyright (c) 2020-2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.glfw;

import com.blackrook.gloop.glfw.exception.GLFWException;

/**
 * Generic GLFW handle type. 
 * This wraps an allocated memory address.
 * @author Matthew Tropiano
 */
public abstract class GLFWHandle
{
	/**
	 * Allocates a new GLFW handle.
	 */
	protected GLFWHandle() {}
	
	/**
	 * @return this handle's GLFW address handle.
	 */
	public abstract long getHandle();

	/**
	 * @return true if this handle was allocated, false if not.
	 */
	public abstract boolean isCreated(); 
	
	/**
	 * Destroys this handle. Does nothing if already destroyed.
	 * @throws GLFWException if a problem occurs during free.
	 */
	public abstract void destroy();

	@Override
	public int hashCode() 
	{
		return Long.hashCode(getHandle());
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof GLFWHandle)
			return equals((GLFWHandle)obj);
		return super.equals(obj);
	}

	/**
	 * Tests if this GLFW handle equals the provided one.
	 * @param handle the handle to test.
	 * @return true if so, false if not.
	 */
	public boolean equals(GLFWHandle handle) 
	{
		return getClass().equals(handle.getClass()) && this.getHandle() == handle.getHandle();
	}

	@Override
	public void finalize() throws Throwable
	{
		destroy();
		super.finalize();
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ' ' + getHandle();
	}

}

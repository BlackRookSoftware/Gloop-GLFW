/*******************************************************************************
 * Copyright (c) 2020-2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.glfw;

import org.lwjgl.glfw.GLFW;

/**
 * A GLFW cursor instance.
 * @author Matthew Tropiano
 */
public class GLFWCursor extends GLFWHandle
{
	/** The cursor memory address. */
	private long handle;
	/** Is this allocated? */
	private boolean allocated;

	/**
	 * <b>This must only be called from the main thread.</b>
	 * @return the arrow cursor.
	 */
	public static GLFWCursor createArrowCursor()
	{
		return new GLFWCursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR));
	}
	
	/**
	 * <b>This must only be called from the main thread.</b>
	 * @return the I-Beam (text carat) cursor.
	 */
	public static GLFWCursor createIBeamCursor()
	{
		return new GLFWCursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR));
	}
	
	/**
	 * <b>This must only be called from the main thread.</b>
	 * @return the crosshair cursor.
	 */
	public static GLFWCursor createCrosshairCursor()
	{
		return new GLFWCursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR));
	}
	
	/**
	 * <b>This must only be called from the main thread.</b>
	 * @return the hand cursor.
	 */
	public static GLFWCursor createHandCursor()
	{
		return new GLFWCursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR));
	}
	
	/**
	 * <b>This must only be called from the main thread.</b>
	 * @return the horizontal resize cursor.
	 */
	public static GLFWCursor createHorizontalResizeCursor()
	{
		return new GLFWCursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR));
	}
	
	/**
	 * <b>This must only be called from the main thread.</b>
	 * @return the vertical resize cursor.
	 */
	public static GLFWCursor createVerticalResizeCursor()
	{
		return new GLFWCursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR));
	}
	
	/**
	 * Creates a new GLFW cursor.
	 * @param handle the memory handle.
	 */
	private GLFWCursor(long handle) 
	{
		GLFWContext.init(); // init GLFW if not already (only happens once).
		this.handle = handle;
		this.allocated = true;
	}
		
	@Override
	public long getHandle()
	{
		return handle;
	}

	@Override
	public boolean isCreated()
	{
		return allocated;
	}

	@Override
	public void destroy()
	{
		GLFW.glfwDestroyCursor(handle);
	}

}

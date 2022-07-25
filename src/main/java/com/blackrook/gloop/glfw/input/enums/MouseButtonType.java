/*******************************************************************************
 * Copyright (c) 2020-2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.glfw.input.enums;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

/**
 * All valid input mouse buttons.
 * @author Matthew Tropiano
 */
public enum MouseButtonType
{
	LEFT(GLFW.GLFW_MOUSE_BUTTON_LEFT),
	RIGHT(GLFW.GLFW_MOUSE_BUTTON_RIGHT),
	MIDDLE(GLFW.GLFW_MOUSE_BUTTON_MIDDLE),
	_4(GLFW.GLFW_MOUSE_BUTTON_4),
	_5(GLFW.GLFW_MOUSE_BUTTON_5),
	_6(GLFW.GLFW_MOUSE_BUTTON_6),
	_7(GLFW.GLFW_MOUSE_BUTTON_7),
	_8(GLFW.GLFW_MOUSE_BUTTON_8),
	LAST(GLFW.GLFW_MOUSE_BUTTON_LAST);

	private static final Map<Integer, MouseButtonType> VALUE_MAP = new HashMap<Integer, MouseButtonType>()
	{
		private static final long serialVersionUID = -4718418325150039182L;
		{
			for (MouseButtonType m : MouseButtonType.values())
				put(m.glfwId, m);
		}
		
	};
	
	private int glfwId;
	
	private MouseButtonType(int glfwId)
	{
		this.glfwId = glfwId;
	}
	
	/**
	 * Gets a mouse button by corresponding GLFW id.
	 * @param glfwId the id.
	 * @return the corresponding type, or null if not found.
	 */
	public static final MouseButtonType getById(int glfwId)
	{
		return VALUE_MAP.get(glfwId);
	}

	/**
	 * @return the GLFW id of this key.
	 */
	public int getGLFWId()
	{
		return glfwId;
	}
	
}

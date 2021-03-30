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

/**
 * All valid input joystick direction positions.
 * @author Matthew Tropiano
 */
public enum JoystickDirectionType
{
	NEUTRAL(0),
	POSITIVE(1),
	NEGATIVE(1);

	private static final Map<Integer, JoystickDirectionType> VALUE_MAP = new HashMap<Integer, JoystickDirectionType>()
	{
		private static final long serialVersionUID = -2586863475214282129L;
		{
			for (JoystickDirectionType h : JoystickDirectionType.values())
				put(h.glfwId, h);
		}
		
	};
	
	private int glfwId;
	
	private JoystickDirectionType(int glfwId)
	{
		this.glfwId = glfwId;
	}
	
	/**
	 * Gets a joystick hat position by corresponding GLFW id.
	 * @param glfwId the id.
	 * @return the corresponding type, or null if not found.
	 */
	public static final JoystickDirectionType getById(int glfwId)
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

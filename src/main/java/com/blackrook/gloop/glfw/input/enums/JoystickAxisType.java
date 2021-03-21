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
 * All valid input joystick axes.
 * @author Matthew Tropiano
 */
public enum JoystickAxisType
{
	LEFT_X(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X),
	LEFT_Y(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y),
	RIGHT_X(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X),
	RIGHT_Y(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y),

	L2(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER),
	R2(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER),

	LEFT_TRIGGER(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER),
	RIGHT_TRIGGER(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER);

	private static final Map<Integer, JoystickAxisType> VALUE_MAP = new HashMap<Integer, JoystickAxisType>()
	{
		private static final long serialVersionUID = -7901479982306401986L;
		{
			for (JoystickAxisType b : JoystickAxisType.values())
				put(b.glfwId, b);
		}
	};
	
	private int glfwId;
	
	private JoystickAxisType(int glfwId)
	{
		this.glfwId = glfwId;
	}
	
	/**
	 * Gets a gamepad axis by corresponding GLFW id.
	 * @param glfwId the id.
	 * @return the corresponding type, or null if not found.
	 */
	public static final JoystickAxisType getById(int glfwId)
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

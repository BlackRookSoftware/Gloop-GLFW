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
 * All valid input joystick buttons.
 * @author Matthew Tropiano
 */
public enum JoystickButtonType
{
	A(GLFW.GLFW_GAMEPAD_BUTTON_A),
	B(GLFW.GLFW_GAMEPAD_BUTTON_B),
	X(GLFW.GLFW_GAMEPAD_BUTTON_X),
	Y(GLFW.GLFW_GAMEPAD_BUTTON_Y),
	LEFT_BUMPER(GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER),
	RIGHT_BUMPER(GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER),
	BACK(GLFW.GLFW_GAMEPAD_BUTTON_BACK),
	START(GLFW.GLFW_GAMEPAD_BUTTON_START),
	GUIDE(GLFW.GLFW_GAMEPAD_BUTTON_GUIDE),
	LEFT_THUMB(GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB),
	RIGHT_THUMB(GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB),
	DPAD_UP(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP),
	DPAD_RIGHT(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT),
	DPAD_DOWN(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN),
	DPAD_LEFT(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT);

	private static final Map<Integer, JoystickButtonType> VALUE_MAP = new HashMap<Integer, JoystickButtonType>()
	{
		private static final long serialVersionUID = 2569334204822550576L;
		{
			for (JoystickButtonType b : JoystickButtonType.values())
				put(b.glfwId, b);
		}
	};
	
	private int glfwId;
	
	private JoystickButtonType(int glfwId)
	{
		this.glfwId = glfwId;
	}
	
	/**
	 * Gets a gamepad button by corresponding GLFW id.
	 * @param glfwId the id.
	 * @return the corresponding type, or null if not found.
	 */
	public static final JoystickButtonType getById(int glfwId)
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

package com.blackrook.gloop.glfw.input.enums;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

/**
 * All valid input gamepad axes.
 * @author Matthew Tropiano
 */
public enum GamepadAxisType
{
	LEFT_X(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X),
	LEFT_Y(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y),
	RIGHT_X(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X),
	RIGHT_Y(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y),
	LEFT_TRIGGER(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER),
	RIGHT_TRIGGER(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER),
	LAST(GLFW.GLFW_GAMEPAD_AXIS_LAST);

	private static final Map<Integer, GamepadAxisType> VALUE_MAP = new HashMap<Integer, GamepadAxisType>()
	{
		private static final long serialVersionUID = -7901479982306401986L;
		{
			for (GamepadAxisType b : GamepadAxisType.values())
				put(b.glfwId, b);
		}
		
	};
	
	private int glfwId;
	
	private GamepadAxisType(int glfwId)
	{
		this.glfwId = glfwId;
	}
	
	/**
	 * Gets a gamepad axis by corresponding GLFW id.
	 * @param glfwId the id.
	 * @return the corresponding type, or null if not found.
	 */
	public static final GamepadAxisType getById(int glfwId)
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

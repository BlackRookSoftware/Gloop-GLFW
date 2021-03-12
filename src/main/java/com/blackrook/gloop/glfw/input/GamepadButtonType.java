package com.blackrook.gloop.glfw.input;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

/**
 * All valid input gamepad buttons.
 * @author Matthew Tropiano
 */
public enum GamepadButtonType
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
	DPAD_LEFT(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT),
	LAST(GLFW.GLFW_GAMEPAD_BUTTON_LAST),
	CROSS(GLFW.GLFW_GAMEPAD_BUTTON_CROSS),
	CIRCLE(GLFW.GLFW_GAMEPAD_BUTTON_CIRCLE),
	SQUARE(GLFW.GLFW_GAMEPAD_BUTTON_SQUARE),
	TRIANGLE(GLFW.GLFW_GAMEPAD_BUTTON_TRIANGLE);

	private static final Map<Integer, GamepadButtonType> VALUE_MAP = new HashMap<Integer, GamepadButtonType>()
	{
		private static final long serialVersionUID = 2569334204822550576L;
		{
			for (GamepadButtonType b : GamepadButtonType.values())
				put(b.glfwId, b);
		}
		
	};
	
	private int glfwId;
	
	private GamepadButtonType(int glfwId)
	{
		this.glfwId = glfwId;
	}
	
	/**
	 * Gets a gamepad button by corresponding GLFW id.
	 * @param glfwId the id.
	 * @return the corresponding type, or null if not found.
	 */
	public static final GamepadButtonType getById(int glfwId)
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

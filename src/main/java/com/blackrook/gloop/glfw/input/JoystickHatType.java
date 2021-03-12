package com.blackrook.gloop.glfw.input;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

/**
 * All valid input joystick hat positions.
 * @author Matthew Tropiano
 */
public enum JoystickHatType
{
	CENTERED(GLFW.GLFW_HAT_CENTERED),
	UP(GLFW.GLFW_HAT_UP),
	RIGHT(GLFW.GLFW_HAT_RIGHT),
	DOWN(GLFW.GLFW_HAT_DOWN),
	LEFT(GLFW.GLFW_HAT_LEFT),
	RIGHT_UP(GLFW.GLFW_HAT_RIGHT_UP),
	RIGHT_DOWN(GLFW.GLFW_HAT_RIGHT_DOWN),
	LEFT_UP(GLFW.GLFW_HAT_LEFT_UP),
	LEFT_DOWN(GLFW.GLFW_HAT_LEFT_DOWN);

	private static final Map<Integer, JoystickHatType> VALUE_MAP = new HashMap<Integer, JoystickHatType>()
	{
		private static final long serialVersionUID = -6295752599264541456L;
		{
			for (JoystickHatType h : JoystickHatType.values())
				put(h.glfwId, h);
		}
		
	};
	
	private int glfwId;
	
	private JoystickHatType(int glfwId)
	{
		this.glfwId = glfwId;
	}
	
	/**
	 * Gets a joystick hat position by corresponding GLFW id.
	 * @param glfwId the id.
	 * @return the corresponding type, or null if not found.
	 */
	public static final JoystickHatType getById(int glfwId)
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

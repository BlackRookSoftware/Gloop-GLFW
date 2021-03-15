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
	_1(GLFW.GLFW_MOUSE_BUTTON_1),
	_2(GLFW.GLFW_MOUSE_BUTTON_2),
	_3(GLFW.GLFW_MOUSE_BUTTON_3),
	_4(GLFW.GLFW_MOUSE_BUTTON_4),
	_5(GLFW.GLFW_MOUSE_BUTTON_5),
	_6(GLFW.GLFW_MOUSE_BUTTON_6),
	_7(GLFW.GLFW_MOUSE_BUTTON_7),
	_8(GLFW.GLFW_MOUSE_BUTTON_8),
	LAST(GLFW.GLFW_MOUSE_BUTTON_LAST),
	LEFT(GLFW.GLFW_MOUSE_BUTTON_LEFT),
	RIGHT(GLFW.GLFW_MOUSE_BUTTON_RIGHT),
	MIDDLE(GLFW.GLFW_MOUSE_BUTTON_MIDDLE);

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

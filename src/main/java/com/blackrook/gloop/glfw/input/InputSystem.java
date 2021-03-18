package com.blackrook.gloop.glfw.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackrook.gloop.glfw.GLFWWindow;
import com.blackrook.gloop.glfw.GLFWWindow.InputListener;
import com.blackrook.gloop.glfw.input.exception.InputSetupException;

/**
 * An input event filtering system, meant to be used as a "one-stop" management class.
 * <p>Annotated input objects are attached to this system as listeners and have their
 * fields and methods called when events are sent through via window events and other
 * device polling like joysticks/gamepads.
 * @author Matthew Tropiano
 */
public class InputSystem
{
	private Map<Object, InputObjectProfile> addedObjects;
	
	private List<InputObjectProfile> keyHandlers;
	private List<InputObjectProfile> mouseButtonHandlers;
	private List<InputObjectProfile> mouseAxisHandlers;
	private List<InputObjectProfile> mouseScrollHandlers;
	private List<InputObjectProfile> gamepadButtonHandlers;
	private List<InputObjectProfile> gamepadAxisHandlers;
	private List<InputObjectProfile> joystickHatHandlers;
	
	private InputListener inputListener;
	
	/**
	 * Creates a new input system.
	 */
	public InputSystem()
	{
		this.addedObjects = new HashMap<Object, InputObjectProfile>();
		this.keyHandlers = null;
		this.mouseButtonHandlers = null;
		this.mouseAxisHandlers = null;
		this.mouseScrollHandlers = null;
		this.gamepadButtonHandlers = null;
		this.gamepadAxisHandlers = null;
		this.joystickHatHandlers = null;
		this.inputListener = new InputListener()
		{
			@Override
			public void onKeyPress(GLFWWindow window, int glfwKey, int scanCode, int modFlags)
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void onKeyRelease(GLFWWindow window, int glfwKey, int scanCode, int modFlags)
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void onKeyRepeated(GLFWWindow window, int glfwKey, int scanCode, int modFlags)
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void onKeyTyped(GLFWWindow window, char c)
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void onMousePosition(GLFWWindow window, double x, double y)
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void onMouseButtonPress(GLFWWindow window, int glfwButton, int modFlags)
			{
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onMouseButtonRelease(GLFWWindow window, int glfwButton, int modFlags)
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void onScroll(GLFWWindow window, double x, double y)
			{
				// TODO Auto-generated method stub
			}
			
		};
		
	}

	/**
	 * Adds an object that is annotated for receiving input events to this input system.
	 * Adding or removing an object from this system in thread-safe.
	 * @param inputObject the input object to add.
	 * @throws InputSetupException if an object's annotations are not set up properly.
	 */
	public void addInputObject(Object inputObject)
	{
		if (addedObjects.containsKey(inputObject))
			return;
		
		synchronized (addedObjects)
		{
			// multi-add lock escape.
			if (addedObjects.containsKey(inputObject))
				return;
			
			InputObjectProfile profile = new InputObjectProfile(inputObject);
			
			if (profile.handlesKeys())
				(keyHandlers = makeIfNull(keyHandlers)).add(profile);
			if (profile.handlesMouseButtons())
				(mouseButtonHandlers = makeIfNull(mouseButtonHandlers)).add(profile);
			if (profile.handlesMouseAxes())
				(mouseAxisHandlers = makeIfNull(mouseAxisHandlers)).add(profile);
			if (profile.handlesMouseScroll())
				(mouseScrollHandlers = makeIfNull(mouseScrollHandlers)).add(profile);
			if (profile.handlesGamepadButtons())
				(gamepadButtonHandlers = makeIfNull(gamepadButtonHandlers)).add(profile);
			if (profile.handlesGamepadAxes())
				(gamepadAxisHandlers = makeIfNull(gamepadAxisHandlers)).add(profile);
			if (profile.handlesJoystickHats())
				(joystickHatHandlers = makeIfNull(joystickHatHandlers)).add(profile);
			
			addedObjects.put(inputObject, profile);
		}
	}
	
	/**
	 * Removes an object that is annotated for receiving input events from this input system.
	 * Adding or removing an object from this system in thread-safe.
	 * @param inputObject the input object to remove.
	 */
	public void removeInputObject(Object inputObject)
	{
		if (!addedObjects.containsKey(inputObject))
			return;
		
		synchronized (addedObjects)
		{
			// multi-remove lock escape.
			if (!addedObjects.containsKey(inputObject))
				return;
			
			InputObjectProfile profile = addedObjects.remove(inputObject);
			
			if (profile.handlesKeys())
				keyHandlers.remove(profile);
			if (profile.handlesMouseButtons())
				mouseButtonHandlers.remove(profile);
			if (profile.handlesMouseAxes())
				mouseAxisHandlers.remove(profile);
			if (profile.handlesMouseScroll())
				mouseScrollHandlers.remove(profile);
			if (profile.handlesGamepadButtons())
				gamepadButtonHandlers.remove(profile);
			if (profile.handlesGamepadAxes())
				gamepadAxisHandlers.remove(profile);
			if (profile.handlesJoystickHats())
				joystickHatHandlers.remove(profile);
		}
	}
	
	/**
	 * Gets this system's input listener to attach to a window to receive and filter input events.
	 * @return this instance's listener.
	 */
	public InputListener getWindowInputListener()
	{
		return inputListener;
	}
	
	// TODO: Event firing.
	
	// TODO: Joystick polling.
	
	// Creates an empty list if list is null, else return parameter.
	private static <T> List<T> makeIfNull(List<T> map)
	{
		return map != null ? map : new ArrayList<>(4);
	}
	
}

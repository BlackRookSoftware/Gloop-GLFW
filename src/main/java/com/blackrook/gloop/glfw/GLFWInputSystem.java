package com.blackrook.gloop.glfw;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWJoystickCallback;

import com.blackrook.gloop.glfw.GLFWWindow.InputListener;
import com.blackrook.gloop.glfw.GLFWWindow.WindowListener;
import com.blackrook.gloop.glfw.input.annotation.InputGamepadAxis;
import com.blackrook.gloop.glfw.input.annotation.InputGamepadButton;
import com.blackrook.gloop.glfw.input.annotation.InputJoystickHat;
import com.blackrook.gloop.glfw.input.annotation.InputKey;
import com.blackrook.gloop.glfw.input.annotation.InputMouseAxis;
import com.blackrook.gloop.glfw.input.annotation.InputMouseButton;
import com.blackrook.gloop.glfw.input.annotation.InputMousePosition;
import com.blackrook.gloop.glfw.input.annotation.InputMouseScroll;
import com.blackrook.gloop.glfw.input.annotation.OnGamepadAxisAction;
import com.blackrook.gloop.glfw.input.annotation.OnGamepadButtonAction;
import com.blackrook.gloop.glfw.input.annotation.OnJoystickHatAction;
import com.blackrook.gloop.glfw.input.annotation.OnKeyAction;
import com.blackrook.gloop.glfw.input.annotation.OnKeyTypedAction;
import com.blackrook.gloop.glfw.input.annotation.OnMouseAxisAction;
import com.blackrook.gloop.glfw.input.annotation.OnMouseButtonAction;
import com.blackrook.gloop.glfw.input.annotation.OnMousePositionAction;
import com.blackrook.gloop.glfw.input.annotation.OnMouseScrollAction;
import com.blackrook.gloop.glfw.input.enums.GamepadAxisType;
import com.blackrook.gloop.glfw.input.enums.GamepadButtonType;
import com.blackrook.gloop.glfw.input.enums.JoystickHatType;
import com.blackrook.gloop.glfw.input.enums.KeyType;
import com.blackrook.gloop.glfw.input.enums.MouseAxisType;
import com.blackrook.gloop.glfw.input.enums.MouseButtonType;
import com.blackrook.gloop.glfw.input.exception.InputDispatchException;
import com.blackrook.gloop.glfw.input.exception.InputSetupException;

/**
 * An input event filtering system, meant to be used as a "one-stop" management class.
 * <p> Annotated input objects are attached to this system as listeners and have their
 * fields altered and methods called when events are sent through via window events and other
 * device polling like joysticks/gamepads.
 * <p> Since the Main thread is the thread responsible for carrying out callbacks and
 * other listeners via a window, all input objects should handle their event consumption as lightly
 * as possible so that other event processing doesn't get held up. If actions need to be taken
 * on value changes via mouse/keyboard events, it might be worth processing in a parallel 
 * thread and kicked off by a listener that listens for event dispatch completion.
 * @author Matthew Tropiano
 */
public class GLFWInputSystem
{
	/** The map of added input object to profile. */
	private Map<Object, InputObjectProfile> addedObjects;
	/** List of objects that handle keys. */
	private List<InputObjectProfile> keyHandlers;
	/** List of objects that handle key typing. */
	private List<InputObjectProfile> keyTypedHandlers;
	/** List of objects that handle mouse buttons. */
	private List<InputObjectProfile> mouseButtonHandlers;
	/** List of objects that handle mouse axes. */
	private List<InputObjectProfile> mouseAxisHandlers;
	/** List of objects that handle mouse positions. */
	private List<InputObjectProfile> mousePositionHandlers;
	/** List of objects that handle mouse scrolling. */
	private List<InputObjectProfile> mouseScrollHandlers;
	/** The listener to attach to windows to affect input filtering. */
	private ListenerFilter listener;
	
	/** Bit set for what joysticks are present to poll. */
	private boolean[] joystickIsPresent;
	/** The input objects to use for each gamepad. */
	private JoystickInputObject[] joystickInputObjects;
	/** List of joystick event listeners. */
	private List<JoystickConnectionListener> joystickListeners;
	/** Allocated callback handle. */
	private GLFWJoystickCallback joystickCallbackHandle;

	/**
	 * Joystick input object.
	 */
	private static class JoystickInputObject
	{
		/** List of objects that handle gamepad buttons. */
		private List<InputObjectProfile> gamepadButtonHandlers;
		/** List of objects that handle gamepad axes. */
		private List<InputObjectProfile> gamepadAxisHandlers;
		/** List of objects that handle joystick hats. */
		private List<InputObjectProfile> joystickHatHandlers;
		
		private JoystickInputObject(Object instance)
		{
			this.gamepadButtonHandlers = null;
			this.gamepadAxisHandlers = null;
			this.joystickHatHandlers = null;

			InputObjectProfile profile = new InputObjectProfile(instance);

			if (profile.handlesGamepadButtons())
				(gamepadButtonHandlers = makeIfNull(gamepadButtonHandlers)).add(profile);
			if (profile.handlesGamepadAxes())
				(gamepadAxisHandlers = makeIfNull(gamepadAxisHandlers)).add(profile);
			if (profile.handlesJoystickHats())
				(joystickHatHandlers = makeIfNull(joystickHatHandlers)).add(profile);
		}
		
		/**
		 * Fires a gamepad button event in this system.
		 * @param type the gamepad button type.
		 * @param pressed true if pressed, false if released.
		 * @return true if the event was handled by an object, false if not. 
		 */
		public boolean fireGamepadButtonEvent(GamepadButtonType type, boolean pressed)
		{
			boolean handled = false;
			if (gamepadButtonHandlers != null) for (int i = 0; i < gamepadButtonHandlers.size(); i++)
				handled |= gamepadButtonHandlers.get(i).fireGamepadButton(type, pressed);
			return handled;
		}
		
		/**
		 * Fires a gamepad axis event in this system. 
		 * @param type the gamepad axis type.
		 * @param amount the amount moved.
		 * @return true if the event was handled by an object, false if not. 
		 */
		public boolean fireGamepadAxisEvent(GamepadAxisType type, double amount)
		{
			boolean handled = false;
			if (gamepadAxisHandlers != null) for (int i = 0; i < gamepadAxisHandlers.size(); i++)
				handled |= gamepadAxisHandlers.get(i).fireGamepadAxis(type, amount);
			return handled;
		}
		
		/**
		 * Fires a joystick hat event in this system. 
		 * @param index the hat index on the device.
		 * @param type the joystick hat type.
		 * @return true if the event was handled by an object, false if not. 
		 */
		public boolean fireJoystickHatEvent(int index, JoystickHatType type)
		{
			boolean handled = false;
			if (joystickHatHandlers != null) for (int i = 0; i < joystickHatHandlers.size(); i++)
				handled |= joystickHatHandlers.get(i).fireJoystickHat(index, type);
			return handled;
		}
		
	}
	
	/**
	 * A listener interface for when joysticks/gamepads get 
	 * connected or disconnected from GLFW.
	 */
	public interface JoystickConnectionListener
	{
		/**
		 * Called when a joystick connects to the system.
		 * @param joystickId the GLFW joystick id.
		 * @param isGamepad true if GLFW thinks this is a gamepad of some kind, false otherwise.
		 * @param guid the GUID of the connected joystick.
		 * @param name the name of the joystick.
		 */
		void onJoystickConnect(int joystickId, boolean isGamepad, String guid, String name);

		/**
		 * Called when a joystick disconnects from the system.
		 * @param joystickId the GLFW joystick id.
		 */
		void onJoystickDisconnect(int joystickId);
	}

	/**
	 * Creates a new input system.
	 */
	public GLFWInputSystem()
	{
		this.addedObjects = new HashMap<Object, InputObjectProfile>();
		this.keyHandlers = null;
		this.mouseButtonHandlers = null;
		this.mouseAxisHandlers = null;
		this.mousePositionHandlers = null;
		this.mouseScrollHandlers = null;
		this.listener = new ListenerFilter();

		this.joystickIsPresent = null;
		this.joystickInputObjects = null;
		this.joystickListeners = new ArrayList<>(4);
		this.joystickCallbackHandle = null;
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
			if (profile.handlesKeyTyping())
				(keyTypedHandlers = makeIfNull(keyTypedHandlers)).add(profile);
			if (profile.handlesMouseButtons())
				(mouseButtonHandlers = makeIfNull(mouseButtonHandlers)).add(profile);
			if (profile.handlesMouseAxes())
				(mouseAxisHandlers = makeIfNull(mouseAxisHandlers)).add(profile);
			if (profile.handlesMousePositions())
				(mousePositionHandlers = makeIfNull(mousePositionHandlers)).add(profile);
			if (profile.handlesMouseScroll())
				(mouseScrollHandlers = makeIfNull(mouseScrollHandlers)).add(profile);

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
			if (profile.handlesKeyTyping())
				keyTypedHandlers.remove(profile);
			if (profile.handlesMouseButtons())
				mouseButtonHandlers.remove(profile);
			if (profile.handlesMouseAxes())
				mouseAxisHandlers.remove(profile);
			if (profile.handlesMousePositions())
				mousePositionHandlers.remove(profile);
			if (profile.handlesMouseScroll())
				mouseScrollHandlers.remove(profile);
		}
	}

	/**
	 * Adds a joystick-specific object that is annotated for receiving input events to this input system.
	 * Adding or removing an object from this system in thread-safe.
	 * Adding a joystick where a JID is already used will remove the old one.
	 * @param jid the joystick id.
	 * @param inputObject the input object to add.
	 * @throws InputSetupException if an object's annotations are not set up properly.
	 */
	public void addJoystickInputObject(int jid, Object inputObject)
	{
		removeJoystickInputObject(jid);
		synchronized (joystickInputObjects)
		{
			joystickInputObjects[jid] = new JoystickInputObject(inputObject);
		}
	}
	
	/**
	 * Removes a joystick-specific object that is annotated for receiving input events to this input system.
	 * Adding or removing an object from this system in thread-safe.
	 * @param jid the joystick id.
	 * @throws InputSetupException if an object's annotations are not set up properly.
	 */
	public void removeJoystickInputObject(int jid)
	{
		if (joystickInputObjects[jid] == null)
			return;
		
		synchronized (joystickInputObjects)
		{
			joystickInputObjects[jid] = null;
		}
	}
	
	/**
	 * Attaches this input system to a window.
	 * This listens for key/mouse events.
	 * <p> Essentially just attaches the window and input listener of this system to the window. 
	 * @param window the window to accept events from.
	 */
	public void attachToWindow(GLFWWindow window)
	{
		window.addInputListener(listener);
		window.addWindowListener(listener);
	}
	
	/**
	 * Detaches this input system from a window.
	 * <p> Essentially just detaches the window and input listener of this system from the window. 
	 * @param window the window to remove from.
	 */
	public void detachFromWindow(GLFWWindow window)
	{
		window.addInputListener(listener);
		window.addWindowListener(listener);
	}
	
	/**
	 * Adds a joystick listener to this input system to listen for
	 * joystick connections and disconnections. 
	 * Usually, an annotated input object is added on connection, and removed on disconnect.
	 * This method is thread safe.
	 * <p>Joystick support does not need to be enabled for this to be called.
	 * @param listener the listener to add.
	 */
	public void addJoystickListener(JoystickConnectionListener listener)
	{
		synchronized (joystickListeners)
		{
			joystickListeners.add(listener);
		}
	}
	
	/**
	 * Removes a joystick listener from this input system.
	 * This method is thread safe.
	 * <p>Joystick support does not need to be enabled for this to be called.
	 * @param listener the listener to remove.
	 */
	public void removeJoystickListener(JoystickConnectionListener listener)
	{
		synchronized (joystickListeners)
		{
			joystickListeners.remove(listener);
		}
	}
	
	/**
	 * This method must be called in order to prep joysticks for polling.
	 * If joysticks are already enabled, this does nothing.
	 * <p>This will fire connection events to {@link JoystickConnectionListener}s for 
	 * each joystick found at enable so that they can be attached as input listeners.
	 */
	public void enableJoysticks()
	{
		if (joystickCallbackHandle != null)
			return;
		
		joystickCallbackHandle = GLFW.glfwSetJoystickCallback((jid, event) -> 
		{
			switch (event)
			{
				case GLFW.GLFW_CONNECTED:
				{
					for (int i = 0; i < joystickListeners.size(); i++)
						joystickListeners.get(i).onJoystickConnect(jid, GLFW.glfwJoystickIsGamepad(jid), GLFW.glfwGetJoystickGUID(jid), GLFW.glfwGetJoystickName(jid));
					joystickIsPresent[jid] = true;
				}
				break;
				
				case GLFW.GLFW_DISCONNECTED:
				{
					for (int i = 0; i < joystickListeners.size(); i++)
						joystickListeners.get(i).onJoystickDisconnect(jid);
					joystickIsPresent[jid] = false;
				}
				break;
			}
		});
		
		joystickIsPresent = new boolean[GLFW.GLFW_JOYSTICK_LAST + 1];
		joystickInputObjects = new JoystickInputObject[GLFW.GLFW_JOYSTICK_LAST + 1];
		for (int jid = 0; jid < joystickIsPresent.length; jid++)
		{
			if (!GLFW.glfwJoystickPresent(jid))
				continue;
			for (int i = 0; i < joystickListeners.size(); i++)
				joystickListeners.get(i).onJoystickConnect(jid, GLFW.glfwJoystickIsGamepad(jid), GLFW.glfwGetJoystickGUID(jid), GLFW.glfwGetJoystickName(jid));
		}
	}
	
	/**
	 * This method must be called in order to prep joysticks for polling.
	 * If joysticks are already enabled, this does nothing.
	 */
	public void disableJoysticks()
	{
		if (joystickCallbackHandle == null)
			return;
		joystickIsPresent = null;
		joystickInputObjects = null;
		joystickCallbackHandle.free();
		joystickCallbackHandle = null;
	}
	
	/**
	 * Polls all connected joysticks.
	 * <p><b>This must only be called from the main thread.</b>
	 */
	public void pollJoysticks()
	{
		if (joystickIsPresent == null)
			return;
		
		for (int jid = 0; jid < joystickIsPresent.length; jid++)
		{
			if (!joystickIsPresent[jid])
				continue;
			
			FloatBuffer axisData = GLFW.glfwGetJoystickAxes(jid);
			ByteBuffer buttonData = GLFW.glfwGetJoystickButtons(jid);
			ByteBuffer hatData = GLFW.glfwGetJoystickHats(jid);
			
			// TODO: Finish this.
		}
	}
	
	/**
	 * Fires a key event in this system. 
	 * @param type the key type.
	 * @param pressed true if pressed, false if released.
	 * @return true if the event was handled by an object, false if not. 
	 */
	public boolean fireKeyEvent(KeyType type, boolean pressed)
	{
		boolean handled = false;
		if (keyHandlers != null) for (int i = 0; i < keyHandlers.size(); i++)
			handled |= keyHandlers.get(i).fireKeyEvent(type, pressed);
		return handled;
	}
	
	/**
	 * Fires a key typed event in this system. 
	 * @param c the character typed.
	 * @return true if the event was handled by an object, false if not. 
	 */
	public boolean fireKeyTypedEvent(char c)
	{
		boolean handled = false;
		if (keyTypedHandlers != null) for (int i = 0; i < keyTypedHandlers.size(); i++)
			handled |= keyTypedHandlers.get(i).fireKeyTypedEvent(c);
		return handled;
	}
	
	/**
	 * Fires a mouse button event in this system. 
	 * @param type the mouse button type.
	 * @param pressed true if pressed, false if released.
	 * @return true if the event was handled by an object, false if not. 
	 */
	public boolean fireMouseButtonEvent(MouseButtonType type, boolean pressed)
	{
		boolean handled = false;
		if (mouseButtonHandlers != null) for (int i = 0; i < mouseButtonHandlers.size(); i++)
			handled |= mouseButtonHandlers.get(i).fireMouseButton(type, pressed);
		return handled;
	}
	
	/**
	 * Fires a mouse axis event in this system. 
	 * @param type the mouse axis type.
	 * @param amount the amount moved.
	 * @return true if the event was handled by an object, false if not. 
	 */
	public boolean fireMouseAxisEvent(MouseAxisType type, double amount)
	{
		boolean handled = false;
		if (mouseAxisHandlers != null) for (int i = 0; i < mouseAxisHandlers.size(); i++)
			handled |= mouseAxisHandlers.get(i).fireMouseAxis(type, amount);
		return handled;
	}
	
	/**
	 * Fires a mouse position event in this system. 
	 * @param type the mouse axis type.
	 * @param value the mouse position value.
	 * @return true if the event was handled by an object, false if not. 
	 */
	public boolean fireMousePositionEvent(MouseAxisType type, double value)
	{
		boolean handled = false;
		if (mousePositionHandlers != null) for (int i = 0; i < mousePositionHandlers.size(); i++)
			handled |= mousePositionHandlers.get(i).fireMousePosition(type, value);
		return handled;
	}
	
	/**
	 * Fires a mouse scroll event in this system. 
	 * @param type the mouse axis type.
	 * @param amount the scroll amount.
	 * @return true if the event was handled by an object, false if not. 
	 */
	public boolean fireMouseScrollEvent(MouseAxisType type, double amount)
	{
		boolean handled = false;
		if (mouseScrollHandlers != null) for (int i = 0; i < mouseScrollHandlers.size(); i++)
			handled |= mouseScrollHandlers.get(i).fireMouseScroll(type, amount);
		return handled;
	}
	
	/**
	 * Fires a gamepad button event in this system.
	 * @param jid the joystick id to fire to.
	 * @param type the gamepad button type.
	 * @param pressed true if pressed, false if released.
	 * @return true if the event was handled by an object, false if not. 
	 */
	public boolean fireGamepadButtonEvent(int jid, GamepadButtonType type, boolean pressed)
	{
		return joystickInputObjects[jid] == null ? false : joystickInputObjects[jid].fireGamepadButtonEvent(type, pressed);
	}
	
	/**
	 * Fires a gamepad axis event in this system. 
	 * @param jid the joystick id to fire to.
	 * @param type the gamepad axis type.
	 * @param amount the amount moved.
	 * @return true if the event was handled by an object, false if not. 
	 */
	public boolean fireGamepadAxisEvent(int jid, GamepadAxisType type, double amount)
	{
		return joystickInputObjects[jid] == null ? false : joystickInputObjects[jid].fireGamepadAxisEvent(type, amount);
	}
	
	/**
	 * Fires a joystick hat event in this system. 
	 * @param jid the joystick id to fire to.
	 * @param index the hat index on the device.
	 * @param type the joystick hat type.
	 * @return true if the event was handled by an object, false if not. 
	 */
	public boolean fireJoystickHatEvent(int jid, int index, JoystickHatType type)
	{
		return joystickInputObjects[jid] == null ? false : joystickInputObjects[jid].fireJoystickHatEvent(index, type);
	}
	
	// Creates an empty list if list is null, else return parameter.
	private static <T> List<T> makeIfNull(List<T> map)
	{
		return map != null ? map : new ArrayList<>(4);
	}
	
	/**
	 * Sets the value of a field on an object.
	 * @param instance the object instance to set the field on.
	 * @param field the field to set.
	 * @param value the value to set.
	 * @throws NullPointerException if the field or object provided is null.
	 * @throws ClassCastException if the value could not be cast to the proper type.
	 * @throws InputDispatchException if anything goes wrong (bad field name, 
	 * 		bad target, bad argument, or can't access the field).
	 * @see Field#set(Object, Object)
	 */
	private static void setFieldValue(Object instance, Field field, Object value)
	{
		try {
			field.set(instance, value);
		} catch (ClassCastException ex) {
			throw ex;
		} catch (SecurityException e) {
			throw new InputDispatchException("Could not process event:", e);
		} catch (IllegalArgumentException e) {
			throw new InputDispatchException("Could not process event:", e);
		} catch (IllegalAccessException e) {
			throw new InputDispatchException("Could not process event:", e);
		}
	}

	/**
	 * Blindly invokes a method, only throwing a {@link RuntimeException} if
	 * something goes wrong. Here for the convenience of not making a billion
	 * try/catch clauses for a method invocation.
	 * @param method the method to invoke.
	 * @param instance the object instance that is the method target.
	 * @param params the parameters to pass to the method.
	 * @return the return value from the method invocation. If void, this is null.
	 * @throws ClassCastException if one of the parameters could not be cast to the proper type.
	 * @throws InputDispatchException if anything goes wrong (bad target, bad argument, or can't access the method).
	 * @see Method#invoke(Object, Object...)
	 */
	private static Object invokeBlind(Object instance, Method method, Object ... params)
	{
		Object out = null;
		try {
			out = method.invoke(instance, params);
		} catch (ClassCastException ex) {
			throw ex;
		} catch (InvocationTargetException ex) {
			throw new InputDispatchException("Could not process event:", ex);
		} catch (IllegalArgumentException ex) {
			throw new InputDispatchException("Could not process event:", ex);
		} catch (IllegalAccessException ex) {
			throw new InputDispatchException("Could not process event:", ex);
		}
		return out;
	}

	/**
	 * Checks if a value is "empty."
	 * The following is considered "empty":
	 * <ul>
	 * <li><i>Null</i> references.
	 * <li>{@link Collection} objects where {@link Collection#isEmpty()} returns true.
	 * </ul> 
	 * @param obj the object to check.
	 * @return true if the provided object is considered "empty", false otherwise.
	 */
	private static boolean isEmpty(Object obj)
	{
		if (obj == null)
			return true;
		else if (obj instanceof Map<?, ?>)
			return ((Map<?, ?>)obj).isEmpty();
		else
			return false;
	}

	/**
	 * Combined listeners for this.
	 */
	private class ListenerFilter implements WindowListener, InputListener
	{
		/** Last mouse position, X */
		private double lastMousePositionX;
		/** Last mouse position, Y */
		private double lastMousePositionY;

		@Override
		public void onBlur(GLFWWindow window)
		{
			lastMousePositionX = Double.NaN;
			lastMousePositionY = Double.NaN;
		}

		@Override
		public void onIconify(GLFWWindow window)
		{
			lastMousePositionX = Double.NaN;
			lastMousePositionY = Double.NaN;
		}
		
		@Override
		public void onMouseExited(GLFWWindow window)
		{
			lastMousePositionX = Double.NaN;
			lastMousePositionY = Double.NaN;
		}

		@Override
		public void onClose(GLFWWindow window)
		{
			// Do nothing.
		}

		@Override
		public void onRefresh(GLFWWindow window)
		{
			// Do nothing.
		}

		@Override
		public void onFocus(GLFWWindow window)
		{
			// Do nothing.
		}

		@Override
		public void onRestore(GLFWWindow window)
		{
			// Do nothing.
		}

		@Override
		public void onMaximize(GLFWWindow window)
		{
			// Do nothing.
		}

		@Override
		public void onMouseEntered(GLFWWindow window)
		{
			// Do nothing.
		}

		@Override
		public void onPositionChange(GLFWWindow window, int x, int y)
		{
			// Do nothing.
		}

		@Override
		public void onSizeChange(GLFWWindow window, int width, int height)
		{
			// Do nothing.
		}

		@Override
		public void onFramebufferChange(GLFWWindow window, int width, int height)
		{
			// Do nothing.
		}

		@Override
		public void onContentScaleChange(GLFWWindow window, float x, float y)
		{
			// Do nothing.
		}
		
		@Override
		public void onKeyPress(GLFWWindow window, int glfwKey, int scanCode, int modFlags)
		{
			KeyType type;
			if ((type = KeyType.getById(glfwKey)) != null)
				fireKeyEvent(type, true);
		}

		@Override
		public void onKeyRelease(GLFWWindow window, int glfwKey, int scanCode, int modFlags)
		{
			KeyType type;
			if ((type = KeyType.getById(glfwKey)) != null)
				fireKeyEvent(type, false);
		}

		@Override
		public void onKeyRepeated(GLFWWindow window, int glfwKey, int scanCode, int modFlags)
		{
			// Do nothing.
		}

		@Override
		public void onKeyTyped(GLFWWindow window, char c)
		{
			fireKeyTypedEvent(c);
		}

		@Override
		public void onMousePosition(GLFWWindow window, double x, double y)
		{
			if (!Double.isNaN(lastMousePositionX))
				fireMouseAxisEvent(MouseAxisType.X, x - lastMousePositionX);
			if (!Double.isNaN(lastMousePositionY))
				fireMouseAxisEvent(MouseAxisType.Y, y - lastMousePositionY);
			
			fireMousePositionEvent(MouseAxisType.X, x);
			fireMousePositionEvent(MouseAxisType.Y, y);
			
			lastMousePositionX = x;
			lastMousePositionY = y;
		}

		@Override
		public void onMouseButtonPress(GLFWWindow window, int glfwButton, int modFlags)
		{
			MouseButtonType type;
			if ((type = MouseButtonType.getById(glfwButton)) != null)
				fireMouseButtonEvent(type, true);
		}
		
		@Override
		public void onMouseButtonRelease(GLFWWindow window, int glfwButton, int modFlags)
		{
			MouseButtonType type;
			if ((type = MouseButtonType.getById(glfwButton)) != null)
				fireMouseButtonEvent(type, false);
		}

		@Override
		public void onScroll(GLFWWindow window, double x, double y)
		{
			fireMouseScrollEvent(MouseAxisType.X, x);
			fireMouseScrollEvent(MouseAxisType.Y, y);
		}
		
	}
	
	/**
	 * A single processed annotated input object to direct events to.
	 * @author Matthew Tropiano
	 */
	private static class InputObjectProfile
	{
		private Object instance;
		
		private Map<KeyType, Field> keyFields;
		private Map<KeyType, Method> keyMethods;
		private Map<MouseButtonType, Field> mouseButtonFields;
		private Map<MouseButtonType, Method> mouseButtonMethods;
		private Map<MouseAxisType, Field> mouseAxisFields;
		private Map<MouseAxisType, Method> mouseAxisMethods;
		private Map<MouseAxisType, Field> mousePositionFields;
		private Map<MouseAxisType, Method> mousePositionMethods;
		private Map<MouseAxisType, Field> mouseScrollFields;
		private Map<MouseAxisType, Method> mouseScrollMethods;
		private Method keyEventMethod;
		private Method keyTypedEventMethod;
		private Method mouseButtonEventMethod;
		private Method mouseAxisEventMethod;
		private Method mousePositionEventMethod;
		private Method mouseScrollEventMethod;
		
		private Map<GamepadButtonType, Field> gamepadButtonFields;
		private Map<GamepadButtonType, Method> gamepadButtonMethods;
		private Map<GamepadAxisType, Field> gamepadAxisFields;
		private Map<GamepadAxisType, Method> gamepadAxisMethods;
		private Map<Integer, Field> joystickHatFields;
		private Map<Integer, Method> joystickHatMethods;
		private Method gamepadButtonEventMethod;
		private Method gamepadAxisEventMethod;
		private Method joystickHatEventMethod;
		
		InputObjectProfile(Object instance)
		{
			Class<?> clazz = instance.getClass();
			for (Field f : clazz.getFields())
			{
				if (!isPotentialMember(f))
					continue;
				
				annotationFill(
					isValidKeyInputField(f), 
					(anno)->anno.value(), 
					(value)->(keyFields = makeIfNull(keyFields)).put(value, f)
				);
				annotationFill(
					isValidMouseButtonInputField(f), 
					(anno)->anno.value(), 
					(value)->(mouseButtonFields = makeIfNull(mouseButtonFields)).put(value, f)
				);
				annotationFill(
					isValidMouseAxisInputField(f), 
					(anno)->anno.value(), 
					(value)->(mouseAxisFields = makeIfNull(mouseAxisFields)).put(value, f)
				);
				annotationFill(
					isValidMousePositionInputField(f), 
					(anno)->anno.value(), 
					(value)->(mousePositionFields = makeIfNull(mousePositionFields)).put(value, f)
				);
				annotationFill(
					isValidMouseScrollInputField(f), 
					(anno)->anno.value(), 
					(value)->(mouseScrollFields = makeIfNull(mouseScrollFields)).put(value, f)
				);
				annotationFill(
					isValidGamepadButtonInputField(f), 
					(anno)->anno.value(), 
					(value)->(gamepadButtonFields = makeIfNull(gamepadButtonFields)).put(value, f)
				);
				annotationFill(
					isValidGamepadAxisInputField(f), 
					(anno)->anno.value(), 
					(value)->(gamepadAxisFields = makeIfNull(gamepadAxisFields)).put(value, f)
				);
				annotationFill(
					isValidJoystickHatInputField(f), 
					(anno)->anno.value(), 
					(value)->(joystickHatFields = makeIfNull(joystickHatFields)).put(value, f)
				);
			}
			
			for (Method m : clazz.getMethods())
			{
				if (!isPotentialMember(m))
					continue;
				
				annotationFill(
					isValidKeyInputSetterMethod(m), 
					(anno)->anno.value(), 
					(value)->(keyMethods = makeIfNull(keyMethods)).put(value, m)
				);
				annotationFill(
					isValidMouseButtonInputSetterMethod(m), 
					(anno)->anno.value(), 
					(value)->(mouseButtonMethods = makeIfNull(mouseButtonMethods)).put(value, m)
				);
				annotationFill(
					isValidMouseAxisInputSetterMethod(m), 
					(anno)->anno.value(), 
					(value)->(mouseAxisMethods = makeIfNull(mouseAxisMethods)).put(value, m)
				);
				annotationFill(
					isValidMousePositionInputSetterMethod(m), 
					(anno)->anno.value(), 
					(value)->(mousePositionMethods = makeIfNull(mousePositionMethods)).put(value, m)
				);
				annotationFill(
					isValidMouseScrollInputSetterMethod(m), 
					(anno)->anno.value(), 
					(value)->(mouseScrollMethods = makeIfNull(mouseScrollMethods)).put(value, m)
				);
				annotationFill(
					isValidGamepadButtonInputSetterMethod(m), 
					(anno)->anno.value(), 
					(value)->(gamepadButtonMethods = makeIfNull(gamepadButtonMethods)).put(value, m)
				);
				annotationFill(
					isValidGamepadAxisInputSetterMethod(m), 
					(anno)->anno.value(), 
					(value)->(gamepadAxisMethods = makeIfNull(gamepadAxisMethods)).put(value, m)
				);
				annotationFill(
					isValidJoystickHatInputSetterMethod(m), 
					(anno)->anno.value(), 
					(value)->(joystickHatMethods = makeIfNull(joystickHatMethods)).put(value, m)
				);

				if (isValidKeyEventMethod(m) != null)
					keyEventMethod = m;
				if (isValidKeyTypedEventMethod(m) != null)
					keyTypedEventMethod = m;
				if (isValidMouseButtonEventMethod(m) != null)
					mouseButtonEventMethod = m;
				if (isValidMouseAxisEventMethod(m) != null)
					mouseAxisEventMethod = m;
				if (isValidMousePositionEventMethod(m) != null)
					mousePositionEventMethod = m;
				if (isValidMouseWheelEventMethod(m) != null)
					mouseScrollEventMethod = m;
				if (isValidGamepadButtonEventMethod(m) != null)
					gamepadButtonEventMethod = m;
				if (isValidGamepadAxisEventMethod(m) != null)
					gamepadAxisEventMethod = m;
				if (isValidJoystickHatEventMethod(m) != null)
					joystickHatEventMethod = m;
			}
			
			this.instance = instance;
		}
		
		/**
		 * Checks if this object handles key events.
		 * @return true if so, false if not. 
		 */
		public boolean handlesKeys()
		{
			return !isEmpty(keyFields) || !isEmpty(keyMethods) || !isEmpty(keyEventMethod);
		}
		
		/**
		 * Checks if this object handles key typed events.
		 * @return true if so, false if not. 
		 */
		public boolean handlesKeyTyping()
		{
			return !isEmpty(keyTypedEventMethod);
		}
		
		/**
		 * Checks if this object handles mouse button events.
		 * @return true if so, false if not. 
		 */
		public boolean handlesMouseButtons()
		{
			return !isEmpty(mouseButtonFields) || !isEmpty(mouseButtonMethods) || !isEmpty(mouseButtonEventMethod);
		}
		
		/**
		 * Checks if this object handles mouse axis events.
		 * @return true if so, false if not. 
		 */
		public boolean handlesMouseAxes()
		{
			return !isEmpty(mouseAxisFields) || !isEmpty(mouseAxisMethods) || !isEmpty(mouseAxisEventMethod);
		}
		
		/**
		 * Checks if this object handles mouse position events.
		 * @return true if so, false if not. 
		 */
		public boolean handlesMousePositions()
		{
			return !isEmpty(mousePositionFields) || !isEmpty(mousePositionMethods) || !isEmpty(mousePositionEventMethod);
		}
		
		/**
		 * Checks if this object handles mouse scroll events.
		 * @return true if so, false if not. 
		 */
		public boolean handlesMouseScroll()
		{
			return !isEmpty(mouseScrollFields) || !isEmpty(mouseScrollMethods) || !isEmpty(mouseScrollEventMethod);
		}
		
		/**
		 * Checks if this object handles gamepad button events.
		 * @return true if so, false if not. 
		 */
		public boolean handlesGamepadButtons()
		{
			return !isEmpty(gamepadButtonFields) || !isEmpty(gamepadButtonMethods) || !isEmpty(gamepadButtonEventMethod);
		}
		
		/**
		 * Checks if this object handles gamepad axis events.
		 * @return true if so, false if not. 
		 */
		public boolean handlesGamepadAxes()
		{
			return !isEmpty(gamepadAxisFields) || !isEmpty(gamepadAxisMethods) || !isEmpty(gamepadAxisEventMethod);
		}
		
		/**
		 * Checks if this object handles joystick hat events.
		 * @return true if so, false if not. 
		 */
		public boolean handlesJoystickHats()
		{
			return !isEmpty(joystickHatFields) || !isEmpty(joystickHatMethods) || !isEmpty(joystickHatEventMethod);
		}
		
		/**
		 * Fires a key event to this object.
		 * @param type the key type.
		 * @param pressed true if pressed, false if released.
		 * @return true if handled by this object, false if not. 
		 * @throws InputDispatchException if a field or method could not be invoked.
		 */
		public boolean fireKeyEvent(KeyType type, boolean pressed)
		{
			return fireEvent(keyFields, keyMethods, keyEventMethod, type, pressed);
		}
		
		/**
		 * Fires a key typed event to this object.
		 * @param c the character typed.
		 * @return true if handled by this object, false if not. 
		 * @throws InputDispatchException if a field or method could not be invoked.
		 */
		public boolean fireKeyTypedEvent(char c)
		{
			if (keyTypedEventMethod != null)
			{
				invokeBlind(instance, keyTypedEventMethod, c);
				return true;
			}
			return false;
		}
		
		/**
		 * Fires a mouse button event to this object.
		 * @param type the mouse button type.
		 * @param pressed true if pressed, false if released.
		 * @return true if handled by this object, false if not. 
		 * @throws InputDispatchException if a field or method could not be invoked.
		 */
		public boolean fireMouseButton(MouseButtonType type, boolean pressed)
		{
			return fireEvent(mouseButtonFields, mouseButtonMethods, mouseButtonEventMethod, type, pressed);
		}
		
		/**
		 * Fires a mouse axis event to this object.
		 * @param type the mouse axis type.
		 * @param amount the movement amount on the axis.
		 * @return true if handled by this object, false if not. 
		 * @throws InputDispatchException if a field or method could not be invoked.
		 */
		public boolean fireMouseAxis(MouseAxisType type, double amount)
		{
			return fireEvent(mouseAxisFields, mouseAxisMethods, mouseAxisEventMethod, type, amount);
		}
		
		/**
		 * Fires a mouse position event to this object.
		 * @param type the mouse axis type.
		 * @param amount the new position on the axis.
		 * @return true if handled by this object, false if not. 
		 * @throws InputDispatchException if a field or method could not be invoked.
		 */
		public boolean fireMousePosition(MouseAxisType type, double amount)
		{
			return fireEvent(mousePositionFields, mousePositionMethods, mousePositionEventMethod, type, amount);
		}
		
		/**
		 * Fires a mouse scroll event to this object.
		 * @param type the mouse axis type.
		 * @param amount the scroll amount on the axis.
		 * @return true if handled by this object, false if not. 
		 * @throws InputDispatchException if a field or method could not be invoked.
		 */
		public boolean fireMouseScroll(MouseAxisType type, double amount)
		{
			return fireEvent(mouseScrollFields, mouseScrollMethods, mouseScrollEventMethod, type, amount);
		}
		
		/**
		 * Fires a gamepad button event to this object.
		 * @param type the gamepad button type.
		 * @param pressed true if pressed, false if released.
		 * @return true if handled by this object, false if not. 
		 * @throws InputDispatchException if a field or method could not be invoked.
		 */
		public boolean fireGamepadButton(GamepadButtonType type, boolean pressed)
		{
			return fireEvent(gamepadButtonFields, gamepadButtonMethods, gamepadButtonEventMethod, type, pressed);
		}
		
		/**
		 * Fires a gamepad axis event to this object.
		 * @param type the gamepad axis type.
		 * @param value the movement on the axis.
		 * @return true if handled by this object, false if not. 
		 * @throws InputDispatchException if a field or method could not be invoked.
		 */
		public boolean fireGamepadAxis(GamepadAxisType type, double value)
		{
			return fireEvent(gamepadAxisFields, gamepadAxisMethods, gamepadAxisEventMethod, type, value);
		}
		
		/**
		 * Fires a joystick hat event to this object.
		 * @param index the hat index on the device.
		 * @param type the joystick hat type.
		 * @return true if handled by this object, false if not. 
		 * @throws InputDispatchException if a field or method could not be invoked.
		 */
		public boolean fireJoystickHat(int index, JoystickHatType type)
		{
			return fireEvent(joystickHatFields, joystickHatMethods, joystickHatEventMethod, index, type);
		}
		
		// Fires an event to the handlers.
		private <T> boolean fireEvent(Map<T, Field> fieldMap, Map<T, Method> methodMap, Method eventMethod, T type, Object value)
		{
			return fireEvent(
				fieldMap != null ? fieldMap.get(type) : null, 
				methodMap != null ? methodMap.get(type) : null, 
				eventMethod, type, value
			);
		}

		// Fires an event to the handlers.
		private <T> boolean fireEvent(Field field, Method method, Method eventMethod, T type, Object value)
		{
			boolean handled = false;
			if (field != null)
			{
				setFieldValue(instance, field, value);
				handled = true;
			}
			if (method != null)
			{
				invokeBlind(instance, method, value);
				handled = true;
			}
			if (eventMethod != null)
			{
				invokeBlind(instance, eventMethod, type, value);
				handled = true;
			}
			
			return handled;
		}

		// Checks if a field is a potential input target.
		private static boolean isPotentialMember(Field field)
		{
			int modifiers = field.getModifiers();
			return 
				(modifiers & Modifier.PUBLIC) != 0
				&& (modifiers & Modifier.STATIC) == 0
			;
		}
		
		// Checks if a method is a potential input target.
		private static boolean isPotentialMember(Method method)
		{
			int modifiers = method.getModifiers();
			return 
				(modifiers & Modifier.PUBLIC) != 0
				&& (modifiers & Modifier.STATIC) == 0
			;
		}
		
		// Checks for a field annotation and type.
		private static <A extends Annotation> A checkInputFieldAnnotationType(Field field, Class<A> annotationClass, Class<?> validFieldClass)
		{
			A annotation;
			if ((annotation = field.getAnnotation(annotationClass)) == null)
				return null;
			
			Class<?> type = field.getType();
			if (type == validFieldClass)
				return annotation;
			
			throw new InputSetupException(
				"Field " + field.toString() + " is annotated with " + 
				annotationClass.getSimpleName() + " but does not have one of the following types: " + 
				validFieldClass.getSimpleName()
			);
		}
		
		// Checks for a method annotation and parameter types.
		private static <A extends Annotation> A checkInputMethodAnnotationType(Method method, Class<A> annotationClass, Class<?> ... validMethodParameters)
		{
			A annotation;
			if ((annotation = method.getAnnotation(annotationClass)) == null)
				return null;
			
			Class<?>[] parameterTypes = method.getParameterTypes();
			
			if (validMethodParameters.length != parameterTypes.length)
			{
				throw new InputSetupException(
					"Method " + method.toString() + " is annotated with " + 
					annotationClass.getSimpleName() + " but has the incorrect amount of parameters. Requires: " + 
					Arrays.toString(validMethodParameters)
				);
			}
			
			for (int i = 0; i < parameterTypes.length; i++)
			{
				if (validMethodParameters[i] != parameterTypes[i])
				{
					throw new InputSetupException(
						"Method " + method.toString() + " is annotated with " + 
						annotationClass.getSimpleName() + " but does not have one of the following types: " + 
						Arrays.toString(validMethodParameters)
					);
				}
			}
			return annotation;
		}
			
		// Checks if a field is suitable for using as a target for key actions.
		// See @InputKey.
		private static InputKey isValidKeyInputField(Field field)
		{
			return checkInputFieldAnnotationType(field, InputKey.class, Boolean.TYPE);
		}

		// Checks if a field is suitable for using as a target for mouse button actions.
		// See @InputMouseButton.
		private static InputMouseButton isValidMouseButtonInputField(Field field)
		{
			return checkInputFieldAnnotationType(field, InputMouseButton.class, Boolean.TYPE);
		}

		// Checks if a field is suitable for using as a target for mouse axis actions.
		// See @InputMouseAxis.
		private static InputMouseAxis isValidMouseAxisInputField(Field field)
		{
			return checkInputFieldAnnotationType(field, InputMouseAxis.class, Double.TYPE);
		}

		// Checks if a field is suitable for using as a target for mouse position actions.
		// See @InputMousePosition.
		private static InputMousePosition isValidMousePositionInputField(Field field)
		{
			return checkInputFieldAnnotationType(field, InputMousePosition.class, Double.TYPE);
		}

		// Checks if a method is suitable for using as a target for mouse wheel actions.
		// See @InputMouseWheel.
		private static InputMouseScroll isValidMouseScrollInputField(Field field)
		{
			return checkInputFieldAnnotationType(field, InputMouseScroll.class, Double.TYPE);
		}

		// Checks if a field is suitable for using as a target for gamepad button actions.
		// See @InputGamepadButton.
		private static InputGamepadButton isValidGamepadButtonInputField(Field field)
		{
			return checkInputFieldAnnotationType(field, InputGamepadButton.class, Boolean.TYPE);
		}

		// Checks if a field is suitable for using as a target for gamepad axis actions.
		// See @InputGamepadAxis.
		private static InputGamepadAxis isValidGamepadAxisInputField(Field field)
		{
			return checkInputFieldAnnotationType(field, InputGamepadAxis.class, Boolean.TYPE);
		}

		// Checks if a field is suitable for using as a target for joystick hat actions.
		// See @InputJoystickHat.
		private static InputJoystickHat isValidJoystickHatInputField(Field field)
		{
			return checkInputFieldAnnotationType(field, InputJoystickHat.class, JoystickHatType.class);
		}

		// Checks if a method is suitable for using as a target for key actions.
		// See @InputKey.
		private static InputKey isValidKeyInputSetterMethod(Method method)
		{
			return checkInputMethodAnnotationType(method, InputKey.class, Boolean.TYPE);
		}

		// Checks if a method is suitable for using as a target for mouse button actions.
		// See @InputMouseButton.
		private static InputMouseButton isValidMouseButtonInputSetterMethod(Method method)
		{
			return checkInputMethodAnnotationType(method, InputMouseButton.class, Boolean.TYPE);
		}

		// Checks if a method is suitable for using as a target for mouse axis actions.
		// See @InputMouseAxis.
		private static InputMouseAxis isValidMouseAxisInputSetterMethod(Method method)
		{
			return checkInputMethodAnnotationType(method, InputMouseAxis.class, Double.TYPE);
		}

		// Checks if a method is suitable for using as a target for mouse position actions.
		// See @InputMousePosition.
		private static InputMousePosition isValidMousePositionInputSetterMethod(Method method)
		{
			return checkInputMethodAnnotationType(method, InputMousePosition.class, Double.TYPE);
		}

		// Checks if a method is suitable for using as a target for mouse wheel actions.
		// See @InputMouseWheel.
		private static InputMouseScroll isValidMouseScrollInputSetterMethod(Method method)
		{
			return checkInputMethodAnnotationType(method, InputMouseScroll.class, Double.TYPE);
		}

		// Checks if a method is suitable for using as a target for gamepad button actions.
		// See @InputGamepadButton.
		private static InputGamepadButton isValidGamepadButtonInputSetterMethod(Method method)
		{
			return checkInputMethodAnnotationType(method, InputGamepadButton.class, Boolean.TYPE);
		}

		// Checks if a method is suitable for using as a target for gamepad axis actions.
		// See @InputGamepadAxis.
		private static InputGamepadAxis isValidGamepadAxisInputSetterMethod(Method method)
		{
			return checkInputMethodAnnotationType(method, InputGamepadAxis.class, Double.TYPE);
		}

		// Checks if a method is suitable for using as a target for joystick hat actions.
		// See @InputJoystickHat.
		private static InputJoystickHat isValidJoystickHatInputSetterMethod(Method method)
		{
			return checkInputMethodAnnotationType(method, InputJoystickHat.class, JoystickHatType.class);
		}

		// Checks if a method is suitable for using as a target for key actions.
		// See @OnKeyAction.
		private static OnKeyAction isValidKeyEventMethod(Method method)
		{
			return checkInputMethodAnnotationType(method, OnKeyAction.class, KeyType.class, Boolean.TYPE);
		}

		// Checks if a method is suitable for using as a target for key typing actions.
		// See @OnKeyTypedAction.
		private static OnKeyTypedAction isValidKeyTypedEventMethod(Method method)
		{
			return checkInputMethodAnnotationType(method, OnKeyTypedAction.class, Character.TYPE);
		}

		// Checks if a method is suitable for using as a target for mouse button actions.
		// See @OnMouseButtonAction.
		private static OnMouseButtonAction isValidMouseButtonEventMethod(Method method)
		{
			return checkInputMethodAnnotationType(method, OnMouseButtonAction.class, MouseButtonType.class, Boolean.TYPE);
		}

		// Checks if a method is suitable for using as a target for mouse axis actions.
		// See @OnMouseAxisAction.
		private static OnMouseAxisAction isValidMouseAxisEventMethod(Method method)
		{
			return checkInputMethodAnnotationType(method, OnMouseAxisAction.class, MouseAxisType.class, Double.TYPE);
		}

		// Checks if a method is suitable for using as a target for mouse position actions.
		// See @OnMousePositionAction.
		private static OnMousePositionAction isValidMousePositionEventMethod(Method method)
		{
			return checkInputMethodAnnotationType(method, OnMousePositionAction.class, MouseAxisType.class, Double.TYPE);
		}

		// Checks if a method is suitable for using as a target for mouse wheel actions.
		// See @OnMouseWheelAction.
		private static OnMouseScrollAction isValidMouseWheelEventMethod(Method method)
		{
			return checkInputMethodAnnotationType(method, OnMouseScrollAction.class, MouseAxisType.class, Double.TYPE);
		}

		// Checks if a method is suitable for using as a target for gamepad button actions.
		// See @OnGamepadButtonAction.
		private static OnGamepadButtonAction isValidGamepadButtonEventMethod(Method method)
		{
			return checkInputMethodAnnotationType(method, OnGamepadButtonAction.class, GamepadButtonType.class, Boolean.TYPE);
		}

		// Checks if a method is suitable for using as a target for gamepad axis actions.
		// See @OnGamepadAxisAction.
		private static OnGamepadAxisAction isValidGamepadAxisEventMethod(Method method)
		{
			return checkInputMethodAnnotationType(method, OnGamepadAxisAction.class, GamepadAxisType.class, Double.TYPE);
		}

		// Checks if a method is suitable for using as a target for joystick hat actions.
		// See @OnJoystickHatAction.
		private static OnJoystickHatAction isValidJoystickHatEventMethod(Method method)
		{
			return checkInputMethodAnnotationType(method, OnJoystickHatAction.class, Integer.TYPE, JoystickHatType.class);
		}

		// Creates an empty map if map is null, else return parameter.
		private static <A extends Annotation, K, M> void annotationFill(A annotation, Function<A, K> valueExtractor, Function<K, M> filler)
		{
			if (annotation != null)
				filler.apply(valueExtractor.apply(annotation));
		}
		
		// Creates an empty map if map is null, else return parameter.
		private static <K, V> Map<K, V> makeIfNull(Map<K, V> map)
		{
			return map != null ? map : new HashMap<>(4);
		}
		
	}
	
}

package com.blackrook.gloop.glfw.input;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import com.blackrook.gloop.glfw.input.enums.GamepadAxisType;
import com.blackrook.gloop.glfw.input.enums.GamepadButtonType;
import com.blackrook.gloop.glfw.input.enums.JoystickHatType;
import com.blackrook.gloop.glfw.input.enums.KeyType;
import com.blackrook.gloop.glfw.input.enums.MouseAxisType;
import com.blackrook.gloop.glfw.input.enums.MouseButtonType;
import com.blackrook.gloop.glfw.input.exception.InputDispatchException;

/**
 * A single processed annotated input object to direct events to.
 * @author Matthew Tropiano
 */
public class InputObjectProfile
{
	private Object instance;
	
	private Map<KeyType, Field> keyFields;
	private Map<KeyType, Method> keyMethods;
	private Map<MouseButtonType, Field> mouseButtonFields;
	private Map<MouseButtonType, Method> mouseButtonMethods;
	private Map<MouseAxisType, Field> mouseAxisFields;
	private Map<MouseAxisType, Method> mouseAxisMethods;
	private Map<GamepadButtonType, Field> gamepadButtonFields;
	private Map<GamepadButtonType, Method> gamepadButtonMethods;
	private Map<GamepadAxisType, Field> gamepadAxisFields;
	private Map<GamepadAxisType, Method> gamepadAxisMethods;
	private Map<JoystickHatType, Field> joystickHatFields;
	private Map<JoystickHatType, Method> joystickHatMethods;
	
	private Method keyEventMethod;
	private Method mouseButtonEventMethod;
	private Method mouseAxisEventMethod;
	private Method gamepadButtonEventMethod;
	private Method gamepadAxisEventMethod;
	private Method joystickHatEventMethod;
	private Method changeEventMethod;
	
	InputObjectProfile(Object instance)
	{
		Class<?> clazz = instance.getClass();
		for (Field f : clazz.getFields())
		{
			// ignore non-public, non-static
			if ((f.getModifiers() & Modifier.PUBLIC) == 0)
				continue;
			if ((f.getModifiers() & Modifier.STATIC) != 0)
				continue;
			
			// TODO: Finish.
		}
		
		for (Method m : clazz.getMethods())
		{
			// ignore non-public, non-static
			if ((m.getModifiers() & Modifier.PUBLIC) == 0)
				continue;
			if ((m.getModifiers() & Modifier.STATIC) != 0)
				continue;
			
			// TODO: Finish.	
		}
		
		this.instance = instance;
	}
	
	/**
	 * Checks if this object handles key events.
	 * @return true if so, false if not. 
	 */
	public boolean handlesKeys()
	{
		return !keyFields.isEmpty() || !keyMethods.isEmpty() || keyEventMethod != null;
	}
	
	/**
	 * Checks if this object handles mouse button events.
	 * @return true if so, false if not. 
	 */
	public boolean handlesMouseButtons()
	{
		return !mouseButtonFields.isEmpty() || !mouseButtonMethods.isEmpty() || mouseButtonEventMethod != null;
	}
	
	/**
	 * Checks if this object handles mouse axis events.
	 * @return true if so, false if not. 
	 */
	public boolean handlesMouseAxes()
	{
		return !mouseAxisFields.isEmpty() || !mouseAxisMethods.isEmpty() || mouseAxisEventMethod != null;
	}
	
	/**
	 * Checks if this object handles gamepad button events.
	 * @return true if so, false if not. 
	 */
	public boolean handlesGamepadButtons()
	{
		return !gamepadButtonFields.isEmpty() || !gamepadButtonMethods.isEmpty() || gamepadButtonEventMethod != null;
	}
	
	/**
	 * Checks if this object handles gamepad axis events.
	 * @return true if so, false if not. 
	 */
	public boolean handlesGamepadAxes()
	{
		return !gamepadAxisFields.isEmpty() || !gamepadAxisMethods.isEmpty() || gamepadAxisEventMethod != null;
	}
	
	/**
	 * Checks if this object handles joystick hat events.
	 * @return true if so, false if not. 
	 */
	public boolean handlesJoystickHats()
	{
		return !joystickHatFields.isEmpty() || !joystickHatMethods.isEmpty() || joystickHatEventMethod != null;
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
	
	// TODO: Finish this.
	
	// Fires an event to the handlers.
	private <T> boolean fireEvent(Map<T, Field> fieldMap, Map<T, Method> methodMap, Method eventMethod, T type, Object value)
	{
		boolean handled = false;
		Field field;
		if ((field = fieldMap.get(type)) != null)
		{
			setFieldValue(instance, field, value);
			handled = true;
		}
	
		Method method;
		if ((method = methodMap.get(type)) != null)
		{
			invokeBlind(instance, method, value);
			handled = true;
		}
		if ((method = eventMethod) != null)
		{
			invokeBlind(instance, method, type, value);
			handled = true;
		}
		
		if (handled && (method = changeEventMethod) != null)
			invokeBlind(instance, method);
		
		return handled;
	}

	// Checks if a field is suitable for using as a target for key actions.
	// See @InputKey.
	private static boolean isValidKeyInputField(Field field)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a field is suitable for using as a target for mouse button actions.
	// See @InputMouseButton.
	private static boolean isValidMouseButtonInputField(Field field)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a field is suitable for using as a target for mouse axis actions.
	// See @InputMouseAxis.
	private static boolean isValidMouseAxisInputField(Field field)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a method is suitable for using as a target for mouse wheel actions.
	// See @InputMouseWheel.
	private static boolean isValidMouseWheelInputField(Field field)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a field is suitable for using as a target for gamepad button actions.
	// See @InputGamepadButton.
	private static boolean isValidGamepadButtonInputField(Field field)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a field is suitable for using as a target for gamepad axis actions.
	// See @InputGamepadAxis.
	private static boolean isValidGamepadAxisInputField(Field field)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a field is suitable for using as a target for joystick hat actions.
	// See @OnGamepadJoystickHatAction.
	private static boolean isValidJoystickHatInputField(Field field)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a method is suitable for using as a target for key actions.
	// See @InputKey.
	private static boolean isValidKeyInputSetterMethod(Method method)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a method is suitable for using as a target for mouse button actions.
	// See @InputMouseButton.
	private static boolean isValidMouseButtonInputSetterMethod(Method method)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a method is suitable for using as a target for mouse axis actions.
	// See @InputMouseAxis.
	private static boolean isValidMouseAxisInputSetterMethod(Method method)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a method is suitable for using as a target for mouse wheel actions.
	// See @InputMouseWheel.
	private static boolean isValidMouseWheelInputSetterMethod(Method method)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a method is suitable for using as a target for gamepad button actions.
	// See @InputGamepadButton.
	private static boolean isValidGamepadButtonInputSetterMethod(Method method)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a method is suitable for using as a target for gamepad axis actions.
	// See @InputGamepadAxis.
	private static boolean isValidGamepadAxisInputSetterMethod(Method method)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a method is suitable for using as a target for joystick hat actions.
	// See @OnGamepadJoystickHatAction.
	private static boolean isValidJoystickHatInputSetterMethod(Method method)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a method is suitable for using as a target for key actions.
	// See @OnKeyAction.
	private static boolean isValidKeyEventMethod(Method method)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a method is suitable for using as a target for mouse button actions.
	// See @OnMouseButtonAction.
	private static boolean isValidMouseButtonEventMethod(Method method)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a method is suitable for using as a target for mouse axis actions.
	// See @OnMouseAxisAction.
	private static boolean isValidMouseAxisEventMethod(Method method)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a method is suitable for using as a target for mouse wheel actions.
	// See @OnMouseWheelAction.
	private static boolean isValidMouseWheelEventMethod(Method method)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a method is suitable for using as a target for gamepad button actions.
	// See @OnGamepadButtonAction.
	private static boolean isValidGamepadButtonEventMethod(Method method)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a method is suitable for using as a target for gamepad axis actions.
	// See @OnGamepadAxisAction.
	private static boolean isValidGamepadAxisEventMethod(Method method)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a method is suitable for using as a target for joystick hat actions.
	// See @OnGamepadJoystickHatAction.
	private static boolean isValidJoystickHatEventMethod(Method method)
	{
		// TODO: Finish this.
		return false;
	}

	// Checks if a method is suitable for using as a target for calling after any change.
	// See @OnChange.
	private static boolean isValidChangeEventMethod(Method method)
	{
		// TODO: Finish this.
		return false;
	}

	/**
	 * Sets the value of a field on an object.
	 * @param instance the object instance to set the field on.
	 * @param field the field to set.
	 * @param value the value to set.
	 * @throws NullPointerException if the field or object provided is null.
	 * @throws ClassCastException if the value could not be cast to the proper type.
	 * @throws InputDispatchException if anything goes wrong (bad field name, 
	 * bad target, bad argument, or can't access the field).
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
	
}


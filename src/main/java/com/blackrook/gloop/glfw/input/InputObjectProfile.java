package com.blackrook.gloop.glfw.input;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.blackrook.gloop.glfw.input.annotation.InputGamepadAxis;
import com.blackrook.gloop.glfw.input.annotation.InputGamepadButton;
import com.blackrook.gloop.glfw.input.annotation.InputJoystickHat;
import com.blackrook.gloop.glfw.input.annotation.InputKey;
import com.blackrook.gloop.glfw.input.annotation.InputMouseAxis;
import com.blackrook.gloop.glfw.input.annotation.InputMouseButton;
import com.blackrook.gloop.glfw.input.annotation.InputMouseScroll;
import com.blackrook.gloop.glfw.input.annotation.OnChange;
import com.blackrook.gloop.glfw.input.annotation.OnGamepadAxisAction;
import com.blackrook.gloop.glfw.input.annotation.OnGamepadButtonAction;
import com.blackrook.gloop.glfw.input.annotation.OnJoystickHatAction;
import com.blackrook.gloop.glfw.input.annotation.OnKeyAction;
import com.blackrook.gloop.glfw.input.annotation.OnMouseAxisAction;
import com.blackrook.gloop.glfw.input.annotation.OnMouseButtonAction;
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
	private Map<MouseAxisType, Field> mouseScrollFields;
	private Map<MouseAxisType, Method> mouseScrollMethods;
	private Map<GamepadButtonType, Field> gamepadButtonFields;
	private Map<GamepadButtonType, Method> gamepadButtonMethods;
	private Map<GamepadAxisType, Field> gamepadAxisFields;
	private Map<GamepadAxisType, Method> gamepadAxisMethods;
	private Map<Integer, Field> joystickHatFields;
	private Map<Integer, Method> joystickHatMethods;
	
	private Method keyEventMethod;
	private Method mouseButtonEventMethod;
	private Method mouseAxisEventMethod;
	private Method mouseScrollEventMethod;
	private Method gamepadButtonEventMethod;
	private Method gamepadAxisEventMethod;
	private Method joystickHatEventMethod;
	private Method changeEventMethod;
	
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
			if (isValidMouseButtonEventMethod(m) != null)
				mouseButtonEventMethod = m;
			if (isValidMouseAxisEventMethod(m) != null)
				mouseAxisEventMethod = m;
			if (isValidMouseWheelEventMethod(m) != null)
				mouseScrollEventMethod = m;
			if (isValidGamepadButtonEventMethod(m) != null)
				gamepadButtonEventMethod = m;
			if (isValidGamepadAxisEventMethod(m) != null)
				gamepadAxisEventMethod = m;
			if (isValidJoystickHatEventMethod(m) != null)
				joystickHatEventMethod = m;
			if (isValidChangeEventMethod(m) != null)
				changeEventMethod = m;
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
	
	/**
	 * Fires an "input changed" event to this object.
	 * @return true if handled by this object, false if not. 
	 * @throws InputDispatchException if a field or method could not be invoked.
	 */
	public boolean fireChange()
	{
		if (changeEventMethod != null)
		{
			invokeBlind(instance, changeEventMethod);
			return true;
		}
		return false;
	}
	
	// Fires an event to the handlers.
	private <T> boolean fireEvent(Map<T, Field> fieldMap, Map<T, Method> methodMap, Method eventMethod, T type, Object value)
	{
		return fireEvent(fieldMap.get(type), methodMap.get(type), eventMethod, type, value);
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

	// Checks if a method is suitable for using as a target for calling after any change.
	// See @OnChange.
	private static OnChange isValidChangeEventMethod(Method method)
	{
		return checkInputMethodAnnotationType(method, OnChange.class);
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
	
}

package com.blackrook.gloop.glfw.input.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.blackrook.gloop.glfw.input.JoystickHatType;

/**
 * Annotates a field that sets if a joystick hat is in a position or not.
 * Expects to write to a boolean value; an error will be thrown if this is not the case.
 * <p>Public fields or methods that are "setters" can be annotated with this.
 * @author Matthew Tropiano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface InputJoystickHat
{
	/**
	 * @return the corresponding joystick hat position.
	 */
	JoystickHatType value();
}

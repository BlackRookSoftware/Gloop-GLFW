package com.blackrook.gloop.glfw.input.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.blackrook.gloop.glfw.input.GamepadAxisType;

/**
 * Annotates a field that sets if a gamepad axis is moved by a certain amount of units.
 * Expects to write to a floating-point value; an error will be thrown if this is not the case.
 * <p>Public fields or methods that are "setters" can be annotated with this.
 * @author Matthew Tropiano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface InputGamepadAxis
{
	/**
	 * @return the corresponding gamepad axis.
	 */
	GamepadAxisType value();
}

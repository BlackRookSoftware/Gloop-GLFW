package com.blackrook.gloop.glfw.input.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.blackrook.gloop.glfw.input.enums.JoystickAxisType;

/**
 * Annotates a field that calls a method if a joystick axis is moved.
 * Expects to call a method that takes a {@link JoystickAxisType} and a double parameter; an error will be thrown if this is not the case.
 * <p>The {@link JoystickAxisType} is the axis the action happened on, and the double is the amount. 
 * @author Matthew Tropiano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnJoystickAxisAction
{
}
package com.blackrook.gloop.glfw.input.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.blackrook.gloop.glfw.input.enums.JoystickHatType;

/**
 * Annotates a field that calls a method if a joystick hat is moved.
 * Expects to call a method that takes an integer and a {@link JoystickHatType} parameter; an error will be thrown if this is not the case.
 * <p>The integer is the hat index and the JoystickHatType is the new position. 
 * @author Matthew Tropiano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnJoystickHatAction
{
}

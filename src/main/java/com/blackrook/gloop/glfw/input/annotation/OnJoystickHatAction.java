package com.blackrook.gloop.glfw.input.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a field that calls a method if a joystick hat is moved.
 * Expects to call a method that takes two integer parameters; an error will be thrown if this is not the case.
 * <p>The first integer is the device id, and the second is the new position. 
 * @author Matthew Tropiano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnJoystickHatAction
{
}

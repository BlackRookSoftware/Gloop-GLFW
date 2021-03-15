package com.blackrook.gloop.glfw.input.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.blackrook.gloop.glfw.input.enums.GamepadAxisType;

/**
 * Annotates a field that calls a method if a gamepad axis is moved.
 * Expects to call a method that takes an integer, a {@link GamepadAxisType}, and a float parameter; an error will be thrown if this is not the case.
 * <p>The integer is the device id, the {@link GamepadAxisType} is the axis the action happened on, and the float is the amount. 
 * @author Matthew Tropiano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnGamepadAxisAction
{
}

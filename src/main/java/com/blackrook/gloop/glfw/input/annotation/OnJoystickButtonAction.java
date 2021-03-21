package com.blackrook.gloop.glfw.input.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.blackrook.gloop.glfw.input.enums.JoystickButtonType;

/**
 * Annotates a field that calls a method if a joystick button is pressed or released.
 * Expects to call a method that takes both a {@link JoystickButtonType} and a boolean parameter; an error will be thrown if this is not the case.
 * <p>The {@link JoystickButtonType} is the button the action happened on, and the boolean is true on press and false on release.
 * @author Matthew Tropiano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnJoystickButtonAction
{
}

package com.blackrook.gloop.glfw.input.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.blackrook.gloop.glfw.input.MouseButtonType;

/**
 * Annotates a field that calls a method if a mouse button is pressed or released.
 * Expects to call a method that takes both a {@link MouseButtonType} and a boolean parameter; an error will be thrown if this is not the case.
 * <p>The {@link MouseButtonType} is the button the action happened on, and the boolean is true on press and false on release. 
 * @author Matthew Tropiano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnMouseButtonAction
{
}

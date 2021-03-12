package com.blackrook.gloop.glfw.input.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a field that calls a method if a mouse wheel is moved.
 * Expects to call a method that takes an integer parameter; an error will be thrown if this is not the case.
 * <p>The integer is the amount of the move. 
 * @author Matthew Tropiano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnMouseWheelAction
{
}

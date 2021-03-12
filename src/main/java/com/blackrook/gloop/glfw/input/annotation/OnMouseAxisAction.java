package com.blackrook.gloop.glfw.input.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.blackrook.gloop.glfw.input.MouseAxisType;

/**
 * Annotates a field that calls a method if a mouse axis is moved.
 * Expects to call a method that takes both a {@link MouseAxisType} and an integer parameter; an error will be thrown if this is not the case.
 * <p>The {@link MouseAxisType} is the axis the action happened on, and the integer is the amount. 
 * @author Matthew Tropiano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnMouseAxisAction
{
}

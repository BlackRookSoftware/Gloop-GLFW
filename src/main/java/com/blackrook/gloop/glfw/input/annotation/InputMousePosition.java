package com.blackrook.gloop.glfw.input.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.blackrook.gloop.glfw.input.enums.MouseAxisType;

/**
 * Annotates a field that sets if the mouse's position on an axis changes, reporting the new window-relative position.
 * Expects to write to a double value; an error will be thrown if this is not the case.
 * <p>Public fields or methods that are "setters" can be annotated with this.
 * @author Matthew Tropiano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface InputMousePosition
{
	/**
	 * @return the corresponding mouse axis.
	 */
	MouseAxisType value();
}

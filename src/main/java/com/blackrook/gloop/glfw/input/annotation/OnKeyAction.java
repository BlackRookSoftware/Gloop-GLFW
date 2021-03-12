package com.blackrook.gloop.glfw.input.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.blackrook.gloop.glfw.input.KeyType;

/**
 * Annotates a field that calls a method if a key is pressed or released.
 * Expects to call a method that takes both a {@link KeyType} and a boolean parameter; an error will be thrown if this is not the case.
 * <p>The {@link KeyType} is the key the action happened on, and the boolean is true on press and false on release. 
 * @author Matthew Tropiano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnKeyAction
{
}

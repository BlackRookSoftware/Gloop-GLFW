package com.blackrook.gloop.glfw.input.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a field that calls a method if a key is typed (or repeated type).
 * Expects to call a method that takes a <code>char</code>; an error will be thrown if this is not the case.
 * <p>The <code>char</code> is the key the action happened on, and the boolean is true on press and false on release. 
 * @author Matthew Tropiano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnKeyTypedAction
{
}

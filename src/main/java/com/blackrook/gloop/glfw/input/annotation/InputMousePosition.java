/*******************************************************************************
 * Copyright (c) 2020-2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
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

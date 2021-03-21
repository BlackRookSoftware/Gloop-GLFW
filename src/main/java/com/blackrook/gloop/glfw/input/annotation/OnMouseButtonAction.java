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

import com.blackrook.gloop.glfw.input.enums.MouseButtonType;

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

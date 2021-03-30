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

import com.blackrook.gloop.glfw.input.enums.JoystickAxisType;
import com.blackrook.gloop.glfw.input.enums.JoystickDirectionType;

/**
 * Annotates a field that calls a method if a joystick axis is moved into a discrete state from deadzone.
 * Expects to call a method that takes a {@link JoystickAxisType} and a {@link JoystickDirectionType} parameter; an error will be thrown if this is not the case.
 * <p>The {@link JoystickAxisType} is the axis the action happened on, and the {@link JoystickDirectionType} is the direction. 
 * @author Matthew Tropiano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnJoystickDirectionAction
{
}

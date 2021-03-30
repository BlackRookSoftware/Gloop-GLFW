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
 * Annotates a field that sets if a joystick axis is moved into a discrete direction.
 * The joystick axis only enters the "centered" state if it hits the device deadzone.
 * Expects to write to a {@link JoystickDirectionType} value; an error will be thrown if this is not the case.
 * <p>Public fields or methods that are "setters" can be annotated with this.
 * @author Matthew Tropiano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface InputJoystickDirection
{
	/**
	 * @return the corresponding joystick axis.
	 */
	JoystickAxisType value();
}

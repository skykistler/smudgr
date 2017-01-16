package io.smudgr.app.controller;

import io.smudgr.app.project.ProjectItem;
import io.smudgr.app.project.util.PropertyMap;

/**
 * Interface for defining a controllable action, behavior, etc for use
 * throughout the app.
 */
public interface Controllable extends ProjectItem {

	/**
	 * Key to reference controls by in the property map.
	 * Currently set to {@value}.
	 * 
	 * @see PropertyMap
	 */
	public static final String PROPERTY_MAP_KEY = "control";

	/**
	 * Unique name to identify this control by.
	 * 
	 * @return The name of this controllable.
	 */
	public String getName();

	/**
	 * Trigger an inputed value on this control. For generic use with devices
	 * or programs that may return a discrete value from some source of input.
	 * 
	 * @param value
	 *            Any integer representing the input of some source.
	 */
	public void inputValue(int value);

	/**
	 * Trigger an 'on' input event on this control. For generic use with devices
	 * or programs that may return boolean state information from some source of
	 * input.
	 * 
	 * @see Controllable#inputOff()
	 */
	public void inputOn();

	/**
	 * Trigger an 'off' input event on this control. For generic use with
	 * devices
	 * or programs that may return boolean state information from some source of
	 * input.
	 * 
	 * @see Controllable#inputOn()
	 */
	public void inputOff();

	/**
	 * Trigger an increment event on this control. For generic use with devices
	 * or programs that may return incremental events from some source of input.
	 * 
	 * @see Controllable#decrement()
	 */
	public void increment();

	/**
	 * Trigger a decrement event on this control. For generic use with devices
	 * or programs that may return incremental events from some source of input.
	 * 
	 * @see Controllable#increment()
	 */
	public void decrement();

}

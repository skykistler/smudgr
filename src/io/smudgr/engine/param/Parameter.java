package io.smudgr.engine.param;

import io.smudgr.app.controller.Controllable;
import io.smudgr.app.project.ProjectItem;
import io.smudgr.app.project.util.PropertyMap;

/**
 * The abstract {@link Parameter} class defines the generic behavior for
 * interacting with an arbitrarily typed value.
 * <p>
 * {@link Parameter} implements {@link Controllable}, which defines an interface
 * for reacting to input source events.
 *
 * @see Controllable
 */
public abstract class Parameter implements Controllable, ProjectItem {

	@Override
	public String getTypeName() {
		return "Parameter";
	}

	@Override
	public String getTypeIdentifier() {
		return "parameter";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getIdentifier() {
		return name;
	}

	private String name;
	private Parametric parent;

	/**
	 * Instantiate a new {@link Parameter} with the given name and
	 * {@link Parameter} parent.
	 *
	 * @param name
	 *            Parameter name.
	 * @param parent
	 *            {@link Parametric} parent.
	 */
	public Parameter(String name, Parametric parent) {
		this.name = name;
		this.parent = parent;
		this.parent.addParameter(this);
	}

	/**
	 * Implement this method to set an arbitrarily typed value using a given
	 * {@link Object}
	 *
	 * @param o
	 *            {@link Object} arbitrarily typed object
	 */
	protected abstract void setValueFromObject(Object o);

	/**
	 * Set the value of this parameter given a generic {@link Object}
	 *
	 * @param o
	 *            {@link Object}
	 */
	public void setValue(Object o) {
		setValue(o, null);
	}

	/**
	 * Set the value of this parameter given a generic {@link Object}, with a
	 * {@link ParameterObserver} specified to be ignored.
	 * <p>
	 * This is used when a {@link ParameterObserver} needs to update a parameter
	 * without causing an infinite stack overflow.
	 *
	 * @param o
	 *            {@link Object}
	 * @param ignoreObserver
	 *            {@link ParameterObserver}
	 * @see Parameter#setValue(Object)
	 * @see ParameterObserver
	 */
	public void setValue(Object o, ParameterObserver ignoreObserver) {
		setValueFromObject(o);

		if (getProject() != null && getProject().getParameterObserverNotifier() != null)
			getProject().getParameterObserverNotifier().notify(this, ignoreObserver);
	}

	/**
	 * Get the string representation of this parameter.
	 *
	 * @return {@link String} representation of this parameter.
	 */
	public abstract String getStringValue();

	@Override
	public void save(PropertyMap pm) {
		pm.setAttribute("type", getIdentifier());
		pm.setAttribute("value", getStringValue());
	}

	@Override
	public void load(PropertyMap pm) {
		if (pm.hasAttribute("value"))
			setValue(pm.getAttribute("value"));
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Gets the {@link Parametric} parent of this {@link Parameter}
	 *
	 * @return {@link Parametric}
	 */
	public Parametric getParent() {
		return parent;
	}

}

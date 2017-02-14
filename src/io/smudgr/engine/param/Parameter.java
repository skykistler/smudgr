package io.smudgr.engine.param;

import io.smudgr.app.controller.Controllable;
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
public abstract class Parameter implements Controllable {

	@Override
	public String getTypeCategoryName() {
		return "Parameter";
	}

	@Override
	public String getTypeCategoryIdentifier() {
		return "parameter";
	}

	/**
	 * Gets the user-recognizable parameter name. This may change beween
	 * versions.
	 *
	 * @return {@link String} Name of the parameter
	 * @see #getParameterIdentifier()
	 */
	public String getParameterName() {
		return name;
	}

	/**
	 * Gets the unique string that identifies this parameter between versions
	 * and changes to the user-recognizable name.
	 * <p>
	 * This will be the parameter name by default.
	 *
	 * @return {@link String} Parameter type identifier
	 */
	public String getParameterIdentifier() {
		return identifier;
	}

	private String name;
	private String identifier;
	private Parametric parent;

	/**
	 * Instantiate a totally empty {@link Parameter}, for reflection.
	 * <p>
	 * Because parameters are instantiated in the code and not by loaded states,
	 * an empty constructor is required to allow parameter type (i.e. number,
	 * boolean, etc) enumeration.
	 * <p>
	 * This could be fixed in the future by instantiating parameters in
	 * {@link Parametric} instances via super methods instead of directly. The
	 * super methods could call setters and return the correct {@link Parameter}
	 * instance, instead of relying on {@link Parametric} implementations to do
	 * this properly.
	 */
	public Parameter() {
	}

	/**
	 * Instantiate a new {@link Parameter} with the given name and
	 * {@link Parameter} parent.
	 *
	 * @param name
	 *            Parameter name.
	 * @param identifier
	 *            If the parameter name changes between versions, this property
	 *            can be set for backwards compatibility for save files.
	 * @param parent
	 *            {@link Parametric} parent.
	 */
	public Parameter(String name, String identifier, Parametric parent) {
		this.name = name;
		this.identifier = identifier;
		this.parent = parent;
		this.parent.addParameter(this);
	}

	/**
	 * Instantiate a new {@link Parameter} with the given name and
	 * {@link Parameter} parent.
	 *
	 * @param name
	 *            Parameter user-identifiable name.
	 * @param parent
	 *            {@link Parametric} parent
	 */
	public Parameter(String name, Parametric parent) {
		this(name, name, parent);
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
		Controllable.super.save(pm);

		pm.setAttribute("value", getStringValue());
	}

	@Override
	public void load(PropertyMap pm) {
		Controllable.super.load(pm);

		if (pm.hasAttribute("value"))
			setValue(pm.getAttribute("value"));
	}

	@Override
	public String toString() {
		return getTypeName();
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

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
	public String getTypeName() {
		return "Parameter";
	}

	@Override
	public String getTypeIdentifier() {
		return "parameter";
	}

	/**
	 * Gets the user-recognizable parameter type name
	 *
	 * @return {@link String} Name of the parameter type this class implements
	 */
	public abstract String getParameterTypeName();

	/**
	 * Gets the unique identifying name of the parameter type this class
	 * implements.
	 *
	 * @return {@link String} Parameter type identifier
	 */
	public abstract String getParameterTypeIdentifier();

	@Override
	public String getElementName() {
		return name;
	}

	/**
	 * The default identifier for a parameter is just its name, but in the case
	 * of changing display names, the identifier may be an older name for
	 * backwards compatibility.
	 */
	@Override
	public String getElementIdentifier() {
		return identifier;
	}

	private String name;
	private String identifier;
	private Parametric parent;

	/**
	 * Instantiate a totally empty {@link Parameter}, for reflection purposes.
	 */
	public Parameter() {
		name = getParameterTypeName();
		identifier = getParameterTypeIdentifier();
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
		return getElementName();
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

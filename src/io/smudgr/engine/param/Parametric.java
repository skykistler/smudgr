package io.smudgr.engine.param;

import java.util.Collection;
import java.util.HashMap;

import io.smudgr.app.project.ProjectItem;
import io.smudgr.app.project.reflect.TypeLibrary;
import io.smudgr.app.project.util.PropertyMap;

/**
 * The abstract {@link Parametric} class defines generic behavior for
 * {@link ProjectItem} implementations that contain and use {@link Parameter}
 * parameters.
 */
public abstract class Parametric implements ProjectItem {

	private static TypeLibrary<Parameter> parameterLibrary = new TypeLibrary<Parameter>(Parameter.class);

	private HashMap<String, Parameter> parameters = new HashMap<String, Parameter>();

	/**
	 * Add a parameter to be tracked and used by this {@link Parametric}
	 *
	 * @param p
	 *            {@link Parameter} to add
	 */
	public void addParameter(Parameter p) {
		parameters.put(p.getParameterIdentifier(), p);

		if (getProject() != null)
			getProject().add(p);
	}

	/**
	 * Gets a {@link Parameter} added to this {@link Parametric} using its
	 * identifier.
	 *
	 * @param identifier
	 *            {@link String}
	 * @return {@link Parameter} or {@code null} if none exists by given name.
	 * @see Parameter#getTypeIdentifier()
	 */
	public Parameter getParameter(String identifier) {
		return parameters.get(identifier);
	}

	/**
	 * Gets a {@link Parameter} added to this {@link Parametric} using its
	 * identifier.
	 *
	 * @param state
	 *            {@link PropertyMap} with {@link Parameter#PARAMETER_ID_ATTR}
	 * @return {@link Parameter} or {@code null} if none exists by given name.
	 * @see Parameter#getTypeIdentifier()
	 */
	public Parameter getParameter(PropertyMap state) {
		return getParameter(state.getAttribute(Parameter.PARAMETER_ID_ATTR));
	}

	/**
	 * Gets list of all {@link Parameter} instances used by this
	 * {@link Parametric}
	 *
	 * @return {@code Collection<Parameter>}
	 */
	public Collection<Parameter> getParameters() {
		return parameters.values();
	}

	/**
	 * Prints a list of all {@link Parameter} instances used by this
	 * {@link Parametric}
	 */
	public void listParameters() {
		for (Parameter p : parameters.values())
			System.out.println(p);
	}

	/**
	 * This method is called when a parameter has been changed. This method can
	 * be optionally implemented to listen to parameter changes.
	 */
	public void triggerChange() {

	}

	@Override
	public void save(PropertyMap pm) {
		ProjectItem.super.save(pm);

		for (Parameter param : getParameters())
			pm.add(new PropertyMap(param));
	}

	@Override
	public void load(PropertyMap pm) {
		ProjectItem.super.load(pm);

		for (PropertyMap map : pm.getChildren(parameterLibrary)) {
			Parameter param = getParameter(map);

			// If this Parametric doesn't have a Parameter instance with ID from
			// this map, don't load it.
			if (param == null)
				continue;

			param.load(map);
		}
	}

	@Override
	public String toString() {
		return getTypeName();
	}

}

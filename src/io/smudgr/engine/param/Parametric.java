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
		parameters.put(p.getTypeIdentifier(), p);

		if (getProject() != null)
			getProject().add(p);
	}

	/**
	 * Gets a {@link Parameter} added to this {@link Parametric} by identifier.
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

		for (Parameter param : getParameters()) {
			PropertyMap map = new PropertyMap(param);
			param.save(map);

			pm.add(map);
		}
	}

	@Override
	public void load(PropertyMap pm) {
		ProjectItem.super.load(pm);

		for (PropertyMap map : pm.getChildren(parameterLibrary)) {
			Parameter param = getParameter(map.getAttribute(PropertyMap.TYPE_ATTR));

			if (param == null)
				continue;

			param.load(map);
		}

		// Add any unloaded parameters to the project
		for (Parameter param : getParameters()) {
			if (getProject().getId(param) == -1)
				getProject().add(param);
		}
	}

	@Override
	public String toString() {
		return getTypeName();
	}

}

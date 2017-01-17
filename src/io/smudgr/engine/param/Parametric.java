package io.smudgr.engine.param;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import io.smudgr.app.project.ProjectItem;
import io.smudgr.app.project.util.PropertyMap;

/**
 * The abstract {@link Parametric} class defines generic behavior for
 * {@link ProjectItem} implementations that contain and use {@link Parameter}
 * parameters.
 */
public abstract class Parametric implements ProjectItem {

	private HashMap<String, Parameter> parameters = new HashMap<String, Parameter>();

	/**
	 * Add a parameter to be tracked and used by this {@link Parametric}
	 * 
	 * @param p
	 *            {@link Parameter} to add
	 */
	public void addParameter(Parameter p) {
		parameters.put(p.getName(), p);

		if (getProject() != null)
			getProject().add(p);
	}

	/**
	 * Gets a {@link Parameter} added to this {@link Parametric} by name.
	 * 
	 * @param name
	 *            {@link String}
	 * @return {@link Parameter} or {@code null} if none exists by given name.
	 */
	public Parameter getParameter(String name) {
		return parameters.get(name);
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
		pm.setAttribute("id", getProject().getId(this));

		for (Parameter param : getParameters()) {
			PropertyMap map = new PropertyMap(Parameter.PROJECT_MAP_TAG);

			map.setAttribute("id", getProject().getId(param));
			map.setAttribute("name", param.getName());

			param.save(map);

			pm.add(map);
		}
	}

	@Override
	public void load(PropertyMap pm) {
		if (pm.hasAttribute("id"))
			getProject().put(this, Integer.parseInt(pm.getAttribute("id")));
		else
			getProject().add(this);

		ArrayList<PropertyMap> children = pm.getChildren(Parameter.PROJECT_MAP_TAG);
		for (PropertyMap map : children) {
			Parameter param = getParameter(map.getAttribute("name"));

			if (param != null) {
				getProject().put(param, Integer.parseInt(map.getAttribute("id")));

				param.load(map);
			}
		}

		// Add any unloaded parameters to the project
		for (Parameter param : getParameters()) {
			if (getProject().getId(param) == -1)
				getProject().add(param);
		}
	}

	/**
	 * Gets the identifying name of this {@link Parametric}
	 * 
	 * @return {@link String} identifying name
	 */
	public abstract String getName();

	@Override
	public String toString() {
		return getName();
	}

}

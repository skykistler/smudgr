package io.smudgr.project.smudge.param;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import io.smudgr.project.ProjectElement;
import io.smudgr.project.PropertyMap;

public abstract class Parametric implements ProjectElement {

	private HashMap<String, Parameter> parameters = new HashMap<String, Parameter>();

	public void addParameter(Parameter p) {
		parameters.put(p.getName(), p);

		if (getProject() != null)
			getProject().add(p);
	}

	public Parameter getParameter(String name) {
		return parameters.get(name);
	}

	public Collection<Parameter> getParameters() {
		return (Collection<Parameter>) parameters.values();
	}

	public void listParameters() {
		for (Parameter p : parameters.values())
			System.out.println(p);
	}

	public void triggerChange() {

	}

	public void save(PropertyMap pm) {
		pm.setAttribute("id", getProject().getId(this));

		for (Parameter param : getParameters()) {
			PropertyMap map = new PropertyMap(Parameter.PROPERTY_MAP_KEY);

			map.setAttribute("id", getProject().getId(param));
			map.setAttribute("name", param.getName());

			param.save(map);

			pm.add(map);
		}
	}

	public void load(PropertyMap pm) {
		if (pm.hasAttribute("id"))
			getProject().put(this, Integer.parseInt(pm.getAttribute("id")));
		else
			getProject().add(this);

		ArrayList<PropertyMap> children = pm.getChildren(Parameter.PROPERTY_MAP_KEY);
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

	public abstract String getName();

	public String toString() {
		return getName();
	}

}

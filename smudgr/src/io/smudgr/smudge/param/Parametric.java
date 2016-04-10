package io.smudgr.smudge.param;

import java.util.Collection;
import java.util.HashMap;

import io.smudgr.app.ProjectIdManager.HasProjectId;
import io.smudgr.controller.BaseController;

public abstract class Parametric implements HasProjectId {

	private HashMap<String, Parameter> parameters = new HashMap<String, Parameter>();

	public void addParameter(Parameter p) {
		parameters.put(p.getName(), p);

		BaseController.getInstance().add(p);
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

	public void bind(String parameterName) {
		Parameter p = getParameter(parameterName);

		if (p != null)
			p.requestBind();
		else
			System.out.println("Could not find parameter: " + parameterName);
	}

	public void triggerChange() {
		// Left empty to allow optional inheritance
	}

	public abstract String getName();

	public String toString() {
		return getName();
	}

}

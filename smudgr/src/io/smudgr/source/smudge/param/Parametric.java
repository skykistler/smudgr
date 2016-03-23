package io.smudgr.source.smudge.param;

import java.util.Collection;
import java.util.HashMap;

import io.smudgr.controller.Controller;

public abstract class Parametric {

	private HashMap<String, Parameter> parameters = new HashMap<String, Parameter>();

	public void addParameter(Parameter p) {
		parameters.put(p.getName(), p);
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

	public void setController(Controller c) {
		if (c == null)
			return;

		for (Parameter p : parameters.values())
			c.add(p);
	}

	public void triggerChange() {
		// Left empty to allow optional inheritance
	}

	public abstract String getName();

	public String toString() {
		return getName();
	}

}

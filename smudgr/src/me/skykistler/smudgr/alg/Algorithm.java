package me.skykistler.smudgr.alg;

import java.util.Collection;
import java.util.HashMap;

import me.skykistler.smudgr.alg.param.Parameter;
import me.skykistler.smudgr.view.View;
import processing.core.PImage;

public abstract class Algorithm {
	private HashMap<String, Parameter> parameters = new HashMap<String, Parameter>();

	public abstract void execute(View processor, PImage img);

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
	}

	public abstract String getName();

	public String toString() {
		return getName();
	}

}

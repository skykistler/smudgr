package io.smudgr.alg;

import java.util.Collection;
import java.util.HashMap;

import io.smudgr.alg.bound.Bound;
import io.smudgr.alg.param.Parameter;
import io.smudgr.view.View;
import processing.core.PImage;

public abstract class Algorithm {
	private HashMap<String, Parameter> parameters = new HashMap<String, Parameter>();
	private View processor;
	private Bound bound;

	public void init(View processor) {
		this.setView(processor);

		if (bound == null)
			applyMask(new Bound(1, 1));
	};

	public abstract void execute(PImage img);

	public PImage mask(PImage frame, PImage mix, Bound mask) {
		if (mask == null)
			return mix;

		for (int i = 0; i < mix.width; i++)
			for (int j = 0; j < mix.height; j++)
				// If the pixel is within the mask, overwrite the frame pixel
				if (mask.containsPoint(mix, i, j)) {
					int index = i + j * mix.width;
					frame.pixels[index] = mix.pixels[index];
				}

		return frame;
	}

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

	public void applyMask(Bound bound) {
		this.bound = bound;
	}

	public Bound getMask() {
		return bound;
	}

	public abstract String getName();

	public String toString() {
		return getName();
	}

	public View getView() {
		return processor;
	}

	public void setView(View processor) {
		this.processor = processor;
	}

}

package io.smudgr.alg;

import java.util.Collection;
import java.util.HashMap;

import io.smudgr.Smudge;
import io.smudgr.alg.bound.Bound;
import io.smudgr.alg.coord.CoordFunction;
import io.smudgr.alg.param.DoubleParameter;
import io.smudgr.alg.param.Parameter;
import io.smudgr.model.Frame;

public abstract class Algorithm {
	private Smudge parent;
	private HashMap<String, Parameter> parameters = new HashMap<String, Parameter>();
	private Bound bound;

	private DoubleParameter boundX;
	private DoubleParameter boundY;
	private DoubleParameter boundW;
	private DoubleParameter boundH;
	private CoordFunction coordFunction;

	protected Frame img;

	public Algorithm(Smudge s) {
		parent = s;
		parent.addAlgorithm(this);

		boundX = new DoubleParameter(this, "Bound X", 0, 0, 1, 0.005);
		boundY = new DoubleParameter(this, "Bound Y", 0, 0, 1, 0.005);
		boundW = new DoubleParameter(this, "Bound Width", 1, 0, 1, 0.005);
		boundH = new DoubleParameter(this, "Bound Height", 1, 0, 1, 0.005);
	}

	public void init() {
		if (bound == null)
			applyMask(new Bound(1, 1));

		if (coordFunction != null)
			coordFunction.setBound(bound);
	}

	public void apply(Frame img) {
		double x = boundX.getValue();
		double y = boundY.getValue();
		double w = boundW.getValue();
		double h = boundH.getValue();

		boolean boundChanged = false;
		if (x != bound.getOffsetX() || y != bound.getOffsetY() || w != bound.getWidth() || h != bound.getHeight()) {
			bound.setOffsetX(x);
			bound.setOffsetY(y);
			bound.setWidth(w);
			bound.setHeight(h);
			boundChanged = true;
		}

		boolean imgSizeChanged = this.img == null || (img.getWidth() != this.img.getWidth() || img.getHeight() != this.img.getHeight());
		if (coordFunction != null)
			if (boundChanged || imgSizeChanged) {
				coordFunction.setBound(bound);
				coordFunction.setImage(img);
				coordFunction.update();
			}

		this.img = img;

		execute(img);
	}

	public abstract void execute(Frame img);

	public Frame mask(Frame frame, Frame mix, Bound mask) {
		if (mask == null)
			return mix;

		for (int i = 0; i < mix.getWidth(); i++)
			for (int j = 0; j < mix.getHeight(); j++)
				// If the pixel is within the mask, overwrite the frame pixel
				if (mask.containsPoint(mix, i, j)) {
					int index = i + j * mix.getWidth();
					frame.pixels[index] = mix.pixels[index];
				}

		return frame;
	}

	public Smudge getParent() {
		return parent;
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

	public void setCoordFunction(CoordFunction cf) {
		coordFunction = cf;
		coordFunction.setBound(bound);
		coordFunction.init(this);
	}

	public CoordFunction getCoordFunction() {
		return coordFunction;
	}

	public abstract String getName();

	public String toString() {
		return (coordFunction == null ? "" : coordFunction + " ") + getName();
	}

}

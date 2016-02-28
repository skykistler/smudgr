package io.smudgr.source.smudge.alg;

import java.util.ArrayList;

import io.smudgr.controller.Controller;
import io.smudgr.source.Frame;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.source.smudge.alg.bound.Bound;
import io.smudgr.source.smudge.alg.coord.AllCoords;
import io.smudgr.source.smudge.alg.coord.CoordFunction;
import io.smudgr.source.smudge.alg.op.Operation;
import io.smudgr.source.smudge.param.BooleanParameter;
import io.smudgr.source.smudge.param.Parametric;

public class Algorithm extends Parametric {

	private Smudge parent;

	private ArrayList<AlgorithmComponent> components = new ArrayList<AlgorithmComponent>();
	private Bound bound;
	private CoordFunction coordFunction;
	protected Frame lastFrame;

	private BooleanParameter enable = new BooleanParameter("Enable", this, true);

	public Algorithm() {
		add(new Bound(1, 1));
		add(new AllCoords());
	}

	public void init() {
		for (AlgorithmComponent c : components)
			c.init();
	}

	private double lastBoundX;
	private double lastBoundY;
	private double lastBoundW;
	private double lastBoundH;

	public void update() {

	}

	public void apply(Frame img) {
		if (!enable.getValue())
			return;

		boolean boundChanged = lastBoundX != bound.getOffsetX() || lastBoundY != bound.getOffsetY() || lastBoundW != bound.getWidth() || lastBoundH != bound.getHeight();
		boolean imgSizeChanged = lastFrame == null || (lastFrame.getWidth() != this.lastFrame.getWidth() || img.getHeight() != this.lastFrame.getHeight());

		if (imgSizeChanged || boundChanged) {
			coordFunction.setBound(bound);
			coordFunction.setImage(img);
			coordFunction.update();
		}

		for (AlgorithmComponent component : components) {
			if (component instanceof Operation)
				((Operation) component).apply(img);
		}

		lastFrame = img;
	}

	public void add(AlgorithmComponent component) {
		component.setAlgorithm(this);

		if (component instanceof Bound)
			setBound((Bound) component);

		if (component instanceof CoordFunction)
			setCoordFunction((CoordFunction) component);

		if (component instanceof Operation)
			setOperation((Operation) component);

	}

	private void setBound(Bound bound) {
		if (bound == null)
			return;

		ArrayList<AlgorithmComponent> otherBounds = new ArrayList<AlgorithmComponent>();

		for (AlgorithmComponent component : components)
			if (component instanceof CoordFunction)
				otherBounds.add(component);

		components.remove(otherBounds);
		components.add(bound);

		this.bound = bound;

		if (coordFunction != null) {
			coordFunction.setBound(bound);
			coordFunction.update();
		}
	}

	public Bound getBound() {
		return bound;
	}

	private void setCoordFunction(CoordFunction cf) {
		if (cf == null)
			return;

		ArrayList<AlgorithmComponent> otherCoordFunctions = new ArrayList<AlgorithmComponent>();

		for (AlgorithmComponent component : components)
			if (component instanceof CoordFunction)
				otherCoordFunctions.add(component);

		components.remove(otherCoordFunctions);
		components.add(cf);

		coordFunction = cf;

		if (bound != null)
			coordFunction.setBound(bound);
	}

	public CoordFunction getCoordFunction() {
		return coordFunction;
	}

	public void setOperation(Operation op) {
		if (op == null)
			return;

		components.add(op);
	}

	public String toString() {
		StringBuffer name = new StringBuffer();

		for (AlgorithmComponent component : components)
			if (component instanceof CoordFunction)
				name.append(component instanceof AllCoords ? "" : coordFunction + " ");

		for (AlgorithmComponent component : components)
			if (component instanceof Operation)
				name.append(component + " ");

		return name.toString().trim();
	}

	public void setSmudge(Smudge s) {
		parent = s;

		setController(s.getController());
	}

	public void setController(Controller c) {
		if (c == null)
			return;

		super.setController(c);

		for (AlgorithmComponent component : components)
			component.setController(c);
	}

	public Smudge getSmudge() {
		return parent;
	}
}

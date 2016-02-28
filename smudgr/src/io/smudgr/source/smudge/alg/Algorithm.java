package io.smudgr.source.smudge.alg;

import java.util.ArrayList;

import io.smudgr.controller.Controller;
import io.smudgr.source.Frame;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.source.smudge.alg.bound.Bound;
import io.smudgr.source.smudge.alg.coord.AllCoords;
import io.smudgr.source.smudge.alg.coord.CoordFunction;
import io.smudgr.source.smudge.alg.op.Operation;
import io.smudgr.source.smudge.alg.select.Selector;
import io.smudgr.source.smudge.param.BooleanParameter;
import io.smudgr.source.smudge.param.Parametric;

public class Algorithm extends Parametric {

	private BooleanParameter enable = new BooleanParameter("Enable", this, true);

	private Smudge parent;
	private Bound bound;
	private CoordFunction coordFunction;

	private ArrayList<AlgorithmComponent> components = new ArrayList<AlgorithmComponent>();
	private ArrayList<ColorIndexList> selectedPixels = new ArrayList<ColorIndexList>();

	protected Frame lastFrame;

	public Algorithm() {
		add(new Bound());
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
		boolean dimensionsChanged = lastFrame == null || (img.getWidth() != this.lastFrame.getWidth() || img.getHeight() != this.lastFrame.getHeight());

		coordFunction.setDimensions(img);
		coordFunction.setBound(bound);
		if (dimensionsChanged || boundChanged)
			coordFunction.update();

		if (lastFrame != img) {
			setSelectedPixels(coordFunction.getCoordSet());

			for (AlgorithmComponent component : components)
				if (component instanceof Selector) {
					Selector selector = ((Selector) component);
					selector.setFrame(img);
					selector.update();
				}
		}

		for (AlgorithmComponent component : components)
			if (component instanceof Operation)
				((Operation) component).execute(img);

		lastFrame = img;
	}

	public void add(AlgorithmComponent component) {
		if (component == null)
			return;

		component.setAlgorithm(this);

		if (component instanceof Bound)
			setBound((Bound) component);

		if (component instanceof CoordFunction)
			setCoordFunction((CoordFunction) component);

		components.add(component);
	}

	private void setBound(Bound bound) {
		if (bound == null)
			return;

		ArrayList<AlgorithmComponent> otherBounds = new ArrayList<AlgorithmComponent>();

		for (AlgorithmComponent component : components)
			if (component instanceof CoordFunction)
				otherBounds.add(component);

		components.remove(otherBounds);

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

		coordFunction = cf;

		if (bound != null)
			coordFunction.setBound(bound);
	}

	public void setSelectedPixels(ArrayList<ColorIndexList> selected) {
		selectedPixels = selected;
	}

	public ArrayList<ColorIndexList> getSelectedPixels() {
		return selectedPixels;
	}

	public String getName() {
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

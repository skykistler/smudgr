package io.smudgr.smudge.alg;

import java.util.ArrayList;

import io.smudgr.smudge.Smudge;
import io.smudgr.smudge.alg.bound.Bound;
import io.smudgr.smudge.alg.coord.AllCoords;
import io.smudgr.smudge.alg.coord.CoordFunction;
import io.smudgr.smudge.alg.op.Operation;
import io.smudgr.smudge.alg.select.Selector;
import io.smudgr.smudge.param.BooleanParameter;
import io.smudgr.smudge.param.Parametric;
import io.smudgr.smudge.source.Frame;

public class Algorithm extends Parametric {

	private BooleanParameter enable = new BooleanParameter("Enable", this, true);

	private Smudge parent;
	private Bound bound;
	private CoordFunction coordFunction;

	private ArrayList<AlgorithmComponent> components = new ArrayList<AlgorithmComponent>();

	private ArrayList<PixelIndexList> selectedPixels = new ArrayList<PixelIndexList>();

	protected Frame lastFrame;

	public void init() {
		if (bound == null)
			add(new Bound());
		if (coordFunction == null)
			add(new AllCoords());

		for (AlgorithmComponent c : getComponents())
			c.init();
	}

	private double lastBoundX;
	private double lastBoundY;
	private double lastBoundW;
	private double lastBoundH;

	public void apply(Frame img) {
		if (!enable.getValue())
			return;

		bound.update();

		boolean boundChanged = lastBoundX != bound.getOffsetX() || lastBoundY != bound.getOffsetY() || lastBoundW != bound.getWidth() || lastBoundH != bound.getHeight();
		boolean dimensionsChanged = lastFrame == null || (img.getWidth() != this.lastFrame.getWidth() || img.getHeight() != this.lastFrame.getHeight());

		coordFunction.setDimensions(img);
		coordFunction.setBound(bound);

		if (dimensionsChanged || boundChanged)
			coordFunction.triggerChange();

		coordFunction.update();

		if (lastFrame != img) {
			setSelectedPixels(coordFunction.getCoordSet());

			for (AlgorithmComponent component : getComponents())
				if (component instanceof Selector) {
					Selector selector = ((Selector) component);
					selector.setFrame(img);
					selector.update();
				}
		}

		for (AlgorithmComponent component : getComponents())
			if (component instanceof Operation)
				((Operation) component).execute(img);

		lastFrame = img;
		lastBoundX = bound.getOffsetX();
		lastBoundY = bound.getOffsetY();
		lastBoundW = bound.getWidth();
		lastBoundH = bound.getHeight();
	}

	public void add(AlgorithmComponent component) {
		if (components.contains(component))
			return;

		getIdManager().add(component);

		component.setAlgorithm(this);
		components.add(component);

		if (component instanceof Bound)
			setBound((Bound) component);

		if (component instanceof CoordFunction)
			setCoordFunction((CoordFunction) component);
	}

	public AlgorithmComponent getComponent(int id) {
		return components.get(id);
	}

	public ArrayList<AlgorithmComponent> getComponents() {
		return components;
	}

	private void setBound(Bound bound) {
		if (bound == null)
			return;

		ArrayList<AlgorithmComponent> otherBounds = new ArrayList<AlgorithmComponent>();

		for (AlgorithmComponent component : getComponents())
			if (component instanceof CoordFunction)
				otherBounds.add(component);

		components.remove(otherBounds);

		this.bound = bound;

		if (coordFunction != null)
			coordFunction.setBound(bound);
	}

	public Bound getBound() {
		return bound;
	}

	private void setCoordFunction(CoordFunction cf) {
		if (cf == null)
			return;

		ArrayList<AlgorithmComponent> otherCoordFunctions = new ArrayList<AlgorithmComponent>();

		for (AlgorithmComponent component : getComponents())
			if (component instanceof CoordFunction)
				otherCoordFunctions.add(component);

		components.remove(otherCoordFunctions);

		coordFunction = cf;

		if (bound != null)
			coordFunction.setBound(bound);
	}

	public void setSelectedPixels(ArrayList<PixelIndexList> selected) {
		selectedPixels = selected;
	}

	public ArrayList<PixelIndexList> getSelectedPixels() {
		return selectedPixels;
	}

	public String getName() {
		StringBuffer name = new StringBuffer();

		for (AlgorithmComponent component : getComponents())
			if (component instanceof CoordFunction)
				name.append(component instanceof AllCoords ? "" : coordFunction + " ");

		for (AlgorithmComponent component : getComponents())
			if (component instanceof Operation)
				name.append(component + " ");

		return name.toString().trim();
	}

}

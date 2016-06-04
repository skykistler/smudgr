package io.smudgr.project.smudge.alg;

import java.util.ArrayList;

import io.smudgr.project.PropertyMap;
import io.smudgr.project.smudge.alg.bound.Bound;
import io.smudgr.project.smudge.alg.coord.CoordFunction;
import io.smudgr.project.smudge.alg.coord.RowCoords;
import io.smudgr.project.smudge.alg.op.Operation;
import io.smudgr.project.smudge.alg.select.Selector;
import io.smudgr.project.smudge.param.BooleanParameter;
import io.smudgr.project.smudge.param.Parametric;
import io.smudgr.project.util.Frame;

public class Algorithm extends Parametric {

	private BooleanParameter enabled = new BooleanParameter("Enable", this, true);

	private Bound bound;
	private CoordFunction coordFunction;

	private ArrayList<AlgorithmComponent> components = new ArrayList<AlgorithmComponent>();

	private ArrayList<PixelIndexList> selectedPixels = new ArrayList<PixelIndexList>();

	protected Frame lastFrame;

	public void init() {
		if (bound == null)
			add(new Bound());

		if (coordFunction == null) {
			RowCoords defaultCoords = new RowCoords();
			defaultCoords.getParameter("Continuous").setValue(true);

			add(defaultCoords);
		}
	}

	private double lastBoundX;
	private double lastBoundY;
	private double lastBoundW;
	private double lastBoundH;

	public void apply(Frame img) {
		if (!enabled.getValue())
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

		getProject().add(component);

		component.setAlgorithm(this);
		components.add(component);

		if (component instanceof Bound)
			setBound((Bound) component);

		if (component instanceof CoordFunction)
			setCoordFunction((CoordFunction) component);

		component.init();
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

	public void save(PropertyMap pm) {
		super.save(pm);

		for (AlgorithmComponent component : getComponents()) {
			PropertyMap map = new PropertyMap("component");

			component.save(map);

			pm.add(map);
		}
	}

	public void load(PropertyMap pm) {
		super.load(pm);

		ArrayList<PropertyMap> children = pm.getChildren("component");

		for (PropertyMap map : children) {
			String type = map.getAttribute("type");
			String name = map.getAttribute("name");

			AlgorithmComponent comp = getProject().getComponentLibrary().getNewComponent(type, name);

			if (comp != null) {
				comp.load(map);
				add(comp);
			}
		}
	}

	public String getName() {
		StringBuffer name = new StringBuffer();

		for (AlgorithmComponent component : getComponents())
			if (component instanceof CoordFunction)
				name.append(coordFunction + " ");

		for (AlgorithmComponent component : getComponents())
			if (component instanceof Operation)
				name.append(component + " ");

		return name.toString().trim();
	}

}

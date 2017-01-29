package io.smudgr.engine.alg;

import java.util.ArrayList;

import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.engine.alg.bound.Bound;
import io.smudgr.engine.alg.coord.CoordFunction;
import io.smudgr.engine.alg.coord.StraightCoords;
import io.smudgr.engine.alg.op.Operation;
import io.smudgr.engine.alg.select.Selector;
import io.smudgr.engine.element.Element;
import io.smudgr.engine.param.BooleanParameter;
import io.smudgr.engine.param.Parametric;
import io.smudgr.util.Frame;

/**
 * The {@link Algorithm} class is a container for {@link AlgorithmComponent}
 * instances. Each {@link AlgorithmComponent} serves a role in determining the
 * entire {@link Algorithm} behavior.
 */
public class Algorithm extends Parametric implements Element {

	@Override
	public String getType() {
		return "Algorithm";
	}

	private BooleanParameter enabled = new BooleanParameter("Enable", this, true);

	private Bound bound;
	private CoordFunction coordFunction;

	private ArrayList<AlgorithmComponent> components = new ArrayList<AlgorithmComponent>();

	private ArrayList<PixelIndexList> selectedPixels = new ArrayList<PixelIndexList>();

	protected Frame lastFrame;

	/**
	 * Initialize the {@link Algorithm} with default components
	 */
	public void init() {
		if (bound == null)
			add(new Bound());

		if (coordFunction == null) {
			StraightCoords defaultCoords = new StraightCoords();
			defaultCoords.getParameter("Continuous").setValue(true);

			add(defaultCoords);
		}
	}

	private double lastBoundX;
	private double lastBoundY;
	private double lastBoundW;
	private double lastBoundH;

	/**
	 * Apply this {@link Algorithm} to the given {@link Frame}
	 *
	 * @param img
	 *            {@link Frame}
	 */
	public void apply(Frame img) {
		if (!enabled.getValue())
			return;

		bound.update();

		boolean boundChanged = lastBoundX != bound.getOffsetX() || lastBoundY != bound.getOffsetY() || lastBoundW != bound.getWidth() || lastBoundH != bound.getHeight();
		boolean dimensionsChanged = lastFrame == null || (img.getWidth() != this.lastFrame.getWidth() || img.getHeight() != this.lastFrame.getHeight());

		coordFunction.setFrameDimensions(img.getWidth(), img.getHeight());
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

	/**
	 * Add a given {@link AlgorithmComponent} to this {@link Algorithm}
	 *
	 * @param component
	 *            {@link AlgorithmComponent}
	 */
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

	/**
	 * Gets every {@link AlgorithmComponent} contained in this {@link Algorithm}
	 *
	 * @return List of {@link AlgorithmComponent}
	 */
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

	/**
	 * Gets the bound of this {@link Algorithm}
	 *
	 * @return {@link Bound}
	 */
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

	/**
	 * Sets the list of {@link PixelIndexList} lists that break an image down
	 * into iterable fragments.
	 *
	 * @param selected
	 *            {@code ArrayList<PixelIndexList>}
	 * @see Selector
	 */
	public void setSelectedPixels(ArrayList<PixelIndexList> selected) {
		selectedPixels = selected;
	}

	/**
	 * Gets the list of {@link PixelIndexList} lists that break an image down
	 * into iterable fragments.
	 *
	 * @return {@code ArrayList<PixelIndexList>}
	 * @see Selector
	 */
	public ArrayList<PixelIndexList> getSelectedPixels() {
		return selectedPixels;
	}

	@Override
	public void save(PropertyMap pm) {
		super.save(pm);

		pm.setAttribute("type", getType());

		for (AlgorithmComponent component : getComponents()) {
			PropertyMap map = new PropertyMap(AlgorithmComponent.PROJECT_MAP_TAG);

			component.save(map);

			pm.add(map);
		}
	}

	@Override
	public void load(PropertyMap pm) {
		super.load(pm);

		ArrayList<PropertyMap> children = pm.getChildren(AlgorithmComponent.PROJECT_MAP_TAG);

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

	@Override
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

package io.smudgr.extensions.image.alg;

import java.util.ArrayList;

import io.smudgr.engine.Smudge;
import io.smudgr.engine.SmudgeComponent;
import io.smudgr.extensions.image.alg.bound.Bound;
import io.smudgr.extensions.image.alg.coord.CoordFunction;
import io.smudgr.extensions.image.alg.coord.StraightCoords;
import io.smudgr.extensions.image.alg.op.Operation;
import io.smudgr.extensions.image.alg.select.Selector;
import io.smudgr.util.Frame;
import io.smudgr.util.PixelFrame;

/**
 * The {@link Algorithm} class is a container for {@link AlgorithmComponent}
 * instances. Each {@link AlgorithmComponent} serves a role in determining the
 * entire {@link Algorithm} behavior.
 */
public class Algorithm extends Smudge {

	@Override
	public String getTypeName() {
		for (SmudgeComponent component : getComponents())
			if (component instanceof Operation) {
				return component.toString();
			}

		return "Algorithm";
	}

	@Override
	public String getTypeIdentifier() {
		return "algorithm";
	}

	private Bound bound;
	private CoordFunction coordFunction;

	private ArrayList<PixelIndexList> selectedPixels = new ArrayList<PixelIndexList>();

	protected PixelFrame lastFrame;
	private double lastBoundX, lastBoundY, lastBoundW, lastBoundH;

	/**
	 * Initialize the {@link Algorithm} with default components
	 */
	@Override
	public void onInit() {
		if (bound == null)
			add(new Bound());

		if (coordFunction == null) {
			StraightCoords defaultCoords = new StraightCoords();
			defaultCoords.getParameter("Continuous").setValue(true);

			add(defaultCoords);
		}
	}

	@Override
	public Frame smudge(Frame data) {
		PixelFrame img = (PixelFrame) data;

		boolean boundChanged = lastBoundX != bound.getOffsetX() || lastBoundY != bound.getOffsetY() || lastBoundW != bound.getWidth() || lastBoundH != bound.getHeight();
		boolean dimensionsChanged = lastFrame == null || (img.getWidth() != this.lastFrame.getWidth() || img.getHeight() != this.lastFrame.getHeight());

		coordFunction.setFrameDimensions(img.getWidth(), img.getHeight());
		coordFunction.setBound(bound);

		if (dimensionsChanged || boundChanged)
			coordFunction.triggerChange();

		coordFunction.generateIfChanged();

		if (lastFrame != img) {
			setSelectedPixels(coordFunction.getCoordSet());

			for (SmudgeComponent component : getComponents())
				if (component instanceof Selector) {
					Selector selector = ((Selector) component);
					selector.setFrame(img);
					selector.generate();
				}
		}

		for (SmudgeComponent component : getComponents())
			if (component instanceof Operation)
				((Operation) component).execute(img);

		lastFrame = img;
		lastBoundX = bound.getOffsetX();
		lastBoundY = bound.getOffsetY();
		lastBoundW = bound.getWidth();
		lastBoundH = bound.getHeight();

		return lastFrame;
	}

	@Override
	protected void onAdd(SmudgeComponent component) {
		if (component instanceof Bound)
			setBound((Bound) component);

		if (component instanceof CoordFunction)
			setCoordFunction((CoordFunction) component);
	}

	private void setBound(Bound bound) {
		if (bound == null)
			return;

		ArrayList<SmudgeComponent> otherBounds = new ArrayList<SmudgeComponent>();

		for (SmudgeComponent component : getComponents())
			if (component instanceof CoordFunction && component != bound)
				otherBounds.add(component);

		getComponents().removeAll(otherBounds);

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

		ArrayList<SmudgeComponent> otherCoordFunctions = new ArrayList<SmudgeComponent>();

		for (SmudgeComponent component : getComponents())
			if (component instanceof CoordFunction && component != cf)
				otherCoordFunctions.add(component);

		getComponents().removeAll(otherCoordFunctions);

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

}

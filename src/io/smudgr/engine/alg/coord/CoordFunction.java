package io.smudgr.engine.alg.coord;

import java.util.ArrayList;
import java.util.Stack;

import io.smudgr.engine.alg.Algorithm;
import io.smudgr.engine.alg.AlgorithmComponent;
import io.smudgr.engine.alg.PixelIndexList;
import io.smudgr.engine.alg.bound.Bound;
import io.smudgr.engine.alg.op.Operation;
import io.smudgr.engine.param.BooleanParameter;
import io.smudgr.util.Frame;

/**
 * The abstract {@link CoordFunction} is an {@link AlgorithmComponent} that
 * specifies in what order an {@link Operation} should act on the {@link Frame}.
 * <p>
 * Depending on the {@link Operation}, ordering may make no visual difference.
 * In many operations though, order will dramatically change the form of the
 * image.
 * <p>
 * Generating the coordinate set ({@link CoordFunction#update()}) is expensive
 * and happens conservatively, usually only on bound or image dimension changes.
 */
public abstract class CoordFunction extends AlgorithmComponent {

	@Override
	public String getType() {
		return "Flow";
	}

	private BooleanParameter continuous = new BooleanParameter("Continuous", this, false);

	private Algorithm parent;
	private boolean wasChanged = true;

	protected Bound bound;
	protected ArrayList<PixelIndexList> coordSet = null;
	protected Stack<PixelIndexList> disposedLists = new Stack<PixelIndexList>();
	protected PixelIndexList currentSet = null;

	protected int imageWidth, imageHeight, boundX, boundY, boundWidth, boundHeight;

	@Override
	public void init() {

	}

	@Override
	public void update() {
		if (bound == null || imageWidth <= 0 || imageHeight <= 0)
			return;

		if (!wasChanged)
			return;

		reset();

		generate(imageWidth, imageHeight, boundX, boundY, boundWidth, boundHeight);
		nextSet();
	}

	private void reset() {
		if (coordSet != null) {
			for (PixelIndexList list : coordSet)
				disposedLists.push(list);

			coordSet.clear();
		}

		if (currentSet != null)
			currentSet.clear();

		nextSet();

		wasChanged = false;
	}

	/**
	 * Implementations of this method should make any number of coordinate sets
	 * by calling {@link CoordFunction#nextSet()}, and calling
	 * {@link CoordFunction#nextPoint(int, int)} to add coordinates to the
	 * current set.
	 * <p>
	 * Sets are used to break the image into iterable fragments, while each
	 * point defines a pixel to act on.
	 *
	 * @param imageWidth
	 * @param imageHeight
	 * @param boundX
	 * @param boundY
	 * @param boundWidth
	 * @param boundHeight
	 */
	protected abstract void generate(int imageWidth, int imageHeight, int boundX, int boundY, int boundWidth, int boundHeight);

	protected void nextSet() {
		boolean breakSet = !continuous.getValue();

		// If our total set of coords doesn't exist yet, make it
		if (coordSet == null)
			coordSet = new ArrayList<PixelIndexList>();

		// If we should put breaks in the set, or we haven't added our first set
		if (breakSet || coordSet.size() == 0)
			// And our current set isn't empty, then add it
			if (currentSet != null && currentSet.size() > 0)
			coordSet.add(currentSet);

		// Finally, reset the current set if needed
		if (currentSet == null || breakSet) {
			if (!disposedLists.empty()) {
				currentSet = disposedLists.pop();
				currentSet.resetQuick();
			} else
				currentSet = new PixelIndexList();
		}

		wasInBound = false;
	}

	private boolean wasInBound = false;

	protected void nextPoint(int x, int y) {
		// If we haven't started generating, make the first set
		if (currentSet == null)
			nextSet();

		// If point is in bound, add it's index
		if (boundContainsPoint(x, y)) {
			int index = x + y * imageWidth;
			currentSet.add(index);
			wasInBound = true;
		}
		// Else, if we were just in bound, break the set and set our flag
		else if (wasInBound) {
			nextSet();
			wasInBound = false;
		}
	}

	/**
	 * Helper method to determine if the current bound contains a given point
	 *
	 * @param x
	 * @param y
	 * @return {@code true} if bound contains point, {@code false} if otherwise
	 */
	protected boolean boundContainsPoint(int x, int y) {
		return bound.containsPoint(x, y, imageWidth, imageHeight);
	}

	@Override
	public void triggerChange() {
		wasChanged = true;
	}

	/**
	 * Get the coordinate set generated by this function
	 *
	 * @return {@code ArrayList<PixelIndexList>}
	 * @see PixelIndexList
	 */
	public ArrayList<PixelIndexList> getCoordSet() {
		return coordSet;
	}

	/**
	 * Sets the current bound to comply with
	 *
	 * @param bound
	 *            {@link Bound}
	 */
	public void setBound(Bound bound) {
		this.bound = bound;
		updateBoundDimensions();
	}

	/**
	 * Set the current frame dimensions to work within
	 *
	 * @param frameWidth
	 *            Width of current frame/image
	 * @param frameHeight
	 *            Height of current frame/image
	 */
	public void setFrameDimensions(int frameWidth, int frameHeight) {
		imageWidth = frameWidth;
		imageHeight = frameHeight;
		updateBoundDimensions();
	}

	/**
	 * Update the real bound dimensions to work within
	 */
	protected void updateBoundDimensions() {
		if (bound == null || imageWidth <= 0 || imageHeight <= 0)
			return;

		boundX = bound.getTranslatedX(imageWidth);
		boundY = bound.getTranslatedY(imageHeight);
		boundWidth = bound.getTranslatedWidth(imageWidth);
		boundHeight = bound.getTranslatedHeight(imageHeight);
	}

	@Override
	public Algorithm getAlgorithm() {
		return parent;
	}

}

package io.smudgr.smudge.alg.coord;

import java.util.ArrayList;

import io.smudgr.smudge.alg.Algorithm;
import io.smudgr.smudge.alg.AlgorithmComponent;
import io.smudgr.smudge.alg.PixelIndexList;
import io.smudgr.smudge.alg.bound.Bound;
import io.smudgr.smudge.source.Frame;

public abstract class CoordFunction extends AlgorithmComponent {
	private Algorithm parent;
	private Frame frame;
	private Bound bound;

	private boolean wasChanged = true;

	protected ArrayList<PixelIndexList> coordSet = null;
	protected PixelIndexList currentSet = null;

	public void init() {

	}

	public void update() {
		if (frame == null)
			return;

		if (!wasChanged)
			return;

		reset();

		generate(bound, frame);
		nextSet();
	}

	private void reset() {
		if (coordSet != null)
			coordSet.clear();
		if (currentSet != null)
			currentSet.clear();

		nextSet();

		wasChanged = false;
	}

	protected abstract void generate(Bound b, Frame img);

	protected void nextSet() {
		// If our total set of coords doesn't exist yet, make it
		if (coordSet == null)
			coordSet = new ArrayList<PixelIndexList>();

		// If a current set was being generated, add it if not empty
		if (currentSet != null && currentSet.size() > 0)
			coordSet.add(currentSet);

		// Finally, reset the current set and our in-bound flag
		currentSet = new PixelIndexList();
		wasInBound = false;
	}

	private boolean wasInBound = false;

	protected void nextPoint(int x, int y) {
		// If we haven't started generating, make the first set
		if (currentSet == null)
			nextSet();

		// If point is in bound, add it's index
		if (bound.containsPoint(frame, x, y)) {
			int index = x + y * frame.getWidth();
			currentSet.add(index);
			wasInBound = true;
		}
		// Else, if we were just in bound, break the set and set our flag
		else if (wasInBound) {
			nextSet();
			wasInBound = false;
		}
	}

	public void triggerChange() {
		wasChanged = true;
	}

	public ArrayList<PixelIndexList> getCoordSet() {
		return coordSet;
	}

	public void setBound(Bound bound) {
		this.bound = bound;
	}

	public void setDimensions(Frame image) {
		this.frame = image;
	}

	public abstract String getName();

	public String toString() {
		return getName();
	}

	public Algorithm getAlgorithm() {
		return parent;
	}

}

package io.smudgr.source.smudge.alg.coord;

import java.util.ArrayList;

import io.smudgr.source.Frame;
import io.smudgr.source.smudge.alg.Algorithm;
import io.smudgr.source.smudge.alg.AlgorithmComponent;
import io.smudgr.source.smudge.alg.bound.Bound;

public abstract class CoordFunction extends AlgorithmComponent {
	private Algorithm parent;
	private Frame image;
	private Bound bound;

	protected ArrayList<ArrayList<Integer>> coordSet = null;
	protected ArrayList<Integer> currentSet = null;

	public void init() {

	}

	public void update() {
		if (image == null)
			return;

		reset();

		generate();
		nextSet();
	}

	private void reset() {
		if (coordSet != null)
			coordSet.clear();
		if (currentSet != null)
			currentSet.clear();

		nextSet();
	}

	protected abstract void generate();

	protected void nextSet() {
		// If our total set of coords doesn't exist yet, make it
		if (coordSet == null)
			coordSet = new ArrayList<ArrayList<Integer>>();

		// If a current set was being generated, add it if not empty
		if (currentSet != null && currentSet.size() > 0)
			coordSet.add(currentSet);

		// Finally, reset the current set and our in-bound flag
		currentSet = new ArrayList<Integer>();
		wasInBound = false;
	}

	private boolean wasInBound = false;

	protected void nextPoint(int x, int y) {
		// If we haven't started generating, make the first set
		if (currentSet == null)
			nextSet();

		// If point is in bound, add it's index
		if (bound.containsPoint(image, x, y)) {
			int index = x + y * image.getWidth();
			currentSet.add(index);
			wasInBound = true;
		}
		// Else, if we were just in bound, break the set and set our flag
		else if (wasInBound) {
			nextSet();
			wasInBound = false;
		}
	}

	public ArrayList<ArrayList<Integer>> getCoordSet() {
		return coordSet;
	}

	public Bound getBound() {
		return bound;
	}

	public void setBound(Bound bound) {
		this.bound = bound;
	}

	public Frame getImage() {
		return image;
	}

	public void setImage(Frame image) {
		this.image = image;
	}

	public abstract String getName();

	public String toString() {
		return getName();
	}

	public Algorithm getAlgorithm() {
		return parent;
	}

}

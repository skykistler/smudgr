package io.smudgr.project.smudge.alg.coord;

import java.util.ArrayList;
import java.util.Stack;

import io.smudgr.project.smudge.alg.Algorithm;
import io.smudgr.project.smudge.alg.AlgorithmComponent;
import io.smudgr.project.smudge.alg.PixelIndexList;
import io.smudgr.project.smudge.alg.bound.Bound;
import io.smudgr.project.smudge.param.BooleanParameter;
import io.smudgr.util.Frame;

public abstract class CoordFunction extends AlgorithmComponent {

	public String getType() {
		return "Flow";
	}

	private BooleanParameter continuous = new BooleanParameter("Continuous", this, false);

	private Algorithm parent;
	private Frame frame;
	private Bound bound;

	private boolean wasChanged = true;

	protected ArrayList<PixelIndexList> coordSet = null;
	protected Stack<PixelIndexList> disposedLists = new Stack<PixelIndexList>();
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

	protected abstract void generate(Bound b, Frame img);

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

	public Algorithm getAlgorithm() {
		return parent;
	}

}

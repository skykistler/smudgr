package io.smudgr.source.smudge.alg.select;

import java.util.ArrayList;

import gnu.trove.list.array.TIntArrayList;
import io.smudgr.source.Frame;
import io.smudgr.source.smudge.alg.AlgorithmComponent;

public abstract class Selector extends AlgorithmComponent {

	private Frame frame;

	public void init() {

	}

	public void update() {
		if (frame == null)
			return;

		ArrayList<TIntArrayList> selected = getAlgorithm().getSelectedPixels();
		ArrayList<TIntArrayList> newSelected = new ArrayList<TIntArrayList>();

		TIntArrayList currentSet = new TIntArrayList();

		for (int i = 0; i < selected.size(); i++) {
			TIntArrayList coords = selected.get(i);

			for (int index = 0; index < coords.size(); index++) {
				int coord = coords.get(index);
				int x = coord % frame.getWidth();
				int y = (coord - x) / frame.getWidth();

				if (selectsPoint(frame, x, y))
					currentSet.add(coord);
				else if (currentSet.size() > 0) {
					newSelected.add(currentSet);
					currentSet = new TIntArrayList();
				}
			}

			if (currentSet.size() > 0) {
				newSelected.add(currentSet);
				currentSet = new TIntArrayList();
			}
		}

		getAlgorithm().setSelectedPixels(newSelected);
	}

	public abstract boolean selectsPoint(Frame img, int x, int y);

	public void setFrame(Frame f) {
		this.frame = f;
	}

}

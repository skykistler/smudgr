package io.smudgr.source.smudge.alg.select;

import java.util.ArrayList;

import io.smudgr.source.Frame;
import io.smudgr.source.smudge.alg.AlgorithmComponent;

public abstract class Selector extends AlgorithmComponent {

	private Frame frame;

	public void init() {

	}

	public void update() {
		if (frame == null)
			return;

		ArrayList<ArrayList<Integer>> selected = getAlgorithm().getSelectedPixels();
		ArrayList<ArrayList<Integer>> newSelected = new ArrayList<ArrayList<Integer>>();

		ArrayList<Integer> currentSet = new ArrayList<Integer>();

		for (int i = 0; i < selected.size(); i++) {
			ArrayList<Integer> coords = selected.get(i);

			for (Integer coord : coords) {
				int x = coord % frame.getWidth();
				int y = (coord - x) / frame.getWidth();

				if (selectsPoint(frame, x, y))
					currentSet.add(coord);
				else if (currentSet.size() > 0) {
					newSelected.add(currentSet);
					currentSet = new ArrayList<Integer>();
				}
			}

			if (currentSet.size() > 0) {
				newSelected.add(currentSet);
				currentSet = new ArrayList<Integer>();
			}
		}

		getAlgorithm().setSelectedPixels(newSelected);
	}

	public abstract boolean selectsPoint(Frame img, int x, int y);

	public void setFrame(Frame f) {
		this.frame = f;
	}

}

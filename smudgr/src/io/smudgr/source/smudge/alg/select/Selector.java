package io.smudgr.source.smudge.alg.select;

import java.util.ArrayList;

import io.smudgr.source.Frame;
import io.smudgr.source.smudge.alg.AlgorithmComponent;
import io.smudgr.source.smudge.alg.ColorIndexList;

public abstract class Selector extends AlgorithmComponent {

	private Frame frame;

	public void init() {

	}

	public void update() {
		if (frame == null)
			return;

		ArrayList<ColorIndexList> selected = getAlgorithm().getSelectedPixels();
		ArrayList<ColorIndexList> newSelected = new ArrayList<ColorIndexList>();

		ColorIndexList currentSet = new ColorIndexList();

		for (int i = 0; i < selected.size(); i++) {
			ColorIndexList coords = selected.get(i);

			for (int index = 0; index < coords.size(); index++) {
				int coord = coords.get(index);
				int x = coord % frame.getWidth();
				int y = (coord - x) / frame.getWidth();

				if (selectsPoint(frame, x, y))
					currentSet.add(coord);
				else if (currentSet.size() > 0) {
					newSelected.add(currentSet);
					currentSet = new ColorIndexList();
				}
			}

			if (currentSet.size() > 0) {
				newSelected.add(currentSet);
				currentSet = new ColorIndexList();
			}
		}

		getAlgorithm().setSelectedPixels(newSelected);
	}

	public abstract boolean selectsPoint(Frame img, int x, int y);

	public void setFrame(Frame f) {
		this.frame = f;
	}

}

package io.smudgr.project.smudge.alg.select;

import java.util.ArrayList;
import java.util.Stack;

import io.smudgr.project.smudge.alg.AlgorithmComponent;
import io.smudgr.project.smudge.alg.PixelIndexList;
import io.smudgr.util.Frame;

public abstract class Selector extends AlgorithmComponent {

	public String getType() {
		return "Selector";
	}

	protected ArrayList<PixelIndexList> selectedList = new ArrayList<PixelIndexList>();
	protected Stack<PixelIndexList> disposedLists = new Stack<PixelIndexList>();

	private Frame frame;

	public void init() {

	}

	public void update() {
		if (frame == null)
			return;

		ArrayList<PixelIndexList> selected = getAlgorithm().getSelectedPixels();

		for (PixelIndexList list : selectedList)
			disposedLists.push(list);

		selectedList.clear();

		PixelIndexList currentSet = getNewSet();

		for (int i = 0; i < selected.size(); i++) {
			PixelIndexList coords = selected.get(i);

			for (int index = 0; index < coords.size(); index++) {
				int coord = coords.get(index);
				int x = coord % frame.getWidth();
				int y = (coord - x) / frame.getWidth();

				if (selectsPoint(frame, x, y))
					currentSet.add(coord);
				else if (currentSet.size() > 0) {
					selectedList.add(currentSet);
					currentSet = getNewSet();
				}
			}

			if (currentSet.size() > 0) {
				selectedList.add(currentSet);
				currentSet = getNewSet();
			}
		}

		getAlgorithm().setSelectedPixels(selectedList);
	}

	public PixelIndexList getNewSet() {
		if (disposedLists.empty())
			return new PixelIndexList();

		PixelIndexList list = disposedLists.pop();
		list.resetQuick();

		return list;
	}

	public abstract boolean selectsPoint(Frame img, int x, int y);

	public void setFrame(Frame f) {
		this.frame = f;
	}

}

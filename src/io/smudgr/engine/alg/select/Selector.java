package io.smudgr.engine.alg.select;

import java.util.ArrayList;
import java.util.Stack;

import io.smudgr.engine.alg.Algorithm;
import io.smudgr.engine.alg.AlgorithmComponent;
import io.smudgr.engine.alg.PixelIndexList;
import io.smudgr.engine.alg.coord.CoordFunction;
import io.smudgr.engine.alg.op.Operation;
import io.smudgr.util.Frame;

/**
 * The abstract {@link Selector} class defines an {@link AlgorithmComponent}
 * that uses actual pixel values of an image to focus an {@link Operation}. A
 * {@link CoordFunction} generates a pixel order but {@link Selector}
 * implementations narrow that set down based on pixel value.
 */
public abstract class Selector extends AlgorithmComponent {

	@Override
	public String getComponentTypeName() {
		return "Selector";
	}

	@Override
	public String getComponentTypeIdentifier() {
		return "selector";
	}

	protected ArrayList<PixelIndexList> selectedList = new ArrayList<PixelIndexList>();
	protected Stack<PixelIndexList> disposedLists = new Stack<PixelIndexList>();

	private Frame frame;
	private int i, index, coord, x, y;

	/**
	 * Generates the selected pixel list.
	 */
	public void generate() {
		if (frame == null)
			return;

		ArrayList<PixelIndexList> selected = getSelectedPixels();

		for (PixelIndexList list : selectedList)
			disposedLists.push(list);

		selectedList.clear();

		PixelIndexList currentSet = getNewSet();

		for (i = 0; i < selected.size(); i++) {
			PixelIndexList coords = selected.get(i);

			for (index = 0; index < coords.size(); index++) {
				coord = coords.get(index);
				x = coord % frame.getWidth();
				y = (coord - x) / frame.getWidth();

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

		setSelectedPixels(selectedList);
	}

	protected void setSelectedPixels(ArrayList<PixelIndexList> selectedPixels) {
		((Algorithm) getParent()).setSelectedPixels(selectedPixels);
	}

	protected PixelIndexList getNewSet() {
		if (disposedLists.empty())
			return new PixelIndexList();

		PixelIndexList list = disposedLists.pop();
		list.resetQuick();

		return list;
	}

	/**
	 * Gets whether the given point is selected for operation given an image
	 * {@link Frame}
	 *
	 * @param img
	 *            {@link Frame}
	 * @param x
	 *            coordinate of pixel
	 * @param y
	 *            coordinate of pixel
	 * @return {@code true} if the pixel should be acted upon, {@code false} if
	 *         otherwise
	 */
	public abstract boolean selectsPoint(Frame img, int x, int y);

	/**
	 * Sets the frame to use when selecting pixels.
	 *
	 * @param f
	 *            {@link Frame}
	 */
	public void setFrame(Frame f) {
		this.frame = f;
	}

}

package io.smudgr.project.smudge.alg.op;

import io.smudgr.project.smudge.alg.PixelIndexList;
import io.smudgr.project.smudge.param.BooleanParameter;
import io.smudgr.project.smudge.param.NumberParameter;
import io.smudgr.util.Frame;

public class PixelShift extends Operation {

	public String getName() {
		return "Pixel Shift";
	}

	private NumberParameter amount = new NumberParameter("Amount", this, 0, 0, 1, 0.005);
	private NumberParameter intervals = new NumberParameter("Intervals", this, 5, 1, 1000, 1);

	private NumberParameter start = new NumberParameter("Start", this, 0, 0, 1, 0.01);
	private NumberParameter length = new NumberParameter("Length", this, 1, 0, 1, 0.01);

	/*
	 * allows shift to be set via switch structure in calculateShift function
	 */
	private NumberParameter shiftType = new NumberParameter("Shift Type", this, 1, 1, 4, 1);
	private BooleanParameter reverse = new BooleanParameter("Reverse", this, true);

	private int selectedShiftType;
	private boolean isReversed;
	private int intervalStart;
	private int intervalEnd;

	public void init() {
		amount.setContinuous(true);
	}

	public void execute(Frame img) {
		int size = getAlgorithm().getSelectedPixels().size();

		selectedShiftType = shiftType.getIntValue();
		isReversed = reverse.getValue();

		Frame shifted = img.copy();

		double shift = amount.getValue();

		double ints = intervals.getValue();

		// set start and end of intervals affected
		int n0 = (int) (ints * start.getValue());
		int nLength = (int) (ints * length.getValue());
		int n1 = n0 + nLength;

		if (n1 > ints)
			n1 = (int) ints;

		double intervalWidth = size / ints;

		if (intervalWidth < 1) {
			intervalWidth = 1;
			ints = size;
		}

		intervalStart = n0;
		intervalEnd = n1;

		for (int n = n0 + 1; n < n1 + 1; n++) {

			double interval = (n - 1) * intervalWidth;

			for (int i = 0; i < intervalWidth; i++) {
				int index = (int) (interval + i);
				if (index < size) {
					PixelIndexList coords = getAlgorithm().getSelectedPixels().get(index);
					shift(shifted, img, coords, n, shift, i, ints);
				}
			}
		}

		shifted.copyTo(img);
		shifted.dispose();
	}

	public void shift(Frame shifted, Frame orig, PixelIndexList coords, int currentInterval, double amount,
			int indexIntoCurrentInt, double totalInts) {

		double shiftScale = 1;

		// find type of shift

		switch (selectedShiftType) {

		case 1:
			shiftScale = currentInterval;
			break;
		case 2:
			shiftScale = (currentInterval % 2 == 0 ? -currentInterval : currentInterval);
			break;
		case 3:
			int w = intervalEnd - intervalStart;
			shiftScale = Math.abs((intervalStart + (w / 2)) - currentInterval + 1);
			break;
		case 4:
			w = intervalEnd - intervalStart;
			shiftScale = Math.abs((intervalStart + (w / 2)) - currentInterval + 1);
			shiftScale = (currentInterval % 2 == 0 ? -shiftScale : shiftScale);
			break;
		}

		// reverse direction of shifting

		if (isReversed)
			shiftScale = -shiftScale;

		// shift pixels now and wrap around using modulo

		for (int i = 0; i < coords.size(); i++) {
			int shift = (int) (i + (shiftScale * amount) * coords.size());

			shift = Math.floorMod(shift, coords.size());

			shifted.pixels[coords.get(i)] = orig.pixels[coords.get(shift)];
		}

	}

}
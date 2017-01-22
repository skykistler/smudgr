package io.smudgr.engine.alg.op;

import io.smudgr.engine.alg.PixelIndexList;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.util.Frame;

/**
 * I honestly haven't played with this so who knows
 */
public class Smear extends Operation {

	@Override
	public String getName() {
		return "Smear";
	}

	private NumberParameter intervals = new NumberParameter("Intervals", this, 5, 1, 1000, 1);
	private NumberParameter start = new NumberParameter("Start", this, 0, 0, 1, 0.01);
	private NumberParameter length = new NumberParameter("Length", this, 1, 0, 1, 0.01);

	@Override
	public void execute(Frame img) {
		Frame stretched = img.copy();

		for (PixelIndexList coords : getAlgorithm().getSelectedPixels()) {
			stretch(coords, stretched, img);
		}

		stretched.copyTo(img);
	}

	private void stretch(PixelIndexList coords, Frame stretched, Frame orig) {

		int size = coords.size();
		int ints = intervals.getIntValue();

		// set start and end of intervals affected
		int n0 = (int) (ints * start.getValue());
		int nLength = (int) (ints * length.getValue());

		double intervalWidth = size / ints;

		if (intervalWidth <= 1) {
			intervalWidth = 1;
			ints = size;
		}

		int n1;

		if (n0 + nLength >= ints)
			n1 = ints;
		else
			n1 = n0 + nLength;

		for (int n = n0 + 1; n < n1 + 1; n++) {

			int interval = (int) ((n - 1) * intervalWidth);

			int color = orig.pixels[coords.get(interval)];

			for (int i = 0; i < intervalWidth; i++) {
				int index = interval + i;
				stretched.pixels[coords.get(index)] = color;
			}
		}
	}

}

package io.smudgr.engine.alg.op;

import io.smudgr.engine.alg.PixelIndexList;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.util.Frame;

/**
 * I honestly haven't played with this so who knows
 */
public class Smear extends ParallelOperation {

	@Override
	public String getName() {
		return "Smear";
	}

	private NumberParameter intervals = new NumberParameter("Intervals", this, 5, 1, 1000, 1);
	private NumberParameter start = new NumberParameter("Start", this, 0, 0, 1, 0.01);
	private NumberParameter length = new NumberParameter("Length", this, 1, 0, 1, 0.01);

	private Frame stretched;
	private int ints, n0, nLength;
	private double startPos, len;

	@Override
	public void preParallel(Frame img) {
		if (stretched == null || stretched.getWidth() != img.getWidth() || stretched.getHeight() != img.getHeight()) {
			if (stretched != null)
				stretched.dispose();

			stretched = img.copy();
		} else {
			img.copyTo(stretched);
		}

		ints = intervals.getIntValue();
		startPos = start.getValue();
		len = length.getValue();

		// set start and end of intervals affected
		n0 = (int) (ints * startPos);
		nLength = (int) (ints * len);
	}

	@Override
	public void postParallel(Frame img) {
		stretched.copyTo(img);
	}

	@Override
	public ParallelOperationTask getParallelTask() {
		return new SmearTask();
	}

	class SmearTask extends ParallelOperationTask {

		private int size, n1, n, interval, color, i, index;
		private double intervalWidth;

		@Override
		public void executeParallel(Frame img, PixelIndexList coords) {
			size = coords.size();
			stretch(coords, img);
		}

		private void stretch(PixelIndexList coords, Frame orig) {
			intervalWidth = size / ints;

			if (intervalWidth <= 1) {
				intervalWidth = 1;
				ints = size;
			}

			if (n0 + nLength >= ints)
				n1 = ints;
			else
				n1 = n0 + nLength;

			for (n = n0 + 1; n < n1 + 1; n++) {

				interval = (int) ((n - 1) * intervalWidth);

				color = orig.pixels[coords.get(interval)];

				for (i = 0; i < intervalWidth; i++) {
					index = interval + i;
					stretched.pixels[coords.get(index)] = color;
				}
			}

		}
	}

}

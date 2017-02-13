package io.smudgr.engine.alg.op;

import io.smudgr.engine.alg.PixelIndexList;
import io.smudgr.engine.alg.math.ColorHelper;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.util.Frame;

/**
 * Channel Drift shifts image RGB layers separately by parameterized amounts.
 */
public class ChannelDrift extends ParallelOperation {

	@Override
	public String getName() {
		return "Channel Drift";
	}

	NumberParameter redX = new NumberParameter("Red Offset - X", this, 0, 0, 1, 0.001);
	NumberParameter redY = new NumberParameter("Red Offset - Y", this, 0, 0, 1, 0.001);
	NumberParameter greenX = new NumberParameter("Green Offset - X", this, 0, 0, 1, 0.001);
	NumberParameter greenY = new NumberParameter("Green Offset - Y", this, 0, 0, 1, 0.001);
	NumberParameter blueX = new NumberParameter("Blue Offset - X", this, 0, 0, 1, 0.001);
	NumberParameter blueY = new NumberParameter("Blue Offset - Y", this, 0, 0, 1, 0.001);

	private Frame buffer;
	private int boundWidth, boundHeight;
	private int redShiftX, redShiftY, greenShiftX, greenShiftY, blueShiftX, blueShiftY;

	@Override
	public void onInit() {
		redX.setContinuous(true);
		redY.setContinuous(true);
		greenX.setContinuous(true);
		greenY.setContinuous(true);
		blueX.setContinuous(true);
		blueY.setContinuous(true);

		redX.setReverse(true);
		greenX.setReverse(true);
		blueX.setReverse(true);
	}

	@Override
	public void preParallel(Frame img) {
		boundWidth = getAlgorithm().getBound().getTranslatedWidth(img.getWidth());
		boundHeight = getAlgorithm().getBound().getTranslatedHeight(img.getHeight());

		if (buffer == null || buffer.getWidth() != img.getWidth() || buffer.getHeight() != img.getHeight()) {
			if (buffer != null)
				buffer.dispose();

			buffer = img.copy();
		} else {
			img.copyTo(buffer);
		}

		redShiftX = (int) (redX.getValue() * boundWidth);
		redShiftY = (int) (redY.getValue() * boundHeight);
		greenShiftX = (int) (greenX.getValue() * boundWidth);
		greenShiftY = (int) (greenY.getValue() * boundHeight);
		blueShiftX = (int) (blueX.getValue() * boundWidth);
		blueShiftY = (int) (blueY.getValue() * boundHeight);
	}

	@Override
	public void postParallel(Frame img) {
		buffer.copyTo(img);
	}

	@Override
	public ParallelOperationTask getParallelTask() {
		return new ChannelDriftTask();
	}

	class ChannelDriftTask extends ParallelOperationTask {

		private int index, coord, x, y, r, g, b, x1, y1;

		@Override
		public void executeParallel(Frame img, PixelIndexList coords) {
			for (index = 0; index < coords.size(); index++) {
				coord = coords.get(index);

				x = coord % buffer.getWidth();
				y = (coord - x) / buffer.getWidth();

				r = ColorHelper.red(getShifted(img, x, y, redShiftX, redShiftY));
				g = ColorHelper.green(getShifted(img, x, y, greenShiftX, greenShiftY));
				b = ColorHelper.blue(getShifted(img, x, y, blueShiftX, blueShiftY));

				buffer.pixels[coord] = ColorHelper.color(255, r, g, b);
			}
		}

		private int getShifted(Frame orig, int x, int y, int shiftX, int shiftY) {
			x1 = (x + shiftX) % boundWidth;
			y1 = (y + shiftY) % boundHeight;

			return orig.get(x1, y1);
		}
	}

}

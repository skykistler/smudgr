package io.smudgr.extensions.image.alg.op;

import io.smudgr.engine.param.NumberParameter;
import io.smudgr.extensions.image.alg.PixelIndexList;
import io.smudgr.extensions.image.alg.math.ColorHelper;
import io.smudgr.util.PixelFrame;

/**
 * Channel Crush uses a configurable bitwise function to squash RGB layer
 * information, resulting in a image with a singular color palette and bolder
 * forms, at the price of texture detail.
 */
public class ChannelCrush extends ParallelOperation {

	@Override
	public String getTypeName() {
		return "Channel Crush";
	}

	NumberParameter redMaskShiftParam = new NumberParameter("Red Mask", this, 1, 1, 7, 1);
	NumberParameter greenMaskShiftParam = new NumberParameter("Green Mask", this, 1, 1, 7, 1);
	NumberParameter blueMaskShiftParam = new NumberParameter("Blue Mask", this, 1, 1, 7, 1);

	NumberParameter redShiftParam = new NumberParameter("Red Shift", this, 0, 0, 7, 1);
	NumberParameter greenShiftParam = new NumberParameter("Green Shift", this, 0, 0, 7, 1);
	NumberParameter blueShiftParam = new NumberParameter("Blue Shift", this, 0, 0, 7, 1);

	private int redMaskShift, redShift, greenMaskShift, greenShift, blueMaskShift, blueShift;

	@Override
	public void onInit() {
		redShiftParam.setReverse(true);
		blueShiftParam.setReverse(true);
		greenShiftParam.setReverse(true);
	}

	@Override
	public void preParallel(PixelFrame img) {
		redMaskShift = redMaskShiftParam.getIntValue();
		greenMaskShift = greenMaskShiftParam.getIntValue();
		blueMaskShift = blueMaskShiftParam.getIntValue();

		redShift = redShiftParam.getIntValue();
		greenShift = greenShiftParam.getIntValue();
		blueShift = blueShiftParam.getIntValue();
	}

	@Override
	public ParallelOperationTask getParallelTask() {
		return new ChannelCrushTask();
	}

	class ChannelCrushTask extends ParallelOperationTask {

		private int index, color, red_op, red, green_op, green, blue_op, blue, c, newC;

		@Override
		public void executeParallel(PixelFrame img, PixelIndexList coords) {
			for (index = 0; index < coords.size(); index++)
				smear(coords.get(index), img);
		}

		private void smear(int coord, PixelFrame img) {
			color = img.pixels[coord];

			red_op = operate(0xFF0000, redMaskShift, redShift, color);
			red = ColorHelper.red(red_op);

			green_op = operate(0xFF00, greenMaskShift, greenShift, color);
			green = ColorHelper.green(green_op);

			blue_op = operate(0xFF, blueMaskShift, blueShift, color);
			blue = ColorHelper.blue(blue_op);

			img.pixels[coord] = ColorHelper.color(255, red, green, blue);
		}

		// returns individual color that we need for ColorHelper
		private int operate(int mask, int maskShift, int colorShift, int color) {
			c = color & mask;
			c |= c >> 1;
			c |= c >> 2;
			c |= c >> 4;
			c ^= c >> maskShift;
			c &= mask;

			newC = c & color;
			newC = newC >> colorShift;
			return newC & mask;
		}
	}

}

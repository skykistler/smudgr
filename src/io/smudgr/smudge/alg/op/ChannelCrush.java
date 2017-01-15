package io.smudgr.smudge.alg.op;

import io.smudgr.smudge.alg.PixelIndexList;
import io.smudgr.smudge.alg.math.ColorHelper;
import io.smudgr.smudge.param.NumberParameter;
import io.smudgr.util.Frame;

public class ChannelCrush extends Operation {

	public String getName() {
		return "Channel Crush";
	}

	NumberParameter redMaskShift = new NumberParameter("Red Mask", this, 1, 1, 7, 1);
	NumberParameter greenMaskShift = new NumberParameter("Green Mask", this, 1, 1, 7, 1);
	NumberParameter blueMaskShift = new NumberParameter("Blue Mask", this, 1, 1, 7, 1);

	NumberParameter redShift = new NumberParameter("Red Shift", this, 0, 0, 7, 1);
	NumberParameter greenShift = new NumberParameter("Green Shift", this, 0, 0, 7, 1);
	NumberParameter blueShift = new NumberParameter("Blue Shift", this, 0, 0, 7, 1);

	public ChannelCrush() {
		redShift.setReverse(true);
		blueShift.setReverse(true);
		greenShift.setReverse(true);
	}

	@Override
	public void execute(Frame img) {
		for (PixelIndexList coords : getAlgorithm().getSelectedPixels())
			for (int index = 0; index < coords.size(); index++)
				smear(coords.get(index), img);
	}

	public void smear(int coord, Frame img) {
		int color = img.pixels[coord];

		int red_op = operate(0xFF0000, redMaskShift.getIntValue(), redShift.getIntValue(), color);
		int red = ColorHelper.red(red_op);

		int green_op = operate(0xFF00, greenMaskShift.getIntValue(), greenShift.getIntValue(), color);
		int green = ColorHelper.green(green_op);

		int blue_op = operate(0xFF, blueMaskShift.getIntValue(), blueShift.getIntValue(), color);
		int blue = ColorHelper.blue(blue_op);

		img.pixels[coord] = ColorHelper.color(255, red, green, blue);
	}

	//returns individual color that we need for ColorHelper
	public int operate(int mask, int maskShift, int colorShift, int color) {
		int c = color & mask;
		c |= c >> 1;
		c |= c >> 2;
		c |= c >> 4;
		c ^= c >> maskShift;
		c &= mask;

		int newC = c & color;
		newC = newC >> colorShift;
		return newC & mask;
	}

}

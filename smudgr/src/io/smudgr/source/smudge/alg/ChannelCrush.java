package io.smudgr.source.smudge.alg;

import java.util.ArrayList;

import io.smudgr.source.Frame;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.source.smudge.alg.math.ColorHelper;
import io.smudgr.source.smudge.alg.param.NumberParameter;

public class ChannelCrush extends Algorithm {

	NumberParameter redMaskShift = new NumberParameter(this, "Red Mask", 1, 1, 7, 1);
	NumberParameter greenMaskShift = new NumberParameter(this, "Green Mask", 1, 1, 7, 1);
	NumberParameter blueMaskShift = new NumberParameter(this, "Blue Mask", 1, 1, 7, 1);

	NumberParameter redShift = new NumberParameter(this, "Red Shift", 0, 0, 7, 1);
	NumberParameter greenShift = new NumberParameter(this, "Green Shift", 0, 0, 7, 1);
	NumberParameter blueShift = new NumberParameter(this, "Blue Shift", 0, 0, 7, 1);

	public ChannelCrush(Smudge s) {
		super(s);
		redShift.setReverse(true);
		blueShift.setReverse(true);
		greenShift.setReverse(true);
	}

	@Override
	public void execute(Frame img) {
		for (ArrayList<Integer> coords : getCoordFunction().getCoordSet())
			for (Integer coord : coords)
				smear(coord, img);
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

	public String getName() {
		return "Channel Crush";
	}

}

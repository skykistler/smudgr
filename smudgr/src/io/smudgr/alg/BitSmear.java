package io.smudgr.alg;

import java.util.ArrayList;

import io.smudgr.Smudge;
import io.smudgr.alg.math.ColorHelper;
import io.smudgr.alg.param.BooleanParameter;
import io.smudgr.alg.param.NumberParameter;
import io.smudgr.model.Frame;

public class BitSmear extends Algorithm {
	
	NumberParameter redMaskShift = new NumberParameter(this, "Red Mask", 1, 1, 7, 1);
	NumberParameter greenMaskShift = new NumberParameter(this, "Green Mask", 1, 1, 7, 1);
	NumberParameter blueMaskShift = new NumberParameter(this, "Blue Mask", 1, 1, 7, 1);
	
	NumberParameter redShift = new NumberParameter(this, "Red Shift", 0, 0, 7, 1);
	NumberParameter greenShift = new NumberParameter(this, "Green Shift", 0, 0, 7, 1);
	NumberParameter blueShift = new NumberParameter(this, "Blue Shift", 0, 0, 7, 1);
	
	public BitSmear(Smudge s) {
		super(s);
		
	}

	@Override
	public void execute(Frame img) {
		for (ArrayList<Integer> coords : getCoordFunction().getCoordSet()) {
			for (Integer coord : coords) {
				smear(coord, img);
			}
		}
		
	}

	public void smear(int coord, Frame img) {
		int color = img.pixels[coord];
		int red = (int) ColorHelper.red(operate(0x00F00000, redMaskShift.getIntValue(), redShift.getIntValue(), color));
		int green = (int) ColorHelper.green(operate(0x0A00FF00, greenMaskShift.getIntValue(), greenShift.getIntValue(), color));
		int blue = (int) ColorHelper.blue(operate(0x000000FF, blueMaskShift.getIntValue(), blueShift.getIntValue(), color));
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
		return "Bit Smear";
	}
	
}

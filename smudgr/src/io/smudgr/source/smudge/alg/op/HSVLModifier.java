package io.smudgr.source.smudge.alg.op;

import java.util.ArrayList;

import io.smudgr.source.Frame;
import io.smudgr.source.smudge.alg.math.ColorHelper;
import io.smudgr.source.smudge.param.NumberParameter;

public class HSVLModifier extends Operation {

	NumberParameter saturation = new NumberParameter("Saturation", this, 0.5, -1.0, 1.0, 0.01);
	NumberParameter degree = new NumberParameter("Hue Rotation", this, 0, 0, 359, 1);
	NumberParameter value = new NumberParameter("Value/Lightness", this, 0, -1.0, 1.0, 0.01);
	NumberParameter type = new NumberParameter("Color Space", this, 1, 1, 2, 1);

	double lastSaturation, lastValue;
	int lastDegree;
	Frame lastFrame;

	public void execute(Frame img) {
		double sat = saturation.getValue();
		double val = value.getValue();
		int deg = degree.getIntValue();

		if (lastSaturation == sat && lastValue == val && lastDegree == deg) {
			img.setBufferedImage(lastFrame.getBufferedImage());
		} else {
			for (ArrayList<Integer> coords : getAlgorithm().getCoordFunction().getCoordSet()) {
				for (Integer coord : coords) {
					img.pixels[coord] = manipulate(img.pixels[coord], deg, sat, val);
				}
			}
		}

		lastSaturation = sat;
		lastDegree = deg;
		lastValue = val;
		lastFrame = img.copy();
	}

	private int manipulate(int color, int deg, double sat, double val) {
		int newRGB;
		switch (type.getIntValue()) {
		case 1:
			newRGB = ColorHelper.modifyHSV(color, deg, sat, val);
		case 2:
			newRGB = ColorHelper.modifyHSL(color, deg, sat, val);
		default:
			newRGB = ColorHelper.modifyHSL(color, deg, sat, val);
		}
		return newRGB;
	}

	public String getName() {
		return "HSV/HSL Modifier";
	}

}

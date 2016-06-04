package io.smudgr.project.smudge.alg.op;

import io.smudgr.project.smudge.alg.PixelIndexList;
import io.smudgr.project.smudge.alg.math.ColorHelper;
import io.smudgr.project.smudge.param.NumberParameter;
import io.smudgr.project.util.Frame;

public class HSVLModifier extends Operation {

	public String getName() {
		return "HSV/HSL Modifier";
	}

	NumberParameter saturation = new NumberParameter("Saturation", this, 0.5, -1.0, 1.0, 0.01);
	NumberParameter degree = new NumberParameter("Hue Rotation", this, 0, 0, 359, 1);
	NumberParameter value = new NumberParameter("Value/Lightness", this, 0, -1.0, 1.0, 0.01);
	NumberParameter type = new NumberParameter("Color Space", this, 1, 1, 2, 1);

	public void execute(Frame img) {
		double sat = saturation.getValue();
		double val = value.getValue();
		int deg = degree.getIntValue();

		for (PixelIndexList coords : getAlgorithm().getSelectedPixels()) {
			for (int index = 0; index < coords.size(); index++) {
				int coord = coords.get(index);
				img.pixels[coord] = manipulate(img.pixels[coord], deg, sat, val);
			}
		}
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

}

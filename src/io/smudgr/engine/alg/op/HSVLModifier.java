package io.smudgr.engine.alg.op;

import io.smudgr.engine.alg.PixelIndexList;
import io.smudgr.engine.alg.math.ColorHelper;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.util.Frame;

/**
 * HSV/L Modifier provides a traditional manipulation for saturation, hue, and
 * brightness.
 */
public class HSVLModifier extends Operation {

	@Override
	public String getName() {
		return "HSV/HSL Modifier";
	}

	NumberParameter saturation = new NumberParameter("Saturation", this, 0.5, -1.0, 1.0, 0.01);
	NumberParameter degree = new NumberParameter("Hue Rotation", this, 0, 0, 359, 1);
	NumberParameter value = new NumberParameter("Value/Lightness", this, 0, -1.0, 1.0, 0.01);
	NumberParameter type = new NumberParameter("Color Space", this, 1, 1, 2, 1);

	@Override
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

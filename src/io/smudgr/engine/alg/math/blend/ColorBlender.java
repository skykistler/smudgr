package io.smudgr.engine.alg.math.blend;

import io.smudgr.engine.alg.math.ColorHelper;
import io.smudgr.engine.alg.math.univariate.LumaFunction;

public class ColorBlender implements Blender {

	public String getName() {
		return "Hue";
	}

	LumaFunction l = new LumaFunction();

	public int blend(int colorA, int colorB) {
		double hue = ColorHelper.hue(colorA);
		double saturation = ColorHelper.saturation(colorA);
		double luma = l.calculate(colorB);

		return ColorHelper.hsv(hue, saturation, luma);

	}

}
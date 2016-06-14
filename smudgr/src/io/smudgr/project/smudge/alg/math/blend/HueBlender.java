package io.smudgr.project.smudge.alg.math.blend;

import io.smudgr.project.smudge.alg.math.ColorHelper;
import io.smudgr.project.smudge.alg.math.univariate.LumaFunction;

public class HueBlender implements Blender {

	LumaFunction l = new LumaFunction();

	public int blend(int colorA, int colorB) {
		// uses the hue from the top color and luma and chroma from bottom color
		double hue = ColorHelper.hue(colorA);
		double saturation = ColorHelper.saturation(colorB);
		double luma = l.calculate(colorB);

		return ColorHelper.hsv(hue, saturation, luma);

	}

	public String getName() {
		return "Hue";
	}

}

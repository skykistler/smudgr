package io.smudgr.engine.alg.math.blend;

import io.smudgr.engine.alg.math.ColorHelper;
import io.smudgr.engine.alg.math.univariate.LumaFunction;

/**
 * The {@link LumaBlender} preserves the hue and saturation of the first color,
 * but with the luma of the second color.
 */
public class LumaBlender implements Blender {

	@Override
	public String getName() {
		return "Luma";
	}

	LumaFunction l = new LumaFunction();

	@Override
	public int blend(int colorA, int colorB) {
		double hue = ColorHelper.hue(colorA);
		double saturation = ColorHelper.saturation(colorA);
		double luma = l.calculate(colorB);

		return ColorHelper.hsv(hue, saturation, luma);

	}

}

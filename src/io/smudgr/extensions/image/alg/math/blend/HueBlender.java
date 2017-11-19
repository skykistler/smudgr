package io.smudgr.extensions.image.alg.math.blend;

import io.smudgr.extensions.image.alg.math.ColorHelper;
import io.smudgr.extensions.image.alg.math.univariate.LumaFunction;

/**
 * The {@link HueBlender} preserves the hue of the first color,
 * but with the saturation and luma of the second color.
 */
public class HueBlender implements Blender {

	@Override
	public String getName() {
		return "Hue";
	}

	LumaFunction l = new LumaFunction();

	@Override
	public int blend(int colorA, int colorB) {
		double hue = ColorHelper.hue(colorA);
		double saturation = ColorHelper.saturation(colorB);
		double luma = l.calculate(colorB);

		return ColorHelper.hsv(hue, saturation, luma);

	}

}

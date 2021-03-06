package io.smudgr.extensions.image.alg.math.univariate;

import io.smudgr.extensions.image.alg.math.ColorHelper;

/**
 * Calculate the hue degrees of a given color
 */
public class HueFunction implements UnivariateFunction {

	@Override
	public String getName() {
		return "Hue";
	}

	@Override
	public double calculate(double value) {
		int color = (int) value;

		int red = ColorHelper.red(color);
		int blue = ColorHelper.blue(color);
		int green = ColorHelper.green(color);

		int max = Math.max(red, Math.max(blue, green));
		int min = Math.min(red, Math.min(blue, green));
		int c = max - min;

		double hue = 0.0;
		if (c == 0)
			return hue;
		else if (max == red)
			hue = Math.floorMod((green - blue) / c, 6);
		else if (max == green)
			hue = (blue - red) / c + 2;
		else if (max == blue)
			hue = (red - green) / c + 4;

		hue = hue * 60;
		return hue / 360.0;
	}

}

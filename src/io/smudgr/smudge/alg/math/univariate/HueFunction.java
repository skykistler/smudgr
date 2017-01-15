package io.smudgr.smudge.alg.math.univariate;

import io.smudgr.smudge.alg.math.ColorHelper;

public class HueFunction implements UnivariateFunction {

	public String getName() {
		return "Hue";
	}

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

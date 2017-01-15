package io.smudgr.smudge.alg.math.univariate;

import io.smudgr.smudge.alg.math.ColorHelper;

public class BrightnessFunction implements UnivariateFunction {

	public String getName() {
		return "Brightness";
	}

	public double calculate(double value) {
		int color = (int) value;

		int red = ColorHelper.red(color);
		int blue = ColorHelper.blue(color);
		int green = ColorHelper.green(color);

		double brightness = (red + green + blue) / 3;
		return brightness / 255;
	}

}

package io.smudgr.engine.alg.math.univariate;

import io.smudgr.engine.alg.math.ColorHelper;

/**
 * Calculate the numeric brightness (whiteness) of a given color
 */
public class BrightnessFunction implements UnivariateFunction {

	@Override
	public String getName() {
		return "Brightness";
	}

	@Override
	public double calculate(double value) {
		int color = (int) value;

		int red = ColorHelper.red(color);
		int blue = ColorHelper.blue(color);
		int green = ColorHelper.green(color);

		double brightness = (red + green + blue) / 3;
		return brightness / 255;
	}

}

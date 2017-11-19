package io.smudgr.extensions.image.alg.math.univariate;

import io.smudgr.extensions.image.alg.math.ColorHelper;

/**
 * Calculate the sin percentage of an integer color
 * <p>
 * sin returned as a 0-1 value
 */
public class SinFunction implements UnivariateFunction {

	@Override
	public String getName() {
		return "Sin";
	}

	@Override
	public double calculate(double value) {
		int color = (int) value;

		double red = Math.sin(ColorHelper.red(color) / Math.PI) + 1;
		double blue = Math.sin(ColorHelper.blue(color) / Math.PI) + 1;
		double green = Math.sin(ColorHelper.green(color) / Math.PI) + 1;

		return (red + green + blue) / 6;
	}

}

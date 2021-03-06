package io.smudgr.extensions.image.alg.math.univariate;

import io.smudgr.extensions.image.alg.math.ColorHelper;

/**
 * Calculate the perceived color (chroma) of an integer color
 * <p>
 * chroma returned as a 0-1 value
 */
public class ChromaFunction implements UnivariateFunction {

	@Override
	public String getName() {
		return "Chroma";
	}

	@Override
	public double calculate(double value) {
		int color = (int) value;

		double red = ColorHelper.red(color);
		double blue = ColorHelper.blue(color);
		double green = ColorHelper.green(color);

		// double numerator = (red * 255) + (blue * 255) + (green * 255);
		// double denominator = 441.673 * Math.sqrt( red * red + green * green +
		// blue * blue );
		double avg = (red + blue + green) / 3;
		double total = (Math.abs(red - avg) + Math.abs(green - avg) + Math.abs(blue - avg)) / 340.0;

		return total;

	}

}

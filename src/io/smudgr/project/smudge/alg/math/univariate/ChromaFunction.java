package io.smudgr.project.smudge.alg.math.univariate;

import io.smudgr.project.smudge.alg.math.ColorHelper;

public class ChromaFunction implements UnivariateFunction {

	public String getName() {
		return "Chroma";
	}

	/**
	 * Calculate the chroma percentage of an integer color
	 * 
	 * @param value
	 *            color in integer format
	 * @return chroma returned as a 0-1 value
	 */
	public double calculate(double value) {
		int color = (int) value;

		double red = ColorHelper.red(color);
		double blue = ColorHelper.blue(color);
		double green = ColorHelper.green(color);

		//double numerator = (red * 255) + (blue * 255) + (green * 255);
		//double denominator = 441.673 * Math.sqrt( red * red + green * green + blue * blue );
		double avg = (red + blue + green) / 3;
		double total = (Math.abs(red - avg) + Math.abs(green - avg) + Math.abs(blue - avg)) / 340.0;

		return total;

	}

}

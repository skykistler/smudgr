package io.smudgr.alg.math;

public class SinFunction implements UnivariateFunction {

	@Override
	/**
	 * Calculate the sin percentage of an integer color
	 * 
	 * @param value
	 *            color in integer format
	 * @return sin returned as a 0-1 value
	 */
	public double calculate(double value) {
		int color = (int) value;

		double red = Math.sin(ColorHelper.red(color) / Math.PI) + 1;
		double blue = Math.sin(ColorHelper.blue(color) / Math.PI) + 1;
		double green = Math.sin(ColorHelper.green(color) / Math.PI) + 1;

		return (red + green + blue) / 6;
	}

}

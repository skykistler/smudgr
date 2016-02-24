package io.smudgr.source.smudge.alg.math;

public class LumaFunction implements UnivariateFunction {

	@Override
	/**
	 * Calculate the luma percentage of an integer color
	 * 
	 * @param value
	 *            color in integer format
	 * @return luma returned as a 0-1 value
	 */
	public double calculate(double value) {
		int color = (int) value;

		int red = ColorHelper.red(color);
		int blue = ColorHelper.blue(color);
		int green = ColorHelper.green(color);

		return ((red * 3 + blue + green * 4) >> 3) / 255.0;
	}

	public double calculate_precise(double value) {
		int color = (int) value;

		int red = ColorHelper.red(color);
		int blue = ColorHelper.blue(color);
		int green = ColorHelper.green(color);

		return (.299 * red + .587 * green + .114 * blue) / 255.0;
	}

}

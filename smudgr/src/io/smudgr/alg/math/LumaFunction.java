package io.smudgr.alg.math;

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

		double red = ColorHelper.red(color);
		double blue = ColorHelper.blue(color);
		double green = ColorHelper.green(color);

		return (.299 * red + .587 * green + .114 * blue) / 255.0;
	}

}

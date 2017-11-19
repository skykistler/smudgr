package io.smudgr.extensions.image.alg.math.univariate;

import io.smudgr.extensions.image.alg.math.ColorHelper;

/**
 * Calculate the perceived brightness (luma) of an integer color
 * <p>
 * luma returned as a 0-1 value
 */
public class LumaFunction implements UnivariateFunction {

	@Override
	public String getName() {
		return "Luma";
	}

	@Override
	public double calculate(double value) {
		int color = (int) value;

		int red = ColorHelper.red(color);
		int blue = ColorHelper.blue(color);
		int green = ColorHelper.green(color);

		return ((red * 3 + blue + green * 4) >> 3) / 255.0;
	}

	/**
	 * Tester method that provides a more precise value but slower calculation
	 *
	 * @param value
	 *            color
	 * @return luma
	 */
	public double calculate_precise(double value) {
		int color = (int) value;

		int red = ColorHelper.red(color);
		int blue = ColorHelper.blue(color);
		int green = ColorHelper.green(color);

		return (.299 * red + .587 * green + .114 * blue) / 255.0;
	}

}

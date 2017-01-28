package io.smudgr.engine.alg.math.univariate;

import io.smudgr.engine.alg.math.ColorHelper;

/**
 * Calculate the log percentage of an integer color
 * <p>
 * log returned as a 0-1 value
 */
public class LogFunction implements UnivariateFunction {

	@Override
	public String getName() {
		return "Log";
	}

	@Override
	public double calculate(double value) {
		int color = (int) value;

		double red = Math.log(ColorHelper.red(color) / 255.0);
		double blue = Math.log(ColorHelper.blue(color) / 255.0);
		double green = Math.log(ColorHelper.green(color) / 255.0);

		return Math.min(1, Math.max(0, (.299 * red + .587 * green + .114 * blue) + 4));
	}

}

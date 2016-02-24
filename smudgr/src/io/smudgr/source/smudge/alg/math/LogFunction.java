package io.smudgr.source.smudge.alg.math;

public class LogFunction implements UnivariateFunction {

	@Override
	/**
	 * Calculate the log percentage of an integer color
	 * 
	 * @param value
	 *            color in integer format
	 * @return log returned as a 0-1 value
	 */
	public double calculate(double value) {
		int color = (int) value;

		double red = Math.log(ColorHelper.red(color) / 255.0);
		double blue = Math.log(ColorHelper.blue(color) / 255.0);
		double green = Math.log(ColorHelper.green(color) / 255.0);

		return Math.min(1, Math.max(0, (.299 * red + .587 * green + .114 * blue) + 4));
	}

}

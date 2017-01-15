package io.smudgr.engine.alg.math.lerp;

public interface Interpolator {
	/**
	 * @param a
	 *            first point
	 * @param b
	 *            second point
	 * @param a1
	 *            the point before a
	 * @param b1
	 *            the point after b
	 * @param bias
	 *            the percentage of how close to interpolate to point b
	 * @return
	 */
	public double interpolate(double a, double b, double a1, double b1, double bias);
}

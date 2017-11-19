package io.smudgr.extensions.image.alg.math.lerp;

/**
 * The {@link Interpolator} interface provides an interface for implementing
 * various interpolation functions.
 * <p>
 * This should be refactored soon.
 */
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
	 * @return interpolated {@code double} value
	 */
	public double interpolate(double a, double b, double a1, double b1, double bias);
}

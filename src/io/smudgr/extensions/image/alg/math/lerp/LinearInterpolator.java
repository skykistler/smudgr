package io.smudgr.extensions.image.alg.math.lerp;

/**
 * Linear interpolation assumes a straight line of constant slope between two
 * points.
 */
public class LinearInterpolator implements Interpolator {

	@Override
	public double interpolate(double a, double b, double a1, double b1, double bias) {
		return a * (1 - bias) + b * bias;
	}

}

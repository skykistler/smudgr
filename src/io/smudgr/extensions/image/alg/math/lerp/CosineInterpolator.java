package io.smudgr.extensions.image.alg.math.lerp;

/**
 * Cosine interpolation assumes a smooth curve between two points, with deep
 * peaks and valleys when there are sharp changes in slope.
 */
public class CosineInterpolator implements Interpolator {

	@Override
	public double interpolate(double a, double b, double a1, double b1, double bias) {
		double ft = bias * Math.PI;
		double f = (1 - Math.cos(ft)) * .5;

		return a * (1 - f) + b * f;
	}

}

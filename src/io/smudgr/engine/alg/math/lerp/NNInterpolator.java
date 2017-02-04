package io.smudgr.engine.alg.math.lerp;

/**
 * Nearest Neighbor interpolation simply returns the point closest to the bias.
 * <p>
 * A bias less than or equal to .5 will return point a, otherwise point b
 */
public class NNInterpolator implements Interpolator {

	@Override
	public double interpolate(double a, double b, double a1, double b1, double bias) {
		if (bias <= .5)
			return a;
		if (bias > .5)
			return b;

		return a;
	}

}

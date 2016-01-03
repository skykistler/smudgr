package io.smudgr.alg.math;

public class LinearInterpolator implements Interpolator {

	@Override
	public double interpolate(double a, double b, double a1, double b1, double bias) {
		return a * (1 - bias) + b * bias;
	}

}

package io.smudgr.project.smudge.alg.math;

public class CubicInterpolator implements Interpolator {

	@Override
	public double interpolate(double a, double b, double a1, double b1, double bias) {
		double P = (b1 - b) - (a1 - a);
		double Q = (a1 - a) - P;
		double R = b - a1;
		double S = a;

		return P * Math.pow(bias, 3) + Q * Math.pow(bias, 2) + R * bias + S;
	}

}

package io.smudgr.project.smudge.alg.math.univariate;

public class BezierFunction implements UnivariateFunction {

	@Override
	public String getName() {
		return "Bezier Function";
	}

	@Override
	public double calculate(double value) {
		double ts = value * value;
		double tc = ts * value;

		return 1 - (tc * ts + -5 * ts * ts + 10 * tc + -10 * ts + 5 * value);
	}

}

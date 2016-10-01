package io.smudgr.project.smudge.alg.math.lerp;

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

package io.smudgr.engine.alg.math.univariate;

/**
 * Always returns 1
 */
public class LinearFunction implements UnivariateFunction {

	@Override
	public String getName() {
		return "Linear";
	}

	@Override
	public double calculate(double value) {
		return 1;
	}

}

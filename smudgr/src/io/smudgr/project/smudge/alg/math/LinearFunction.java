package io.smudgr.project.smudge.alg.math;

import io.smudgr.project.smudge.alg.math.univariate.UnivariateFunction;

public class LinearFunction implements UnivariateFunction {

	public String getName() {
		return "Linear";
	}

	public double calculate(double value) {
		return 1;
	}

}

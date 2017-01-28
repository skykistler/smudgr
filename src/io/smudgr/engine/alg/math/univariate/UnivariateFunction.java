package io.smudgr.engine.alg.math.univariate;

/**
 * TODO: Refactor
 * <p>
 * The {@link UnivariateFunction} interface defines a single method for
 * returning the {@code double} result of an implemented function given an
 * arbitrary {@code double} value
 */
public interface UnivariateFunction {
	/**
	 * Gets the identifier of this {@link UnivariateFunction}
	 *
	 * @return {@code String}
	 */
	public String getName();

	/**
	 * Calculate the result of a function given an arbitrary value
	 *
	 * @param value
	 *            {@code double}
	 * @return {@code double} result
	 */
	public double calculate(double value);
}

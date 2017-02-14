package io.smudgr.engine.param;

import java.util.ArrayList;

import io.smudgr.engine.alg.math.univariate.UnivariateFunction;

/**
 * The {@link UnivariateParameter} class is a {@link Parameter} implementation
 * for switching between {@link UnivariateFunction} functions that take a single
 * numeric variable, and
 * return a single numeric value.
 * <p>
 * {@link UnivariateParameter} instances can be used to switch between
 * manipulation functions.
 *
 * @see UnivariateFunction
 */
public class UnivariateParameter extends Parameter {

	@Override
	public String getTypeName() {
		return "Function";
	}

	@Override
	public String getTypeIdentifier() {
		return "function";
	}

	private ArrayList<UnivariateFunction> univariates = new ArrayList<UnivariateFunction>();

	private int current;

	/**
	 * Instantiate a totally empty {@link UnivariateParameter}, for reflection.
	 */
	public UnivariateParameter() {
		super();
	}

	/**
	 * Instantiate a new {@link UnivariateParameter} and set an initial value.
	 *
	 * @param name
	 *            Identifying name
	 * @param parent
	 *            {@link Parametric} parent
	 * @param initial
	 *            {@link UnivariateFunction} initial function
	 * @see UnivariateParameter#UnivariateParameter(String, Parametric) new
	 *      UnivariateFunction(name, parent)
	 */
	public UnivariateParameter(String name, Parametric parent, UnivariateFunction initial) {
		this(name, parent);
		setValue(initial);
	}

	/**
	 * Instantiate a new {@link UnivariateParameter}
	 *
	 * @param name
	 *            Identifying name
	 * @param parent
	 *            {@link Parametric} parent
	 * @see UnivariateParameter#UnivariateParameter(String, Parametric) new
	 *      UnivariateFunction(name, parent)
	 */
	public UnivariateParameter(String name, Parametric parent) {
		super(name, parent);
	}

	@Override
	protected void setValueFromObject(Object o) {
		UnivariateFunction func = null;

		if (o instanceof UnivariateFunction)
			func = (UnivariateFunction) o;

		if (o instanceof String) {
			for (UnivariateFunction uf : univariates) {
				if (uf.getName().equals(o)) {
					func = uf;
					break;
				}
			}
		}

		if (func == null)
			return;

		add(func);

		setCurrent(univariates.indexOf(func));
	}

	private void setCurrent(int curr) {
		int prev = current;
		current = curr;

		if (prev != current)
			getParent().triggerChange();
	}

	@Override
	public String getStringValue() {
		if (univariates.size() == 0)
			return "";

		return univariates.get(current).getName();
	}

	/**
	 * Gets the current {@link UnivariateFunction} this parameter is set to, or
	 * {@code null} if no {@link UnivariateFunction} instances have been added
	 * to this parameter.
	 *
	 * @return current {@link UnivariateFunction} this parameter is set to, or
	 *         {@code null}
	 * @see UnivariateParameter#add(UnivariateFunction)
	 */
	public UnivariateFunction getValue() {
		if (univariates.size() == 0)
			return null;

		return univariates.get(current);
	}

	/**
	 * Add a possible {@link UnivariateFunction} value to this parameter.
	 *
	 * @param func
	 *            {@link UnivariateFunction} to add.
	 *
	 * @see UnivariateParameter#getValue()
	 */
	public void add(UnivariateFunction func) {
		if (!univariates.contains(func))
			univariates.add(func);
	}

	@Override
	public void inputValue(int value) {
		value %= univariates.size();

		setCurrent(value);
	}

	@Override
	public void inputOn() {

	}

	@Override
	public void inputOff() {
	}

	@Override
	public void increment() {
		if (univariates.size() == 0)
			return;

		current++;
		current %= univariates.size();
	}

	@Override
	public void decrement() {
		if (univariates.size() == 0)
			return;

		current--;
		if (current < 0)
			current += univariates.size();
	}

}

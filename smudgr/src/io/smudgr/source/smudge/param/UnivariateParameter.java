package io.smudgr.source.smudge.param;

import java.util.ArrayList;

import io.smudgr.source.smudge.alg.math.UnivariateFunction;

public class UnivariateParameter extends Parameter {

	private ArrayList<UnivariateFunction> univariates = new ArrayList<UnivariateFunction>();

	private UnivariateFunction initial;
	private int current;

	public UnivariateParameter(String name, Parametric parent) {
		super(name, parent);
	}

	public void init() {
		setValue(initial);
	}

	public void setInitial(Object o) {
		if (!(o instanceof UnivariateFunction))
			return;

		UnivariateFunction func = (UnivariateFunction) o;
		if (!univariates.contains(func))
			univariates.add(func);

		initial = func;
	}

	public void setValue(Object o) {
		if (!(o instanceof UnivariateFunction))
			return;

		UnivariateFunction func = (UnivariateFunction) o;
		if (!univariates.contains(func))
			univariates.add(func);

		current = univariates.indexOf(func);
	}

	public void inputValue(int value) {
		// TODO do we need this
	}

	public void inputOn(int value) {
		// TODO do we need this

	}

	public void inputOff(int value) {
		// TODO do we need this
	}

	public void increment() {
		if (univariates.size() == 0)
			return;

		current++;
		current %= univariates.size();
	}

	public void decrement() {
		if (univariates.size() == 0)
			return;

		current--;
		if (current < 0)
			current += univariates.size();
	}

}

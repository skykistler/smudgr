package io.smudgr.project.smudge.param;

import java.util.ArrayList;

import io.smudgr.project.smudge.alg.math.univariate.UnivariateFunction;

public class UnivariateParameter extends Parameter {
	
	public String getType() {
		return "Univariate";
	}

	private ArrayList<UnivariateFunction> univariates = new ArrayList<UnivariateFunction>();

	private int current;

	public UnivariateParameter(String name, Parametric parent, UnivariateFunction initial) {
		this(name, parent);
		setValue(initial);
	}

	public UnivariateParameter(String name, Parametric parent) {
		super(name, parent);
	}

	public void update() {
	}

	public void setValue(Object o) {
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

	public String getStringValue() {
		if (univariates.size() == 0)
			return "";

		return univariates.get(current).getName();
	}

	public UnivariateFunction getValue() {
		if (univariates.size() == 0)
			return null;

		return univariates.get(current);
	}

	public void add(UnivariateFunction func) {
		if (!univariates.contains(func))
			univariates.add(func);
	}

	public void inputValue(int value) {
		value %= univariates.size();

		setCurrent(value);
	}

	public void inputOn() {

	}

	public void inputOff() {
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

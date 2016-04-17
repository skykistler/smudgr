package io.smudgr.project.smudge.param;

import java.util.ArrayList;

import io.smudgr.project.smudge.alg.math.UnivariateFunction;

public class UnivariateParameter extends Parameter {

	private ArrayList<UnivariateFunction> univariates = new ArrayList<UnivariateFunction>();

	private Object initial;
	private int current;

	public UnivariateParameter(String name, Parametric parent, UnivariateFunction initial) {
		this(name, parent);
		setInitial(initial);
	}

	public UnivariateParameter(String name, Parametric parent) {
		super(name, parent);
	}

	public void init() {
		setValue(initial);
	}

	public void update() {
	}

	public void setInitial(Object o) {
		initial = o;
	}

	public void setValue(Object o) {
		UnivariateFunction func = null;

		if (o instanceof UnivariateFunction)
			func = (UnivariateFunction) o;

		if (o instanceof String) {
			for (UnivariateFunction uf : univariates) {
				if (func.getName().equals(o)) {
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

	public void inputOn(int value) {

	}

	public void inputOff(int value) {
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

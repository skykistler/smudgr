package io.smudgr.source.smudge.param;

import java.util.ArrayList;

import io.smudgr.source.smudge.alg.math.UnivariateFunction;

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

	public void setInitial(Object o) {
		initial = o;
	}

	public void setValue(Object o) {
		UnivariateFunction func = null;

		if (o instanceof UnivariateFunction)
			func = (UnivariateFunction) o;
		else {
			String className = o.toString();
			try {
				Class<?> c = Class.forName(className);
				func = (UnivariateFunction) c.newInstance();
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				System.out.println("Unable to find univariate: " + o);
			}
		}

		if (func == null)
			return;

		add(func);
		current = univariates.indexOf(func);
	}

	public void setCurrent(int curr) {
		int prev = current;
		current = curr;

		if (prev != current)
			getParent().triggerChange();
	}

	public String getStringValue() {
		if (univariates.size() == 0)
			return "";

		return univariates.get(current).getClass().getCanonicalName();
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
		// TODO how should we implement this
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

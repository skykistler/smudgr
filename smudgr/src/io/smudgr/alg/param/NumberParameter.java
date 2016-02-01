package io.smudgr.alg.param;

import io.smudgr.alg.Algorithm;

public class NumberParameter extends Parameter {
	private double initial;
	private double value;
	private double min;
	private double max;
	private double step;

	public NumberParameter(Algorithm parent, String name) {
		this(parent, name, 0);
	}

	public NumberParameter(Algorithm parent, String name, double initial) {
		this(parent, name, initial, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1);
	}

	public NumberParameter(Algorithm parent, String name, double initial, double minimum, double maximum) {
		this(parent, name, initial, minimum, maximum, (maximum - minimum) / 127);
	}

	public NumberParameter(Algorithm parent, String name, double initial, double minimum, double maximum, double step) {
		super(parent, name);
		setInitial(initial);
		min = minimum;
		max = maximum;
		setStep(step);
	}

	public void init() {
		setValue(initial);
	}

	public void setInitial(Object o) {
		if (o instanceof Double)
			initial = (double) o;
		else
			initial = Double.parseDouble(o.toString());
	}

	public void setValue(Object o) {
		if (o instanceof Double)
			value = (double) o;
		else
			value = Double.parseDouble(o.toString());

		enforce();
	}

	public void setMin(double m) {
		if (m >= min)
			return;

		double ratio = (value - min) / (max - min);
		min = m;
		value = ratio * (max - min) + min;
	}

	public double getMin() {
		return min;
	}

	public void setMax(double m) {
		if (m <= min)
			return;
		double ratio = (value - min) / (max - min);
		max = m;
		value = ratio * (max - min) + min;
	}

	public double getMax() {
		return max;
	}

	public void setStep(double s) {
		s = s > max - min ? max - min : s;
		s = s < 0 ? 0 : s;
		step = s;
	}

	public double getStep() {
		return step;
	}

	public double getValue() {
		return value;
	}

	public int getIntValue() {
		return (int) value;
	}

	public void inputValue(int midi) {
		double m;
		if (reverse)
			m = (127 - midi) / 127.0;
		else
			m = midi / 127.0;

		setValue(m * (max - min));
	}

	public void inputOn(int value) {
		setValue(reverse ? min : max);
	}

	public void inputOff(int value) {
		setValue(reverse ? max : min);
	}

	public void increment() {
		if (reverse)
			dec();
		else
			inc();

		enforce();
	}

	public void decrement() {
		if (reverse) {
			inc();
		} else
			dec();

		enforce();
	}

	private void inc() {
		value += step;
	}

	private void dec() {
		value -= step;
	}

	private void enforce() {
		if (value < min) {
			if (continuous)
				value += (max - min);
			else
				value = min;
		}

		if (value > max) {
			if (continuous)
				value -= (max - min);
			else
				value = max;
		}
	}

}

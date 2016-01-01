package io.smudgr.alg.param;

import io.smudgr.alg.Algorithm;

public class DoubleParameter extends Parameter {
	private double value;
	private double min;
	private double max;
	private double step;

	public DoubleParameter(Algorithm parent, String name) {
		this(parent, name, 0);
	}

	public DoubleParameter(Algorithm parent, String name, double initial) {
		this(parent, name, initial, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1);
	}

	public DoubleParameter(Algorithm parent, String name, double initial, double minimum, double maximum) {
		this(parent, name, initial, minimum, maximum, (maximum - minimum) / 127);
	}

	public DoubleParameter(Algorithm parent, String name, double initial, double minimum, double maximum, double step) {
		super(parent, name);
		value = initial;
		min = minimum;
		max = maximum;
		this.step = step;
	}

	public void setValue(Object o) {
		if (o instanceof Integer)
			value = (double) (int) o;
		else
			value = (double) o;
		enforce();
	}

	public double getValue() {
		return value;
	}

	public void midiValue(int midi) {
		double m;
		if (reverse)
			m = (127 - midi) / 127.0;
		else
			m = midi / 127.0;

		setValue(m * (max - min));
	}

	public void noteOn(int note) {
		setValue(reverse ? min : max);
	}

	public void noteOff(int note) {
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
		if (continuous && value > max)
			value = min + (max - value);
	}

	private void dec() {
		value -= step;
		if (continuous && value < min)
			value = max - (min - value);
	}

	private void enforce() {
		value = value < min ? min : value;
		value = value > max ? max : value;
	}

}

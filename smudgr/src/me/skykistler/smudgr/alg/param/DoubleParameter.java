package me.skykistler.smudgr.alg.param;

import me.skykistler.smudgr.alg.Algorithm;

public class DoubleParameter extends Parameter {
	private double value;
	private double min;
	private double max;
	private double step;
	private boolean continuous = false;
	private boolean reverse = false;

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

	public void midiSet(int midi) {
		double m;
		if (reverse)
			m = (127 - midi) / 127.0;
		else
			m = midi / 127.0;

		setValue(m * (max - min));
	}

	public void setValue(double val) {
		value = val;
		enforce();
	}

	public double getValue() {
		return value;
	}

	public void setContinuous(boolean cont) {
		continuous = cont;
	}

	public void setReverse(boolean rev) {
		reverse = rev;
	}

	public void increment() {
		value += step;
		if (continuous && value > max)
			value = min + (max - value);

		enforce();
	}

	public void decrement() {
		value -= step;
		if (continuous && value < min)
			value = max - (min - value);

		enforce();
	}

	private void enforce() {
		value = value < min ? min : value;
		value = value > max ? max : value;
	}

}

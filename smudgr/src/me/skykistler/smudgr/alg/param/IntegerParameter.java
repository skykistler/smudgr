package me.skykistler.smudgr.alg.param;

import me.skykistler.smudgr.alg.Algorithm;

public class IntegerParameter extends Parameter {
	private int value;
	private int min;
	private int max;
	private int step;
	private boolean continuous = false;
	private boolean reverse = false;

	public IntegerParameter(Algorithm parent, String name) {
		this(parent, name, 0);
	}

	public IntegerParameter(Algorithm parent, String name, int initial) {
		this(parent, name, initial, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
	}

	public IntegerParameter(Algorithm parent, String name, int initial, int minimum, int maximum) {
		this(parent, name, initial, minimum, maximum, 1);
	}

	public IntegerParameter(Algorithm parent, String name, int initial, int minimum, int maximum, int step) {
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

		setValue((int) (m * (max - min)));
	}

	public void setValue(int val) {
		value = val;
		enforce();
	}

	public int getValue() {
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

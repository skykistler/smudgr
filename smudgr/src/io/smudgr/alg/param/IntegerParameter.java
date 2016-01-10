package io.smudgr.alg.param;

import io.smudgr.alg.Algorithm;

public class IntegerParameter extends Parameter {
	private int initial;
	private int value;
	private int min;
	private int max;
	private int step;

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
		setInitial(initial);
		min = minimum;
		max = maximum;
		this.step = step;
	}

	public void init() {
		setValue(initial);
	}

	public void setInitial(Object o) {
		initial = (int) o;
	}

	public void setValue(Object o) {
		value = (int) o;
		enforce();
	}

	public int getValue() {
		return value;
	}

	public void inputValue(int value) {
		double m;
		if (reverse)
			m = (127 - value) / 127.0;
		else
			m = value / 127.0;

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
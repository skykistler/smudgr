package io.smudgr.alg.param;

import io.smudgr.alg.Algorithm;

public class BooleanParameter extends Parameter {
	private boolean initial;
	private boolean value;

	public BooleanParameter(Algorithm parent, String name) {
		this(parent, name, false);
	}

	public BooleanParameter(Algorithm parent, String name, boolean initial) {
		super(parent, name);
		this.initial = initial;
	}

	public void init() {
		setValue(initial);
	}

	public void setValue(Object o) {
		value = (boolean) o;
	}

	public boolean getValue() {
		return value;
	}

	public void inputValue(int midi) {
		setValue(midi > 0);
	}

	public void inputOn(int value) {
		if (reverse)
			setValue(false);
		else
			setValue(true);
	}

	public void inputOff(int value) {
		if (reverse)
			setValue(true);
		else
			setValue(false);
	}

	public void increment() {
		value = !value;
	}

	public void decrement() {
		increment();
	}

}

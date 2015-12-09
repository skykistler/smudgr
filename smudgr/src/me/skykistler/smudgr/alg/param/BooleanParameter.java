package me.skykistler.smudgr.alg.param;

import me.skykistler.smudgr.alg.Algorithm;

public class BooleanParameter extends Parameter {
	private boolean value;

	public BooleanParameter(Algorithm parent, String name) {
		this(parent, name, false);
	}

	public BooleanParameter(Algorithm parent, String name, boolean initial) {
		super(parent, name);
		value = initial;
	}

	public void setValue(Object o) {
		value = (boolean) o;
	}

	public void midiValue(int midi) {
		setValue(midi > 0);
	}

	public boolean getValue() {
		return value;
	}

	public void setReverse(boolean rev) {

	}

	public void increment() {
		value = !value;
	}

	public void decrement() {
		increment();
	}

}

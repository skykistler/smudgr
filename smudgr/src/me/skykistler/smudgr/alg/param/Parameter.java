package me.skykistler.smudgr.alg.param;

import me.skykistler.smudgr.alg.Algorithm;

public abstract class Parameter {
	private Algorithm alg;
	private String name;
	private boolean bindRequested = false;

	public Parameter(Algorithm parent, String name) {
		alg = parent;
		this.name = name;

		alg.addParameter(this);
	}

	public Algorithm getParent() {
		return alg;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return getName();
	}

	public abstract void setValue(Object o);

	public abstract void midiValue(int midiValue);

	public abstract void increment();

	public abstract void decrement();

	public abstract void setReverse(boolean rev);

	public void requestBind() {
		bindRequested = true;
	}

	public boolean isBindRequested() {
		return bindRequested;
	}
}
